package it.unipi.gamerlist.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import it.unipi.gamerlist.model.User;
import org.apache.commons.codec.digest.DigestUtils;
import it.unipi.gamerlist.utils.Session;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;

public class SigninController {

    @FXML
    TextField usernameTextField, passwordTextField;
    @FXML
    Label errorLabel;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void login(ActionEvent event) throws IOException {

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        User u = User.getUserByName(username);
        if(u == null){
            errorLabel.setVisible(true);
            errorLabel.setText("Username or Password incorrect");
            return;
        }
        String salt = u.getSalt();
        String hash256 = u.getSha256();
        String sha256hex = DigestUtils.sha256Hex(password+salt);

        if(hash256.equals(sha256hex)) {
            Session.getInstance().setLoggedUser(u);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
            root = loader.load();

            Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

            UserProfileController userProfileController = loader.getController();
            userProfileController.displayInfo(username, true);
            stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
            UtilityMenu.getInstance().bind(newRoot);
            scene = new Scene(newRoot);
            String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        }
        else{
            errorLabel.setVisible(true);
            errorLabel.setText("Username or Password incorrect");
        }
    }

    public void signup(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignupScene.fxml"));
        root = loader.load();

        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        String css = this.getClass().getResource("/css/signupScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
