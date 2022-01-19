package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Game;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
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

    private final String[] gameDetails = {"single_player", "multi_player", "controller_support", "coop", "cloud_saves", "achievements"};
    private final String[] genres = {"action", "adventure",  "card game", "fps", "indie", "platform", "puzzle",
            "turn-based", "fantasy", "strategy"};
    private final String[] languages = {"english", "italian", "spanish", "french", "chinese", "japanese", "german",
            "spanish - spain", "simplified chinese", "traditional chinese", "korean", "polish", "portuguese",
            "russian", "czech"};

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
        //GET INFO FROM DB
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
            //System.out.println(details.get(key));
            if(details.get(key).toString().equals("true"))
                gameDetailsCheck.add(key);
        }

        //String[] gameDetailsCheck =  ;
        

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
        for(String s: gameDetailsCheck)
            checkComboBoxGameDetails.getCheckModel().toggleCheckState(s);
        for(String s: genresCheck) {
            System.out.println(s);
            checkComboBoxGenres.getCheckModel().toggleCheckState(s.toLowerCase(Locale.ROOT));
        }
        for(String s: languagesCheck){
            checkComboBoxLanguages.getCheckModel().toggleCheckState(s.toLowerCase(Locale.ROOT));
        }
    }

}
