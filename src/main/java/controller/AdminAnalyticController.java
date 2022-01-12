package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminAnalyticController implements Initializable {

    @FXML
    private Label minReviewScoreLabel,meanReviewScoreLabel, maxReviewScoreLabel;
    @FXML
    private ListView<String> userList;
    @FXML
    private TextField searchBar;
    @FXML
    private RadioButton rButtonLastYear, rButtonAlways;
    @FXML
    private HBox statHBox;

    private String[] users = {"Mike98", "Raven86", "Sabaku88", "Cydonia", "Edoardo97", "Beba01", "Francesco97", "Anna97"};
    private ObservableList user = FXCollections.observableArrayList();
    private String currentUser;

    private Stage stage;
    private Scene scene;

    public void searchUsers(ActionEvent event){

        user.clear();

        // TO_DO search users in db
        String searchedString = searchBar.getText();

        for(String u : users)
            user.add(u);

        userList.setItems(user);

    }

    public void initialize(URL location, ResourceBundle resources) {

        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                currentUser = userList.getSelectionModel().getSelectedItem();

            }
        });

        userList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                    try {

                        if(rButtonLastYear.isSelected() || rButtonAlways.isSelected()) {
                            //TO_DO find analytic of current User
                            int minReviewScore = 3;
                            int meanReviewScore = 20;
                            int maxReviewScore = 50;

                            minReviewScoreLabel.setText(String.valueOf(minReviewScore));
                            meanReviewScoreLabel.setText(String.valueOf(meanReviewScore));
                            maxReviewScoreLabel.setText(String.valueOf(maxReviewScore));

                            setColor(minReviewScoreLabel, 10);
                            setColor(meanReviewScoreLabel, 30);
                            setColor(maxReviewScoreLabel, 100);

                            statHBox.setVisible(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void switchToUser(ActionEvent event) throws IOException {
        
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUserEditScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        AdminUserEditController adminUserEditController = loader.getController();
        adminUserEditController.displayInfo(currentUser);

        scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();
    }

    private void setColor(Label label, int goldValue){
        int value = Integer.parseInt(label.getText());
        if(value<0.5*goldValue)
            label.setStyle("-fx-text-fill: red");
        else if(value<goldValue)
            label.setStyle("-fx-text-fill: white");
        else
            label.setStyle("-fx-text-fill: gold");
    }
}
