package controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Game;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameFormController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField gamenameTextField, publisherTextField, developerTextField;
    @FXML
    private RadioButton rButtonSteam, rButtonGog;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private Label errorLabel;

    private final String[] gameDetails = {"single_player", "multi_player", "controller_support", "coop", "cloud_saves", "achievements"};
    private final String[] genres = {"action", "adventure",  "card game", "fps", "indie", "platform", "puzzle"};
    private final String[] languages = {"english", "italian", "spanish", "french", "chinese", "japanese"};

    CheckComboBox<String> checkComboBoxGameDetails, checkComboBoxGenres, checkComboBoxLanguages;

    public void submit(){

        String gamename = gamenameTextField.getText();
        String publisher = publisherTextField.getText();
        String developer = developerTextField.getText();
        String store;
        LocalDate releaseDate;

        //GAME NAME
        if(gamename.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose game name");
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
        ObservableList<String> checkedGameDetailsList = FXCollections.observableArrayList();
        checkedGameDetailsList = checkComboBoxGameDetails.getCheckModel().getCheckedItems();
        if(checkedGameDetailsList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game details");
            return;
        }

        // GENRES
        ObservableList<String> checkedGenreList = FXCollections.observableArrayList();
        checkedGenreList = checkComboBoxGenres.getCheckModel().getCheckedItems();
        if(checkedGenreList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game genres");
            return;
        }

        // LANGUAGES
        ObservableList<String> checkedLanguagesList = FXCollections.observableArrayList();
        checkedLanguagesList = checkComboBoxLanguages.getCheckModel().getCheckedItems();
        if(checkedLanguagesList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game languages");
            return;
        }

        // DATE
        releaseDate = releaseDatePicker.getValue();
        if(releaseDate==null) {
            errorLabel.setVisible(true);
            errorLabel.setText("Select game release date");
            return;
        }

        //Add new game in db
        ArrayList details = new ArrayList<String>(checkedGameDetailsList);
        Game newGame = new Game(
            store,
            "www.google.com",
            gamename,
            java.util.Date.from(releaseDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
            developer,
            publisher,
            new ArrayList<String>(checkedLanguagesList),
            0,
            new ArrayList<String>(checkedGenreList),
            "PEGI 18",
            0,
            details.contains("single_player"),
            details.contains("multi_player"),
            details.contains("coop"),
            details.contains("controller_support"),
            details.contains("cloud_saves"),
            details.contains("achievement"),
            0,
            new ArrayList<String>(),
            "temp MB",
            true,
            "temp",
            "temp",
            "temp"
        );

        newGame.insert();
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
        gridPane.add(checkComboBoxGameDetails,2,4);
        gridPane.add(checkComboBoxGenres,2,5);
        gridPane.add(checkComboBoxLanguages,2,6);

    }
}
