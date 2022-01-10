import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Game;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // set starting page/scene
        Parent root = FXMLLoader.load(getClass().getResource("/SigninScene.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/GameInfoScene.fxml"));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("css/signinScene.css").toExternalForm();
        //String css = this.getClass().getResource("css/gameInfoScene.css").toExternalForm();
        scene.getStylesheets().add(css);

        Image icon = new Image("images/gamepad.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("GamerList");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event ->{
            event.consume();
            logout(primaryStage);
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        Game prova = Game.getGamesByNamePart("Fever").get(0);
        String nome = prova.getName();
        prova.setName(nome + "_PisaMerda");
        prova.update();
    }

    @Override
    public void stop(){

        // close connections
        /*utils.MongoDriver mongoDriver = utils.MongoDriver.getInstance();
        utils.Neo4jDriver neoDriver = utils.Neo4jDriver.getInstance();

        mongoDriver.close();
        neoDriver.close();
        */

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
