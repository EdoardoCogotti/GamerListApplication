package it.unipi.gamerlist.controller;

import javafx.collections.FXCollections;
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
import it.unipi.gamerlist.model.GamerListElement;
import it.unipi.gamerlist.model.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import it.unipi.gamerlist.utils.Session;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupController implements Initializable {

    @FXML
    private TextField usernameTextField, passwordTextField, passwordConfirmTextField,
            firstNameTextField, lastNameTextField, phoneTextField, emailTextField;
    @FXML
    private Label errorLabel;
    @FXML
    private RadioButton rButtonMale, rButtonFemale;
    @FXML
    private DatePicker myDatePicker;
    @FXML
    private Button backButton;
    @FXML
    private GridPane gridPane;

    private ComboBox<String> comboBoxCountry;
    private ComboBox<String> checkComboBoxGenres;
    private ObservableList<String> checkedGenreList;

    private String[] countryList =
            {"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecudaor", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France, Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kosovo", "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfork Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia South Sandwich Islands", "South Sudan", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States minor outlying islands", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City State", "Venezuela", "Vietnam", "Virigan Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zaire", "Zambia", "Zimbabwe"};
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

    private Stage stage;
    private Scene scene;

    private User user;


    public void submit(ActionEvent event){

        String username, password, firstName, lastName, gender, email, phone, country;
        int age;
        LocalDate birthDate, regDate;

        // USERNAME
        // check user existence in db
        username = usernameTextField.getText();
        if(User.doesUserExist(username)){
            errorLabel.setVisible(true);
            errorLabel.setText("This user already exists");
            return;
        }

        // PASSWORD
        password = passwordTextField.getText();
        if(!password.equals(passwordConfirmTextField.getText())){
            errorLabel.setVisible(true);
            errorLabel.setText("Passwords must be equal");
            return;
        }
        else if(password.length()<4){
            errorLabel.setVisible(true);
            errorLabel.setText("Too short password");
            return;
        }

        // FIRST AND LAST NAME
        firstName = firstNameTextField.getText();
        lastName = lastNameTextField.getText();

        // GENDER
        if(!rButtonFemale.isSelected() && !rButtonMale.isSelected()){
            errorLabel.setVisible(true);
            errorLabel.setText("You must choose your sex");
            return;
        }
        else{
            gender = (rButtonMale.isSelected()) ? "Male" : "Female";
        }

        // COUNTRY
        country = comboBoxCountry.getValue();
        if(country==null){
            errorLabel.setVisible(true);
            errorLabel.setText("Select your country");
            return;
        }

        // PHONE
        phone = phoneTextField.getText();
        Pattern phonePattern = Pattern.compile("^\\d{10}$");
        Matcher phoneMatcher = phonePattern.matcher(phone);
        if(!phoneMatcher.find()){
            errorLabel.setVisible(true);
            errorLabel.setText("Not valid phone number");
            return;
        }

        // EMAIL
        email = emailTextField.getText();
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(email);
        if(!emailMatcher.find()){
            errorLabel.setVisible(true);
            errorLabel.setText("Not valid email");
            return;
        }

        // DATE
        birthDate = myDatePicker.getValue();
        regDate = LocalDate.now();
        if(birthDate!=null) {
            age = Period.between(birthDate, regDate).getYears();

            // AGE
            if (age < 18) {
                errorLabel.setVisible(true);
                errorLabel.setText("You must be 18+");
                return;
            }
        }
        else{
            errorLabel.setVisible(true);
            errorLabel.setText("Select your birthday");
            return;
        }

        // GENRE
        checkedGenreList = FXCollections.observableArrayList();
        checkedGenreList = checkComboBoxGenres.getItems();
        if(checkedGenreList.isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Select game genres");
            return;
        }

        // CORRECT SUBMIT
        // register in db

        List<GamerListElement> gamerList = new ArrayList<>();
        String salt = RandomStringUtils.randomAscii(8);
        String sha256 = DigestUtils.sha256Hex(password+salt);

        User u = new User(username,firstName,lastName,gender,country,email,phone,birthDate,regDate,gamerList,false, salt, sha256);
        u.insert( checkComboBoxGenres.getValue());

        errorLabel.setText("You are now signed up");
        try{
            if(user!=null && user.getAdmin())
                switchToMyProfile(event);
            else
                switchToSignin(event);
        }
        catch(IOException e){e.printStackTrace();}
    }

    public void switchToSignin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/SigninScene.fxml"));
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        String css = this.getClass().getResource("/css/signinScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMyProfile(ActionEvent event) throws  IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        String username = Session.getInstance().getLoggedUser().getUsername();
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(username, true);

        UtilityMenu.getInstance().bind(newRoot);
        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        user = Session.getInstance().getLoggedUser();
        if(user!=null && user.getAdmin()){
            backButton.setVisible(false);
            backButton.setManaged(false);
        }

        final ObservableList<String> country = FXCollections.observableArrayList();
        for (String l : countryList) {
            country.add(l);
        }
        comboBoxCountry = new ComboBox<>(country);

        final ObservableList<String> genresList = FXCollections.observableArrayList();
        for (String g : genres) {
            genresList.add(g);
        }
        checkComboBoxGenres = new ComboBox<>(genresList);

        gridPane.add(comboBoxCountry,2,6);
        gridPane.add(checkComboBoxGenres,2,10);
    }
}
