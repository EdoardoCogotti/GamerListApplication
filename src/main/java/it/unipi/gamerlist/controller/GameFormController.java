package it.unipi.gamerlist.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import it.unipi.gamerlist.model.Game;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONObject;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class GameFormController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField gamenameTextField, publisherTextField, developerTextField, ratingTextField, urlTextField, achievementsTextField;
    @FXML
    private RadioButton rButtonSteam, rButtonGog;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private Label errorLabel;

    private final String[] gameDetails = {"single_player", "multi_player", "controller_support", "coop", "cloud_saves", "achievement"};
    private final String[] genres =
            {"Action", "Adventure", "Animation & Modeling", "Arcade", "Audio Production", "Building", "Casual", "Combat", "Comedy",
                    "Design & Illustration", "Detective-mystery", "Early Access", "Economic", "Education", "Educational", "Espionage", "FPP",
                    "Fantasy", "Fighting", "Free to Play", "Game Development", "Hidden Object", "Historical", "Horror", "Indie",  "Managerial",
                    "Massively Multiplayer", "Metroidvania", "Modern", "Mystery", "Narrative", "Naval", "Off-road", "Open World", "Photo Editing",
                    "Pinball", "Platformer", "Point-and-click",  "Puzzle", "RPG", "Racing", "Rally", "Real-time", "Roguelike", "Role-playing",
                    "Sandbox", "Sci-fi",  "Shooter", "Simulation", "Soccer", "Software Training", "Sports", "Stealth", "Strategy", "Survival",
                    "TPP", "Tactical", "Turn-based", "Utilities", "Video Production", "Virtual Life", "Visual Novel", "Web Publishing"};
            /*{"action", "adventure", "animation & modeling", "arcade", "audio production", "building", "casual", "combat", "comedy",
            "design & illustration", "detective-mystery", "early access", "economic", "education", "educational", "espionage", "fpp",
            "fantasy", "fighting", "free to play", "game development", "hidden object", "historical", "horror", "indie",  "managerial",
            "massively multiplayer", "metroidvania", "modern", "mystery", "narrative", "naval", "off-road", "open world", "photo editing",
            "pinball", "platformer", "point-and-click",  "puzzle", "rpg", "racing", "rally", "real-time", "roguelike", "role-playing",
            "sandbox", "sci-fi",  "shooter", "simulation", "soccer", "software training", "sports", "stealth", "strategy", "survival",
            "tpp", "tactical", "turn-based", "utilities", "video production", "virtual life", "visual novel", "web publishing"};*/

    private final String[] languages = {"english", "italian", "spanish", "french", "chinese", "japanese", "german",
            "spanish - spain", "simplified chinese", "traditional chinese", "korean", "polish", "portuguese",
            "russian", "czech", "dutch",
            "danish", "turkish", "ukrainian", "swedish", "norwegian", "arabic", "romanian",
            "finnish", "greek", "bulgarian", "hungarian", "spanish - latin america", "portuguese - brazil","slovak", "catalan","serbian", "icelandic"};




    CheckComboBox<String> checkComboBoxGameDetails, checkComboBoxGenres, checkComboBoxLanguages;

    // game fields
    private String gamename, publisher, developer, store, url, rating;
    private LocalDate releaseDate;
    private int achievements;
    ObservableList<String> checkedGameDetailsList, checkedGenreList, checkedLanguagesList;

    private Stage stage;
    private Scene scene;

    private boolean newGameFlag;

    public void next() throws IOException {

        gamename = gamenameTextField.getText();
        publisher = publisherTextField.getText();
        developer = developerTextField.getText();
        url = urlTextField.getText();
        rating = ratingTextField.getText();

        //GAME NAME
        if(gamename.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose game name");
            return;
        }

        //URL
        if(!url.startsWith("https://")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must set correct url (\"https://\")");
            return;
        }

        //DEVELOPER
        if(developer.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose game developer");
            return;
        }

        //PUBLISHER
        if(publisher.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose game publisher");
            return;
        }

        //RATING
        if(rating.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must set rating");
            return;
        }

        //STORE
        if(!rButtonSteam.isSelected() && !rButtonGog.isSelected()){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose game store");
            return;
        }
        else{
            store = (rButtonSteam.isSelected()) ? "Steam" : "Gog";
        }

        //GAME_DETAILS
        checkedGameDetailsList = FXCollections.observableArrayList();
        checkedGameDetailsList = checkComboBoxGameDetails.getCheckModel().getCheckedItems();
        if(checkedGameDetailsList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game details");
            return;
        }

        // GENRES
        checkedGenreList = FXCollections.observableArrayList();
        checkedGenreList = checkComboBoxGenres.getCheckModel().getCheckedItems();
        if(checkedGenreList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game genres");
            return;
        }

        // LANGUAGES
        checkedLanguagesList = FXCollections.observableArrayList();
        checkedLanguagesList = checkComboBoxLanguages.getCheckModel().getCheckedItems();
        if(checkedLanguagesList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game languages");
            return;
        }

        //ACHIVEMENTS
        achievements = Integer.parseInt(achievementsTextField.getText());

        // DATE
        releaseDate = releaseDatePicker.getValue();
        if(releaseDate==null) {
            errorLabel.setVisible(true);
            errorLabel.setText("Select game release date");
            return;
        }

        switchToNextPage();

    }

    public void switchToNextPage() throws IOException {

        String path;
        if(store.equals("Steam"))
            path = "/GameFormSteamScene.fxml";
        else
            path = "/GameFormGogScene.fxml";

        stage = (Stage) (gridPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        GameFormNextController gameFormNextController = loader.getController();
        gameFormNextController.setCommonGameInfo(gamename, publisher, developer, store, releaseDate, url, rating, achievements, checkedGameDetailsList, checkedGenreList, checkedLanguagesList);
        if(!newGameFlag)
            gameFormNextController.loadFields(gamename);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/signupScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        final ObservableList<String> gameDetailsList = FXCollections.observableArrayList();
        for (String gd : gameDetails) {
            gameDetailsList.add(gd);
        }

        final ObservableList<String> genresList = FXCollections.observableArrayList();
        for (String g : genres) {
            genresList.add(g);
        }

        final ObservableList<String> languagesList = FXCollections.observableArrayList();
        for (String l : languages) {
            languagesList.add(l);
        }

        // Create the CheckComboBox with the data
        checkComboBoxGameDetails = new CheckComboBox<>(gameDetailsList);
        checkComboBoxGenres = new CheckComboBox<>(genresList);
        checkComboBoxLanguages = new CheckComboBox<>(languagesList);

        checkComboBoxGameDetails.setShowCheckedCount(false);
        gridPane.add(checkComboBoxGameDetails,2,6);
        gridPane.add(checkComboBoxGenres,2,7);
        gridPane.add(checkComboBoxLanguages,2,8);

        // force the field to be numeric only
        achievementsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    achievementsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        newGameFlag=true;

    }

    public void loadFields(String name){
        newGameFlag = false;

        gamename = name;
        //get game info from db
        Game game = Game.getGamesByNamePart(gamename).get(0);

        /*PLACEHOLDERS*/
        publisher = game.getPublisher();
        developer = game.getDeveloper();
        store = game.getStore();
        url = game.getUrl();
        rating = game.getRating();
        releaseDate = game.getReleaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        achievements = game.getAchievements();
        ArrayList<String> genresCheck = new ArrayList<String>(game.getGenres());
        ArrayList<String> languagesCheck = new ArrayList<String>(game.getLanguages());

        JSONObject details = game.getGameDetails();
        Iterator x = details.keys();
        ArrayList<String> gameDetailsCheck = new ArrayList<String>();
        while (x.hasNext()){
            String key = (String) x.next();
            if(details.get(key).toString().equals("true"))
                gameDetailsCheck.add(key);
        }

        gamenameTextField.setText(gamename);
        publisherTextField.setText(publisher);
        developerTextField.setText(developer);
        if(store.equals("Steam"))
            rButtonSteam.setSelected(true);
        else if(store.equals("GOG"))
            rButtonGog.setSelected(true);
        urlTextField.setText(url);
        ratingTextField.setText(rating);
        releaseDatePicker.setValue(releaseDate);
        achievementsTextField.setText(String.valueOf(achievements));
        for(String s: gameDetailsCheck) {
            checkComboBoxGameDetails.getCheckModel().toggleCheckState(s);
        }
        for(String s: genresCheck) {
            checkComboBoxGenres.getCheckModel().toggleCheckState(s);
            //checkComboBoxGenres.getCheckModel().toggleCheckState(s.toLowerCase(Locale.ROOT));
        }
        for(String s: languagesCheck){
            checkComboBoxLanguages.getCheckModel().toggleCheckState(s);
            //checkComboBoxLanguages.getCheckModel().toggleCheckState(s.toLowerCase(Locale.ROOT));
        }

        //allow disabled text fields
        gamenameTextField.setDisable(true);
        rButtonGog.setDisable(true);
        rButtonSteam.setDisable(true);
    }

}
