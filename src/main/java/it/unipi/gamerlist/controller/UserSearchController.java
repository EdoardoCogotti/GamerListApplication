package it.unipi.gamerlist.controller;

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
import it.unipi.gamerlist.model.User;
import it.unipi.gamerlist.utils.Session;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserSearchController implements Initializable {

    @FXML
    private ListView<String> userList;
    @FXML
    private TextField searchBar;

    private ObservableList user = FXCollections.observableArrayList();
    private String currentUser;

    private Stage stage;
    private Scene scene;

    public void searchUsers(ActionEvent event){

        user.clear();

        // DONE search users in db
        String searchedString = searchBar.getText();

        List<User> userlist = User.getUsersByNamePart(searchedString);
        for(User u : userlist) {
            user.add(u.getUsername());
        }

        userList.setItems(user);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                currentUser = userList.getSelectionModel().getSelectedItem();
            }
        });

        userList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                    try {
                        if(currentUser==null || currentUser.equals(""))
                            return;

                        if(Session.getInstance().getLoggedUser().getAdmin())
                            switchToEditUser(mouseEvent);
                        else
                            switchToUser(mouseEvent, currentUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void switchToUser(MouseEvent event, String user) throws IOException {
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(user, false);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToEditUser(MouseEvent event) throws IOException {
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUserEditScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        AdminUserEditController adminUserEditController = loader.getController();
        adminUserEditController.displayInfo(currentUser);

        scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();
    }
}
