package service;

import dto.*;
import exception.RepositoryException;
import model.Game;
import model.Question;
import model.Response;
import model.User;
import repository.*;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.pow;

public class Service implements IServices {

    private IUserRepository userRepository;
    private IGameDBRepository gameDBRepository;
    private IQuestionRepository questionRepository;
    private IResponseRepository responseRepository;
    private Map<Long, IObserver> loggedClients;
    private Map<Long, Long> games; // clientid gameid
    private final int defaultThreadsNo = 5;

    public Service(IUserRepository userRepository, IGameDBRepository gameDBRepository,
                   IQuestionRepository questionRepository, IResponseRepository responseRepository) {
        this.userRepository = userRepository;
        this.gameDBRepository = gameDBRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.loggedClients = new ConcurrentHashMap<>();
        this.games = new HashMap<>();
    }

    public synchronized GameDTO checkLogIn(User user, IObserver client) throws ServiceException {
        //find user
        User userToFind;
        System.out.println("SERVER -> checkLogIn -> " + user);
        try {
            userToFind = userRepository.findUserByUsername(user.getUsername());
            System.out.println("NO REPO EXCEPTION");
        } catch (RepositoryException re) {
            System.out.println("REPO EXCEPTION");
            throw new ServiceException(re.getMessage());
        }
        // check if user is already logged in
        if (loggedClients.containsKey(userToFind.getId())) {
            throw new ServiceException("User already logged in.");
        }
        user.setId(userToFind.getId());
        loggedClients.put(user.getId(), client);

        // make new Game
        Game game = new Game();
        game.setPlayer(user);
        game = gameDBRepository.add(game);

        // link game to user
        games.put(user.getId(), game.getId());

        // data to be sent to user
        GameDTO gameDTO = new GameDTO();
        gameDTO.setUser(user);
        gameDTO.setQuestion(getRandomQuestion(1));

        // ListItemsDTO
        ListItemsDTO listItemsDTO = new ListItemsDTO();

        for (var g : gameDBRepository.getAll()) {
            ListItemDTO item = new ListItemDTO();
            item.setGame(g);
            listItemsDTO.addItem(item);
        }

        gameDTO.setItems(listItemsDTO);

        return gameDTO;

    }

    public Question getRandomQuestion(int dif) {
        List<Question> questions = (List<Question>) questionRepository.getAll();
        List<Question> questionsByDf = new ArrayList<>();
        for (var q : questions) {
            if (q.getDifficulty() == dif) {
                questionsByDf.add(q);
            }
        }
        Random rand = new Random();
        int index = rand.nextInt(questionsByDf.size());
        return questionsByDf.get(index);
    }


    @Override
    public synchronized void logout(User user) throws ServiceException {
        System.out.println("SERVER -> logout");
        if (loggedClients.containsKey(user.getId())) {
            loggedClients.remove(user.getId());
        } else{
            throw new ServiceException("User not logged in");
        }
    }

    // gets data from repository in list form
    public synchronized Iterable<Game> getListData() throws ServiceException {
        return gameDBRepository.getAll();
    }

    // encapsulates data in DTO
    public synchronized ListItemsDTO getData(User user) throws ServiceException {
        System.out.println("SERVER -> getData");
        ListItemsDTO listItemsDTO = new ListItemsDTO();
        for (var item : getListData()) {
            ListItemDTO listItemDTO = new ListItemDTO();
            listItemDTO.setGame(item);
            listItemsDTO.addItem(listItemDTO);
        }
        return listItemsDTO;
    }

    public synchronized GameDTO madeAction(ActionDTO action) throws ServiceException {
        System.out.println("SERVER -> madeAction");

        // Do smth for action

        Question question = action.getQuestion();
        Response response = action.getResponse();

        //find game
        Game currentGame = null;
        try {
            currentGame = gameDBRepository.findById(games.get(action.getUser().getId()));
        } catch (RepositoryException re) {
            throw new ServiceException(re.getMessage());
        }

        // add question to nr of answered questions
        if (question.getDifficulty() == 1) {
            currentGame.setNoOfAnswersQ1(currentGame.getNoOfAnswersQ1() + 1);
        } else if (question.getDifficulty() == 2) {
            currentGame.setNoOfAnswersQ2(currentGame.getNoOfAnswersQ2() + 1);
        } else if (question.getDifficulty() == 3) {
            currentGame.setNoOfAnswersQ3(currentGame.getNoOfAnswersQ3() + 1);
        } else if (question.getDifficulty() == 4) {
            currentGame.setNoOfAnswersQ4(currentGame.getNoOfAnswersQ4() + 1);
        }


        GameDTO gameDTO = new GameDTO();
        // check if response conrect
        System.out.println("_______________________________________");
        System.out.println("qa: " + question.getAnswer() + " resp" + response.getValue());
        if (Objects.equals(question.getAnswer(), response.getValue())) {

            response.setNoOfPoints((int) pow(10, question.getDifficulty()-1));

            //todo make sure he doesnt finish the game
            //get new q
            System.out.println("new dif: " + question.getDifficulty() + 1);
            gameDTO.setQuestion(getRandomQuestion(question.getDifficulty() + 1));
            gameDTO.setAnswer(true);
            gameDTO.setNoPoints(response.getNoOfPoints());
        } else {
            gameDTO.setNoPoints(0);
            gameDTO.setAnswer(false);
            //get new q
            System.out.println("new dif: " + question.getDifficulty());
            gameDTO.setQuestion(getRandomQuestion(question.getDifficulty()));
        }

        // add question & response & nr pooints to map
        responseRepository.add(response);
        currentGame.addAnswer(question, response);

        // send new question & nr of points of prev question
        return gameDTO;

        // Update other users
//        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
//        for (var client : loggedClients.entrySet()) {
//            System.out.println("SERVER -> madeAction -> client: " + client);
//
//            executor.execute(() -> {
//                try {
//                    UpdateDTO updateDTO = new UpdateDTO();
//                    updateDTO.setEntities(getData(userRepository.findById(client.getKey())));
//                    client.getValue().update(updateDTO);
//                } catch (ServiceException | RepositoryException e) {
//                    e.printStackTrace();
//                }
//            });
//
//        }
    }




}
