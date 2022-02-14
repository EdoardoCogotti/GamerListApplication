package it.unipi.gamerlist;

import it.unipi.gamerlist.model.Review;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import it.unipi.gamerlist.driver.Neo4jDriver;
import it.unipi.gamerlist.driver.MongoDriver;
import it.unipi.gamerlist.utils.Log;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // set starting page/scene
        Parent root = FXMLLoader.load(getClass().getResource("/SigninScene.fxml"));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/css/signinScene.css").toExternalForm();
        scene.getStylesheets().add(css);

        Image icon = new Image("/images/gamepad.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("GamerList");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event ->{
            event.consume();
            logout(primaryStage);
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        Log.initLog();
    }

    @Override
    public void stop(){

        // close connections
        MongoDriver mongoDriver = MongoDriver.getInstance();
        Neo4jDriver neoDriver = Neo4jDriver.getInstance();

        mongoDriver.close();
        neoDriver.close();

    }

    public void logout(Stage stage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout");
        alert.setContentText("Do you want to close?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }
}