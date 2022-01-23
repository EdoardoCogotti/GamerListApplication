import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Game;
import model.Review;

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

        //Game prova = Game.getGamesByNamePart("Pimpa").get(0);
        //prova.delete();
        //Review provRev = Review.getReviewsByGame(prova).get(0);
        //prova.update();

        long startTime, stopTime;

        //String[] genresArray = {"", "Action", "Adventure", "Animation & Modeling", "Arcade", "Audio Production", "Building", "Casual", "Chess", "Combat", "Comedy", "Design & Illustration", "Detective-mystery", "Early Access", "Economic", "Education", "Educational", "Espionage", "Exploration", "FPP", "Fantasy", "Fighting", "Free to Play", "Game Development", "Gore", "Hidden Object", "Historical", "Horror", "Indie", "JRPG", "Managerial", "Massively Multiplayer", "Metroidvania", "Modern", "Mystery", "Narrative", "Naval", "Off-road", "Open World", "Photo Editing", "Pinball", "Platformer", "Point-and-click", "Programming", "Puzzle", "RPG", "Racing", "Rally", "Real-time", "Roguelike", "Role-playing", "Sandbox", "Sci-fi", "Sexual Content", "Shooter", "Simulation", "Soccer", "Software Training", "Sports", "Stealth", "Strategy", "Survival", "TPP", "Tactical", "Team sport", "Touring", "Turn-based", "Utilities", "Video Production", "Violent", "Virtual life", "Visual Novel", "Web Publishing"};

        /*for(String genre : genresArray){
            startTime = System.currentTimeMillis();
            Game.getTopKByGenre(3, genre);
            stopTime = System.currentTimeMillis();
            System.out.println(genre + " :" + (stopTime - startTime));
        }*/

        /*
        long startTime = System.currentTimeMillis();
        Game.getTopKByGenre(3);
        long stopTime = System.currentTimeMillis();
        System.out.println(stopTime - startTime);
        */

        //Game.getAll();
    }

    @Override
    public void stop(){

        // close connections
        //utils.MongoDriver mongoDriver = utils.MongoDriver.getInstance();
        //utils.Neo4jDriver neoDriver = utils.Neo4jDriver.getInstance();

        //mongoDriver.close();
        //neoDriver.close();


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