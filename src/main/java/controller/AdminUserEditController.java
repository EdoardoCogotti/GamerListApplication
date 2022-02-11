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
        //DONE Delete User
        //System.out.println("cerco di eliminare l'account di " + username);
        //User u = User.getUserByName(username);
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

        // DONE get user info from db
        profile = User.getUserByName(username);

        /*
        User profile = new User();
        //profile.setId(1);
        profile.setFirstName("Edoardo");
        profile.setLastName("Cogotti");
        profile.setGender("male");
        profile.setCountry("Italy");
        profile.setEmail("edoardocogotti@libero.it");
        profile.setPhone("3331234567");
        profile.setBirthday(LocalDate.now());
        profile.setRegistered(LocalDate.now());
        */

        //int id = profile.getId();
        firstNameValue.setText(profile.getFirstName());
        genderValue.setText(profile.getGender());
        countryValue.setText(profile.getCountry());
        registrationValue.setText(profile.getRegistered().toString());
        lastNameValue.setText(profile.getLastName());
        emailValue.setText(profile.getEmail());
        phoneValue.setText(profile.getPhone());
        birthdayValue.setText(profile.getBirthday().toString());

        //DONE find gamelist from db
        for(GamerListElement gle: profile.getGamerList()){
            gameList.add(gle);
        }
        /*for(int i=0; i<30; i++){
            GamerListElement g = new GamerListElement();
            g.setName("Peggle 3");
            g.setDeveloper("Bandai");
            g.setPublisher("Bandai");
            gameList.add(g);
        }*/

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
        /*
        for(int i=0; i<100; i++){
            Review r = new Review();
            r.setUsername(username);
            r.setGamename("Peggle");
            r.setContent(i+"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent dignissim massa eget augue sollicitudin, sit amet tempus ex volutpat. Quisque in tellus et purus tempus ultrices in ut leo. Proin ac viverra tortor. Etiam at arcu vel turpis ullamcorper eleifend. Fusce sit amet nunc in eros sodales vestibulum id eu elit. Phasellus vestibulum tortor eu eros blandit ullamcorper. Nunc bibendum tristique tortor a condimentum. Morbi tristique finibus turpis, vitae ullamcorper dui accumsan nec. Phasellus scelerisque leo in arcu hendrerit facilisis. Nam eget mauris facilisis justo convallis ullamcorper vel id felis. Vivamus ut faucibus massa. Mauris quis cursus sem. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus" );
            reviewList.add(r);
        }*/

        reviewGameName.setCellValueFactory(new PropertyValueFactory<>("Gamename"));
        reviewContentName.setCellValueFactory(new PropertyValueFactory<>("Content"));

        tbReviewData.setItems(reviewList);

    }
}
