package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
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
    private ChoiceBox<String> countryChoiceBox;
    // interesting to use TreeView and MenuBar

    // TO_DO load from json https://github.com/samayo/country-json/blob/master/src/country-by-name.json
    private String[] countryList = {"Italy", "France", "Spain"};

    private int age;

    private Stage stage;
    private Scene scene;


    public void submit(ActionEvent event){

        String username, password, firstName, lastName, gender, email, phone, country;
        int age;
        LocalDate birthDate, regDate;

        // USERNAME
        username = usernameTextField.getText();
        // TO_DO check user existence in db

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

        // COUNTRY
        country = countryChoiceBox.getValue();
        if(country==null){
            errorLabel.setVisible(true);
            errorLabel.setText("Select your country");
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

        // CORRECT SUBMIT
        // TO_DO registra in db
        errorLabel.setText("You are now signed up");
        try{
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        countryChoiceBox.getItems().addAll(countryList);
    }
}
