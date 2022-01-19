package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Game;

import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameFormNextController {

    @FXML
    private TextField gameDescriptionTextField, minimumRequirementsTextField, recommendRequirementsTextField;
    @FXML
    private TextField windowsTextField, linuxTextField, macTextField;

    @FXML
    private TextField sizeTextField, ratingTextField;
    @FXML
    private CheckBox inDevelopmentCheckBox;

    @FXML
    Label errorLabel;
    @FXML
    GridPane gridPane;

    private String gamename, publisher, developer, store, url, rating;
    private LocalDate releaseDate;
    private int achievements;
    ObservableList<String> checkedGameDetailsList, checkedGenreList, checkedLanguagesList;

    private String gameDescription, minimumRequirements, recommendRequirements;
    private String windowsOS, linuxOS, macOS;

    private String size;
    private List<String> oses;
    private boolean inDevelopment;

    boolean newGameFlag = true;

    public void submitSteam() throws IOException {

        gameDescription = gameDescriptionTextField.getText();
        if(gameDescription.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game description");
            return;
        }

        minimumRequirements = minimumRequirementsTextField.getText();
        if(minimumRequirements.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("Select minimum requirements");
            return;
        }

        recommendRequirements = recommendRequirementsTextField.getText();
        if(recommendRequirements.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("Select recommend requirements");
            return;
        }

        ArrayList details = new ArrayList<String>(checkedGameDetailsList);
            Game newGame = new Game(
                store,
                url,
                gamename,
                java.util.Date.from(releaseDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                developer,
                publisher,
                new ArrayList<String>(checkedLanguagesList),
                achievements,
                new ArrayList<String>(checkedGenreList),
                rating,
                0,
                details.contains("single_player"),
                details.contains("multi_player"),
                details.contains("coop"),
                details.contains("controller_support"),
                details.contains("cloud_saves"),
                details.contains("achievement"),
                0.0,
                new ArrayList<String>(),
                size,
                inDevelopment,
                gameDescription,
                minimumRequirements,
                recommendRequirements
            );

        if(newGameFlag) {
            //DONE add new game in db
            newGame.insert();
        }
        else{
            //DONE UPDATE GAME
            String gameName = Session.getInstance().getCurrentGame().getName();
            Game gameToUpdate = Game.getGamesByNamePart(gameName).get(0);
            newGame.setId(gameToUpdate.getId());
            newGame.setTotalReviews(gameToUpdate.getTotReviews());

            newGame.update();
        }

        switchToUserProfile();
    }

    public void submitGog() throws IOException {

        size = sizeTextField.getText();
        inDevelopment = inDevelopmentCheckBox.isSelected();

        windowsOS = windowsTextField.getText();
        linuxOS = linuxTextField.getText();
        macOS = macTextField.getText();

        if(size.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game size");
            return;
        }

        if(windowsOS.equals("") && linuxOS.equals("") && macOS.equals("")){
            errorLabel.setVisible(true);
            errorLabel.setText("Select at least one OS");
            return;
        }

        oses = new ArrayList<>();
        if(!windowsOS.equals(""))
            oses.add("Windows (" + windowsOS + ")");
        if(!linuxOS.equals(""))
            oses.add("Linux (" + linuxOS + ")");
        if(!macOS.equals(""))
            oses.add("Mac OS X (" + macOS + ")");

        //DONE add new game in db
        ArrayList details = new ArrayList<String>(checkedGameDetailsList);
        Game newGame = new Game(
                store,
                url,
                gamename,
                java.util.Date.from(releaseDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                developer,
                publisher,
                new ArrayList<String>(checkedLanguagesList),
                achievements,
                new ArrayList<String>(checkedGenreList),
                rating,
                0,
                details.contains("single_player"),
                details.contains("multi_player"),
                details.contains("coop"),
                details.contains("controller_support"),
                details.contains("cloud_saves"),
                details.contains("achievement"),
                0.0,
                new ArrayList<String>(),
                size,
                inDevelopment,
                gameDescription,
                minimumRequirements,
                recommendRequirements
        );

        if(newGameFlag) {
            newGame.insert();
        }
        else {
            String gameName = Session.getInstance().getCurrentGame().getName();
            Game gameToUpdate = Game.getGamesByNamePart(gameName).get(0);
            newGame.setId(gameToUpdate.getId());
            newGame.setTotalReviews(gameToUpdate.getTotReviews());

            newGame.update();
        }

        switchToUserProfile();
    }

    public void setCommonGameInfo(String gamename, String publisher, String developer, String store, LocalDate releaseDate, String url, String rating, int achievements, ObservableList<String> checkComboBoxGameDetails, ObservableList<String> checkComboBoxGenres, ObservableList<String> checkComboBoxLanguages){
        this.gamename=gamename;
        this.publisher=publisher;
        this.developer=developer;
        this.store=store;
        this.releaseDate=releaseDate;
        this.url = url;
        this.rating = rating;
        this.achievements = achievements;
        this.checkedGameDetailsList = checkComboBoxGameDetails;
        this.checkedGenreList = checkComboBoxGenres;
        this.checkedLanguagesList = checkComboBoxLanguages;
    }

    public void switchToUserProfile() throws IOException {
        Stage stage = (Stage) (gridPane.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        String username = Session.getInstance().getLoggedUser().getUsername();
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(username, true);

        UtilityMenu.getInstance().bind(newRoot);
        Scene scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void loadFields(String gameName){
        newGameFlag = false;
        //GET INFO FROM DB
        Game game = Game.getGamesByNamePart(gameName).get(0);

        if(store.equals("Steam")){
            // PLACEHOLDERS
            gameDescription = game.getGameDescription();
            minimumRequirements = game.getMinimumRequirements();
            recommendRequirements = game.getRecommendedRequirements();

            gameDescriptionTextField.setText(gameDescription);
            minimumRequirementsTextField.setText(minimumRequirements);
            recommendRequirementsTextField.setText(recommendRequirements);
        }
        else if(store.equals("Gog")){
            // PLACEHOLDERS
            size = game.getSize();
            inDevelopment = game.getInDevelopment();
            windowsOS = game.getOses().stream().collect(Collectors.joining(", ", "", ""));

            sizeTextField.setText(size);
            inDevelopmentCheckBox.setSelected(inDevelopment);
            windowsTextField.setText(windowsOS);
            linuxTextField.setText(linuxOS);
            macTextField.setText(macOS);
        }
    }

}
