package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private String gameDescription, minimumRequirements, recommendRequirements;
    private String windowsOS, linuxOS, macOS;

    private String size;
    private List<String> oses;
    private boolean inDevelopment;

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

        //TO_DO add new game in db

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

        //TO_DO add new game in db

        switchToUserProfile();
    }

    public void setCommonGameInfo(String gamename,String publisher,String developer,String store,LocalDate releaseDate, String url, String rating, int achievements){
        this.gamename=gamename;
        this.publisher=publisher;
        this.developer=developer;
        this.store=store;
        this.releaseDate=releaseDate;
        this.url = url;
        this.rating = rating;
        this.achievements = achievements;
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

}
