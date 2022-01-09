package controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.User;

import java.time.LocalDate;

public class UserProfileController {

    // FXML will inject all values fxml file into controller class
    @FXML
    private Label nameLabel;
    @FXML
    private MenuBar profileMenuBar;
    @FXML
    private Label firstNameValue, lastNameValue, genderValue, countryValue,
            emailValue, phoneValue, birthdayValue, registrationValue;
    @FXML
    private HBox lastNameBox, emailBox, phoneBox, birthdayBox, followBox;
    @FXML
    private Button followButton, deleteButton;

    private int id;
    private boolean followed;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void displayInfo(String username, boolean myProfile){

        if (username.length() > 15)
            username = username.substring(0, 10) + "...";
        nameLabel.setText(username);

        // TO_DO get user info from db
        User u = new User();
        u.setId(1);
        u.setFirstName("Edoardo");
        u.setLastName("Cogotti");
        u.setGender("male");
        u.setCountry("Italy");
        u.setEmail("edoardocogotti@libero.it");
        u.setPhone("3331234567");
        u.setBirthday(LocalDate.now());
        u.setRegistered(LocalDate.now());

        // TO_DO check if already follower
        followed = true;

        if(!myProfile) {
            followBox.setVisible(true);
            followBox.setManaged(true);
            if(followed){
                followButton.setText("UNFOLLOW");
                followed = false;
            }
            deleteButton.setManaged(false);
        }
        else{
            followBox.setVisible(false);
            followBox.setManaged(false);
        }

        id = u.getId();
        firstNameValue.setText(u.getFirstName());
        genderValue.setText(u.getGender());
        countryValue.setText(u.getCountry());
        registrationValue.setText(u.getRegistered().toString());
        if(myProfile){
            lastNameValue.setText(u.getLastName());
            emailValue.setText(u.getEmail());
            phoneValue.setText(u.getPhone());
            birthdayValue.setText(u.getBirthday().toString());
        }
        else{
            lastNameBox.setVisible(false);
            lastNameBox.setManaged(false);
            emailBox.setVisible(false);
            emailBox.setManaged(false);
            phoneBox.setVisible(false);
            phoneBox.setManaged(false);
            birthdayBox.setVisible(false);
            birthdayBox.setManaged(false);
        }


    }

    public void follow(){

        System.out.println("FOLLOW PLACEHOLDER");
        if(followed) {
            followed = false;
            followButton.setText("UNFOLLOW");
            // TO_DO delete follow in graph db
        }
        else {
            followed = true;
            followButton.setText("FOLLOW");
            // TO_DO insert follow in graph db
        }
    }


}
