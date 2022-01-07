package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    public void switchToSignin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/SigninScene.fxml"));
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        scene = new Scene(root);
        String css = this.getClass().getResource("/css/signinScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
