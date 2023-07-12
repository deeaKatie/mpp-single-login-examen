package controller;

import com.sun.javafx.util.TempState;
import dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Game;
import model.Response;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.io.IOException;


public class PlayController implements IObserver {
    ObservableList<ListItemDTO> modelLeft = FXCollections.observableArrayList();
    private IServices service;
    private User loggedUser;
    @FXML
    Label usernameLabel;
    @FXML
    Label statusLabel;
    @FXML
    Button logOutButton;
    @FXML
    ListView<ListItemDTO> leftListView;
    @FXML
    Label leftLabel;
    @FXML
    Label rightLabel;
    @FXML
    Label questionLabel;
    @FXML
    TextField reponseTextField;

    private GameDTO game;
    public void setInitGame(GameDTO gameDTO) {
        this.game = gameDTO;
    }

    public void setService(IServices service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    public void initVisuals() {
        usernameLabel.setText("Hi, " + loggedUser.getUsername());
        statusLabel.setVisible(false);
        questionLabel.setVisible(false);
        reponseTextField.setVisible(false);

        ListItemsDTO items = game.getItems();
        initModel(items);
        setQuestionLabel();
    }


    public void initModel(ListItemsDTO items) {
        modelLeft.setAll(items.getItems());
        leftListView.setItems(modelLeft);
    }

    public void setQuestionLabel() {
        questionLabel.setVisible(true);
        reponseTextField.setVisible(true);
        questionLabel.setText(game.getQuestion().getText());
    }

    @FXML
    public void logOutHandler() throws IOException {
        System.out.println("Logging out!\n");
        try {
            service.logout(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error logging out", ex.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LogInView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) logOutButton.getScene().getWindow();
        LogInController logCtrl = fxmlLoader.getController();

        logCtrl.setService(service);
        stage.setScene(scene);
    }

    @Override
    public void update(UpdateDTO updateDTO) {
        initModel(updateDTO.getEntities());
    }

    public void makeAction(ActionEvent actionEvent) {

        String qText = questionLabel.getText();
        if (qText.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error making action", "No question answer!");
            return;
        }

        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setUser(loggedUser);
        actionDTO.setQuestion(game.getQuestion());
        Response response = new Response();
        response.setValue(reponseTextField.getText());
        actionDTO.setResponse(response);

        GameDTO gamed = null;
        try {
            gamed = service.madeAction(actionDTO);
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error making action", e.getMessage());
        }

        if (gamed.getAnswer()) {
            //true
            statusLabel.setVisible(true);
            statusLabel.setText("Correct : " + gamed.getNoPoints());
        } else {
            //false
            statusLabel.setVisible(true);
            statusLabel.setText("Incorrect : " + gamed.getNoPoints());
        }

        // set new question
        questionLabel.setText(gamed.getQuestion().getText());
        this.game.setQuestion(gamed.getQuestion());
    }


}
