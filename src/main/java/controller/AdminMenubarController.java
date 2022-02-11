package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import utils.Session;
import utils.UtilityMenu;

import java.io.IOException;

public class AdminMenubarController {

    @FXML
    private MenuBar profileMenuBar;

    private Scene scene;
    private Stage stage;

    public void switchToAdminAnalytic() throws IOException {
        stage = (Stage)(profileMenuBar.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminAnalyticScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        UtilityMenu.getInstance().bind(newRoot);
        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/analyticScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToUserForm() throws IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection

        Parent root = FXMLLoader.load(getClass().getResource("/SignupScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/signupScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToGameForm() throws IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameFormScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/signupScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSearchUser() throws IOException{
        stage = (Stage) (profileMenuBar.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/UserSearchScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/searchScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMyProfile() throws IOException{
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

    public void switchToSignin() throws IOException{
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
