package controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.User;
import utils.Session;

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
    private Button followButton;

    private int id;
    private boolean followed;

    User profile;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void displayInfo(String username, boolean myProfile){

        if (username.length() > 15)
            username = username.substring(0, 10) + "...";
        nameLabel.setText(username);

        // DONE get user info from db
        profile = User.getUserByName(username);

        /*User profile = new User();
        profile.setId(1);
        profile.setFirstName("Edoardo");
        profile.setLastName("Cogotti");
        profile.setGender("male");
        profile.setCountry("Italy");
        profile.setEmail("edoardocogotti@libero.it");
        profile.setPhone("3331234567");
        profile.setBirthday(LocalDate.now());
        profile.setRegistered(LocalDate.now());*/

        // DONE check if already follower TOCHANGE
        if(!myProfile) {
            followed = Session.getInstance().getLoggedUser().searchInFollowing(profile); //true;
            System.out.println("followed " + followed);

            followBox.setVisible(true);
            followBox.setManaged(true);
            if(followed){
                followButton.setText("UNFOLLOW");
                followed = false;
            }
        }
        else{
            followBox.setVisible(false);
            followBox.setManaged(false);
        }

        //id = profile.getId();
        firstNameValue.setText(profile.getFirstName());
        genderValue.setText(profile.getGender());
        countryValue.setText(profile.getCountry());
        registrationValue.setText(profile.getRegistered().toString());
        if(myProfile){
            lastNameValue.setText(profile.getLastName());
            emailValue.setText(profile.getEmail());
            phoneValue.setText(profile.getPhone());
            birthdayValue.setText(profile.getBirthday().toString());
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
        System.out.println("followed: " + followed);
        if(!followed) {
            followed = true;
            followButton.setText("UNFOLLOW");
            //Session.getInstance().getLoggedUser().unfollowUser(profile);
            User.addFollow(Session.getInstance().getLoggedUser().getUsername(),profile.getUsername());
            // DONE delete follow in graph db
        }
        else {
            followed = false;
            followButton.setText("FOLLOW");
            //Session.getInstance().getLoggedUser().followUser(profile);
            User.unfollowUser(Session.getInstance().getLoggedUser().getUsername(),profile.getUsername());
            // DONE insert follow in graph db
        }
    }


}
