package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AnalyticController implements Initializable {

    @FXML
    private ListView<String> gameList, userList;

    private String[] games = {"Layton", "Dark Souls", "Peggle", "Pokemon"};
    private ObservableList game = FXCollections.observableArrayList();
    private String currentGame;
    private String[] users = {"Edo97", "Fra97", "Anna97"};
    private ObservableList user = FXCollections.observableArrayList();
    private String currentUser;
    private Scene scene;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                currentGame = gameList.getSelectionModel().getSelectedItem();
            }
        });

        gameList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                try {
                    switchToGameInfo(mouseEvent, currentGame);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                currentUser = userList.getSelectionModel().getSelectedItem();
            }
        });

        userList.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                try {
                    switchToUserProfile(mouseEvent, currentUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fillRecommendGames();
        fillRecommendUsers();
    }

    public void fillRecommendGames(){
        game.clear();

        // TO_DO find recommend games
        for(String g : games) {
            game.add(g);
        }

        gameList.setItems(game);
    }


    public void fillRecommendUsers(){
        user.clear();

        // TO_DO find recommend games
        for(String u : users) {
            user.add(u);
        }

        userList.setItems(user);
    }

    public void switchToGameInfo(MouseEvent event, String game) throws IOException {
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameInfoScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        GameInfoController gameInfoController = loader.getController();
        gameInfoController.setGameScene(game);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/gameInfoScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToUserProfile(MouseEvent event, String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();

        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(username, false);

        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        UtilityMenu.getInstance().bind(newRoot);
        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
