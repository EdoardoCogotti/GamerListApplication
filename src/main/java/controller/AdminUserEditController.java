package controller;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.GamerListElement;
import model.Review;
import model.User;
import utils.UtilityMenu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminUserEditController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private TableView<GamerListElement> tbGameData;
    @FXML
    public TableColumn<GamerListElement, String> gameName;
    @FXML
    public TableColumn<GamerListElement, String> developerName;
    @FXML
    public TableColumn<GamerListElement, String> publisherName;
    @FXML
    private TableView<Review> tbReviewData;
    @FXML
    public TableColumn<Review, String> reviewGameName;
    @FXML
    public TableColumn<Review, String> reviewContentName;
    @FXML
    public TextArea reviewTextArea;
    @FXML
    public Button submitReviewButton;
    @FXML
    private Label firstNameValue, lastNameValue, genderValue, countryValue,
            emailValue, phoneValue, birthdayValue, registrationValue;

    private ObservableList<GamerListElement> gameList = FXCollections.observableArrayList();
    private ObservableList<Review> reviewList = FXCollections.observableArrayList();

    private Review currentReview;
    private int currentRow;
    private String username;

    private User profile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void deleteReview(){
        //Delete Review
        currentReview.delete();

        tbReviewData.getItems().removeAll(currentReview);
    }

    public void deleteUser(){
        //delete User
        profile.delete();

        try {
            switchToSearchUser();
        }
        catch(IOException e){e.printStackTrace();}
    }

    public void editReview(){
        submitReviewButton.setVisible(true);
        reviewTextArea.setVisible(true);
        reviewTextArea.setText(currentReview.getContent());
    }

    public void submitReview(){
        String newContent = reviewTextArea.getText();
        currentReview.setContent(reviewTextArea.getText());
        tbReviewData.getItems().set(currentRow, currentReview);

        //Commit Review change in db
        this.currentReview.update();

        submitReviewButton.setVisible(false);
        reviewTextArea.setVisible(false);
    }

    private void switchToSearchUser() throws IOException {
        Stage stage = (Stage) (tbGameData.getScene().getWindow()); // MenuItem isn't child of Node class, use FXML injection
        Parent root = FXMLLoader.load(getClass().getResource("/UserSearchScene.fxml"));
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        Scene scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/searchScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void displayInfo(String username){
        nameLabel.setText(username);
        username = nameLabel.getText();

        // get user info from db
        profile = User.getUserByName(username);

        firstNameValue.setText(profile.getFirstName());
        genderValue.setText(profile.getGender());
        countryValue.setText(profile.getCountry());
        registrationValue.setText(profile.getRegistered().toString());
        lastNameValue.setText(profile.getLastName());
        emailValue.setText(profile.getEmail());
        phoneValue.setText(profile.getPhone());
        birthdayValue.setText(profile.getBirthday().toString());

        //find gamelist from db
        for(GamerListElement gle: profile.getGamerList()){
            gameList.add(gle);
        }

        gameName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        developerName.setCellValueFactory(new PropertyValueFactory<>("Developer"));
        publisherName.setCellValueFactory(new PropertyValueFactory<>("Publisher"));

        tbGameData.setItems(gameList);

        tbReviewData.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {

                currentReview = tbReviewData.getSelectionModel().getSelectedItem();
                currentRow = tbReviewData.getSelectionModel().getSelectedIndex();
            }
        });

        for(Review r: Review.getReviewsByUser(username))
            reviewList.add(r);

        reviewGameName.setCellValueFactory(new PropertyValueFactory<>("Gamename"));
        reviewContentName.setCellValueFactory(new PropertyValueFactory<>("Content"));

        tbReviewData.setItems(reviewList);

    }
}
