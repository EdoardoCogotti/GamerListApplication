package it.unipi.gamerlist.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import it.unipi.gamerlist.utils.Session;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;

public class MenubarController {

    @FXML
    private HBox menuBox;
    @FXML
    private MenuBar profileMenuBar, searchMenuBar;

    private Scene scene;
    private Stage stage;

    public void switchToSearchGame() throws IOException {
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/GameSearchScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/searchScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSearchUser() throws IOException {
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/UserSearchScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/searchScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToGamerList() throws IOException {
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/GamerListScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/tables.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToFriendList() throws  IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/FriendListScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/tables.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSuggestion() throws  IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/SuggestionScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/suggestionScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAnalytic() throws  IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/AnalyticScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/analyticScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMyProfile() throws  IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        String username = Session.getInstance().getLoggedUser().getUsername();
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(username, true);

        UtilityMenu.getInstance().bind(newRoot);
        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSignin(ActionEvent event) throws IOException {
        Session.getInstance().logout();
        UtilityMenu.getInstance().logout();

        Parent root = FXMLLoader.load(getClass().getResource("/SigninScene.fxml"));
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        scene = new Scene(root);
        String css = this.getClass().getResource("/css/signinScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
