import model.Game;
import model.Question;
import model.Response;
import model.User;
import repository.*;
import service.Service;
import services.IServices;
import utils.AbstractServer;
import utils.RpcConcurrentServer;
import utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {

        // Server properties (port)
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException var21) {
            System.err.println("Cannot find server.properties " + var21);
            return;
        }
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong  Port Number" + ex.getMessage());
            System.err.println("Using default port " + defaultPort);
        }


        // Initialize repositories
        IUserRepository userRepository = new UserDBRepository();
        IGameDBRepository gameDBRepository = new GameDBRepository();
        IQuestionRepository questionRepository = new QuestionDBRepository();
        IResponseRepository responseRepository = new ResponseDBRepository();

        // Add / Show data
        //addData(userRepository, gameDBRepository, questionRepository, responseRepository);
        showData(userRepository);

        // Initialize service
        IServices service=new Service(userRepository, gameDBRepository,
                questionRepository, responseRepository);

        // Start server
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try {
            server.start();
        } catch (ServerException ex) {
            System.err.println("Error starting the server" + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException ex) {
                System.err.println("Error stopping server " + ex.getMessage());
            }

        }
    }

    private static void addData(IUserRepository userRepository, IGameDBRepository gameDBRepository,
                                IQuestionRepository questionRepository, IResponseRepository responseRepository) {
//        System.out.println("SERVER -> Adding data");
//        userRepository.add(new User("sam", "sam"));
//        userRepository.add(new User("a", "a"));
//        userRepository.add(new User("b", "b"));

        System.out.println("SERVER -> Adding Questions");
        Question q1 = new Question("What is the capital of Romania?", "A", 1);
        Question q2 = new Question("What is the capital of France?", "A", 1);
        Question q3 = new Question("What is the capital of Germany?", "A", 1);
        Question q4 = new Question("What is the capital of Spain?", "B", 2);
        Question q5 = new Question("What is the capital of Italy?", "B", 2);
        Question q6 = new Question("What is the capital of Greece?", "B", 2);
        Question q7 = new Question("What is the capital of Poland?", "C", 3);
        Question q8 = new Question("What is the capital of Hungary?", "C", 3);
        Question q9 = new Question("What is the capital of Bulgaria?", "C", 3);
        Question q10 = new Question("What is the capital of Austria?", "D", 4);
        Question q11 = new Question("What is the capital of Belgium?", "D", 4);
        Question q12 = new Question("What is the capital of Croatia?", "D", 4);

        questionRepository.add(q1);
        questionRepository.add(q2);
        questionRepository.add(q3);
        questionRepository.add(q4);
        questionRepository.add(q5);
        questionRepository.add(q6);
        questionRepository.add(q7);
        questionRepository.add(q8);
        questionRepository.add(q9);
        questionRepository.add(q10);
        questionRepository.add(q11);
        questionRepository.add(q12);

        System.out.println("SERVER -> Adding Game");
        Game game = new Game();
        User player = null;
        try {
            player = userRepository.findUserByUsername("sam");
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.setPlayer(player);

        Response r1 = new Response("A", 1);
        Response r2 = new Response("B", 10);
        Response r3 = new Response("C", 100);
        Response r4 = new Response("D", 1000);

        responseRepository.add(r1);
        responseRepository.add(r2);
        responseRepository.add(r3);
        responseRepository.add(r4);

        game.addAnswer(q1, r1);
        game.addAnswer(q2, r2);
        game.addAnswer(q3, r3);
        game.addAnswer(q4, r4);

        game.setState("Finished");
        gameDBRepository.add(game);

        System.out.println("SERVER -> Done adding data");

        System.out.println("Games: " + gameDBRepository.getAll());

    }

    private static void showData(IUserRepository userRepository) {
        System.out.println("SERVER -> Showing data");
        System.out.println("Users:");
        for (User user : userRepository.getAll()) {
            System.out.println(user);
        }
        System.out.println("Done printing users");

    }


}
