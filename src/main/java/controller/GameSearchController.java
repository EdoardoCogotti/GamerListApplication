package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameSearchController implements Initializable{

    @FXML
    private ListView<String> gameList;
    @FXML
    private TextField searchBar;
    
    private String[] games = {"Layton", "Dark Souls", "Peggle", "Pokemon", "prova1", "prova2",  "prova3", "prova4", "prova1", "prova2",  "prova3", "prova4","prova1", "prova2",  "prova3", "prova4", "prova1", "prova2",  "prova3", "prova4"};
    private ObservableList game = FXCollections.observableArrayList();
    private String currentGame;

    private Stage stage;
    private Scene scene;

    public void searchGames(ActionEvent event){

        game.clear();

        // TO_DO search games in db
        String searchedString = searchBar.getText();

        for(String g : games)
            game.add(g);

        gameList.setItems(game);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                currentGame = gameList.getSelectionModel().getSelectedItem();
            }
        });

        gameList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                    try {
                        User u = Session.getInstance().getLoggedUser();
                         if(u!=null && u.getAdmin())
                            switchToUserEdit(mouseEvent);
                        else
                            switchToGameInfo(mouseEvent, currentGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    public void switchToUserEdit(MouseEvent event) throws IOException {
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUserEditScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userEditScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
