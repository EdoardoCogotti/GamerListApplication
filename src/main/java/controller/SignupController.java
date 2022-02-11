package controller;

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
import model.GamerListElement;
import model.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import utils.Session;
import utils.UtilityMenu;

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
    // interesting to use TreeView and MenuBar

    private ComboBox<String> comboBoxCountry;
    private ComboBox<String> checkComboBoxGenres;
    private ObservableList<String> checkedGenreList;

    private String[] countryList = //{"Italy", "France", "Spain"};
        {"Aargau", "Acre", "Adana", "Adıyaman", "Afyonkarahisar", "Ain", "Aisne", "Akershus", "Aksaray", "Alabama", "Alagoas", "Alaska", "Alberta", "Allier", "Alpes-Maritimes", "Alpes-de-Haute-Provence", "Amapá", "Amasya", "Amazonas", "Andalucía", "Ankara", "Antalya", "Appenzell Ausserrhoden", "Appenzell Innerrhoden", "Aragón", "Ardahan", "Ardennes", "Ardèche", "Arizona", "Ariège", "Arkansas", "Artvin", "Asturias", "Aube", "Auckland", "Aude", "Aust-Agder", "Australian Capital Territory", "Aveyron", "Avon", "Aydın", "Ağrı", "Baden-Württemberg", "Bahia", "Balıkesir", "Bartın", "Bas-Rhin", "Basel-Landschaft", "Basel-Stadt", "Batman", "Bay of Plenty", "Bayburt", "Bayern", "Bedfordshire", "Bergen", "Berkshire", "Berlin", "Bern", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Borders", "Bouches-du-Rhône", "Brandenburg", "Bremen", "British Columbia", "Buckinghamshire", "Burdur", "Bursa", "Buskerud", "California", "Calvados", "Cambridgeshire", "Canarias", "Cantabria", "Cantal", "Canterbury", "Carlow", "Castilla la Mancha", "Castilla y León", "Cataluña", "Cavan", "Ceará", "Central", "Central Finland", "Central Ostrobothnia", "Ceuta", "Charente", "Charente-Maritime", "Cher", "Cheshire", "Clare", "Cleveland", "Clwyd", "Colorado", "Comunidad Valenciana", "Comunidad de Madrid", "Connecticut", "Cork", "Cork City", "Cornwall", "Corrèze", "Corse-du-Sud", "County Antrim", "County Armagh", "County Down", "County Fermanagh", "County Londonderry", "County Tyrone", "Creuse", "Cumbria", "Côte-D'Or", "Côtes-D'Armor", "Danmark", "Delaware", "Denizli", "Derbyshire", "Description", "Deux-Sèvres", "Devon", "Distrito Federal", "Diyarbakır", "Donegal", "Dordogne", "Dorset", "Doubs", "Drenthe", "Drôme", "Dublin City", "Dumfries and Galloway", "Durham", "Dyfed", "Dún Laoghaire–Rathdown", "East Sussex", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Espírito Santo", "Essex", "Essonne", "Eure", "Eure-et-Loir", "Extremadura", "Fife", "Fingal", "Finistère", "Finland Proper", "Finnmark - Finnmárku", "Flevoland", "Florida", "Fribourg", "Friesland", "Galicia", "Galway", "Galway City", "Gard", "Gaziantep", "Gelderland", "Genève", "Georgia", "Gers", "Giresun", "Gironde", "Gisborne", "Glarus", "Gloucestershire", "Goiás", "Grampian", "Graubünden", "Greater Manchester", "Groningen", "Guadeloupe", "Guyane", "Gwent", "Gwynedd County", "Gümüşhane", "Hakkâri", "Hamburg", "Hampshire", "Hatay", "Haut-Rhin", "Haute-Corse", "Haute-Garonne", "Haute-Loire", "Haute-Marne", "Haute-Savoie", "Haute-Saône", "Haute-Vienne", "Hautes-Alpes", "Hautes-Pyrénées", "Hauts-de-Seine", "Hawaii", "Hawke'S Bay", "Hedmark", "Herefordshire", "Hertfordshire", "Hessen", "Highlands and Islands", "Hordaland", "Hovedstaden", "Humberside", "Hérault", "Idaho", "Ille-et-Vilaine", "Illinois", "Indiana", "Indre", "Indre-et-Loire", "Iowa", "Islas Baleares", "Isle of Wight", "Isparta", "Isère", "Iğdır", "Jura", "Kahramanmaraş", "Kainuu", "Kansas", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kent", "Kentucky", "Kerry", "Kildare", "Kilis", "Kilkenny", "Kocaeli", "Konya", "Kymenlaakso", "Kütahya", "Kırklareli", "Kırıkkale", "Kırşehir", "La Rioja", "La Réunion", "Lancashire", "Landes", "Laois", "Lapland", "Leicestershire", "Leitrim", "Limburg", "Limerick", "Lincolnshire", "Loir-et-Cher", "Loire", "Loire-Atlantique", "Loiret", "Longford", "Lot", "Lot-et-Garonne", "Lothian", "Louisiana", "Louth", "Lozère", "Luzern", "Maine", "Maine-et-Loire", "Malatya", "Manawatu-Wanganui", "Manche", "Manisa", "Manitoba", "Maranhão", "Mardin", "Marlborough", "Marne", "Martinique", "Maryland", "Massachusetts", "Mato Grosso", "Mato Grosso do Sul", "Mayenne", "Mayo", "Mayotte", "Meath", "Mecklenburg-Vorpommern", "Melilla", "Merseyside", "Mersin", "Meurthe-et-Moselle", "Meuse", "Michigan", "Mid Glamorgan", "Midtjylland", "Minas Gerais", "Minnesota", "Mississippi", "Missouri", "Monaghan", "Montana", "Morbihan", "Moselle", "Muğla", "Muş", "Møre og Romsdal", "Navarra", "Nebraska", "Nelson", "Neuchâtel", "Nevada", "Nevşehir", "New Brunswick", "New Hampshire", "New Jersey", "New Mexico", "New South Wales", "New York", "Newfoundland and Labrador", "Nidwalden", "Niedersachsen", "Nièvre", "Niğde", "Noord-Brabant", "Noord-Holland", "Nord", "Nord-Trøndelag", "Nordjylland", "Nordland", "Nordrhein-Westfalen", "Norfolk", "North Carolina", "North Dakota", "North Karelia", "North Yorkshire", "Northamptonshire", "Northern Ostrobothnia", "Northern Savonia", "Northern Territory", "Northland", "Northumberland", "Northwest Territories", "Nottinghamshire", "Nova Scotia", "Nunavut", "Obwalden", "Offaly", "Ohio", "Oise", "Oklahoma", "Ontario", "Oppland", "Ordu", "Oregon", "Orne", "Oslo", "Osmaniye", "Ostrobothnia", "Otago", "Overijssel", "Oxfordshire", "Paraná", "Paraíba", "Paris", "Pará", "Pas-de-Calais", "País Vasco", "Pennsylvania", "Pernambuco", "Piauí", "Pirkanmaa", "Powys", "Prince Edward Island", "Puy-de-Dôme", "Pyrénées-Atlantiques", "Pyrénées-Orientales", "Päijät-Häme", "Queensland", "Québec", "Región de Murcia", "Rheinland-Pfalz", "Rhode Island", "Rhône", "Rio Grande do Norte", "Rio Grande do Sul", "Rio de Janeiro", "Rize", "Rogaland", "Rondônia", "Roraima", "Roscommon", "Rutland", "Saarland", "Sachsen", "Sachsen-Anhalt", "Sakarya", "Samsun", "Santa Catarina", "Sarthe", "Saskatchewan", "Satakunta", "Savoie", "Saône-et-Loire", "Schaffhausen", "Schleswig-Holstein", "Schwyz", "Seine-Maritime", "Seine-Saint-Denis", "Seine-et-Marne", "Sergipe", "Shropshire", "Siirt", "Sinop", "Sivas", "Sjælland", "Sligo", "Sogn og Fjordane", "Solothurn", "Somerset", "Somme", "South Australia", "South Carolina", "South Dakota", "South Dublin", "South Glamorgan", "South Karelia", "South Yorkshire", "Southern Ostrobothnia", "Southern Savonia", "Southland", "St. Gallen", "Staffordshire", "Strathclyde", "Suffolk", "Surrey", "Syddanmark", "São Paulo", "Sør-Trøndelag", "Taranaki", "Tarn", "Tarn-et-Garonne", "Tasman", "Tasmania", "Tavastia Proper", "Tayside", "Tekirdağ", "Telemark", "Tennessee", "Territoire De Belfort", "Texas", "Thurgau", "Thüringen", "Ticino", "Tipperary", "Tocantins", "Tokat", "Trabzon", "Troms - Romsa", "Trøndelag", "Tunceli", "Tyne and Wear", "Uri", "Utah", "Utrecht", "Uusimaa", "Uşak", "Val-D'Oise", "Val-de-Marne", "Valais", "Van", "Var", "Vaucluse", "Vaud", "Vendée", "Vermont", "Vest-Agder", "Vestfold", "Victoria", "Vienne", "Virginia", "Vosges", "Waikato", "Warwickshire", "Washington", "Waterford", "Wellington", "West Coast", "West Glamorgan", "West Midlands", "West Sussex", "West Virginia", "West Yorkshire", "Western Australia", "Westmeath", "Wexford", "Wicklow", "Wiltshire", "Wisconsin", "Worcestershire", "Wyoming", "Yalova", "Yonne", "Yozgat", "Yukon", "Yvelines", "Zeeland", "Zonguldak", "Zug", "Zuid-Holland", "Zürich", "Åland", "Çanakkale", "Çankırı", "Çorum", "İstanbul", "İzmir", "Şanlıurfa", "Şırnak", "آذربایجان شرقی", "آذربایجان غربی", "اردبیل", "اصفهان", "البرز", "ایلام", "بوشهر", "تهران", "خراسان جنوبی", "خراسان رضوی", "خراسان شمالی", "خوزستان", "زنجان", "سمنان", "سیستان و بلوچستان", "فارس", "قزوین", "قم", "لرستان", "مازندران", "مرکزی", "هرمزگان", "همدان", "چهارمحال و بختیاری", "کردستان", "کرمان", "کرمانشاه", "کهگیلویه و بویراحمد", "گلستان", "گیلان", "یزد"};
    private final String[] genres =
            //{"action", "adventure",  "card game", "fps", "indie", "platform", "puzzle", "turn-based", "fantasy", "strategy"};
            {"action", "adventure", "animation & modeling", "arcade", "audio production", "building", "casual", "combat", "comedy",
                    "design & illustration", "detective-mystery", "early access", "economic", "education", "educational", "espionage", "fpp",
                    "fantasy", "fighting", "free to play", "game development", "hidden object", "historical", "horror", "indie",  "managerial",
                    "massively multiplayer", "metroidvania", "modern", "mystery", "narrative", "naval", "off-road", "open world", "photo editing",
                    "pinball", "platformer", "point-and-click",  "puzzle", "rpg", "racing", "rally", "real-time", "roguelike", "role-playing",
                    "sandbox", "sci-fi",  "shooter", "simulation", "soccer", "software training", "sports", "stealth", "strategy", "survival",
                    "tpp", "tactical", "turn-based", "utilities", "video production", "virtual life", "visual novel", "web publishing"};

    private int age;

    private Stage stage;
    private Scene scene;

    private User user;


    public void submit(ActionEvent event){

        String username, password, firstName, lastName, gender, email, phone, country;
        int age;
        LocalDate birthDate, regDate;

        // USERNAME
        // DONE check user existence in db
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
        // DONE registra in db

        List<GamerListElement> gamerList = new ArrayList<>();
        List<String> following = new ArrayList<>();
        List<String> followed = new ArrayList<>();

        String salt = RandomStringUtils.randomAscii(8);
        String sha256 = DigestUtils.sha256Hex(password+salt);

        User u = new User(username,firstName,lastName,gender,country,email,phone,birthDate,regDate,gamerList,false, salt, sha256, checkComboBoxGenres.getValue());
        u.insert();


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
