package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Review;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ReviewItemController implements Initializable {

    @FXML
    private Label usernameValue, creationDateValue, contentReview;
    @FXML
    private Label helpfulValue;
    @FXML
    private ImageView thumbImage;
    @FXML
    private Label titleValue, ratingValue;
    @FXML
    private HBox usernameBar;
    @FXML
    private VBox reviewVbox;

    private PopOver popOver;
    private Label contentPopup, titlePopup;
    private boolean firstPopup;

    private double targetX, targetY;

    private Review review;
    private Stage stage;
    private Scene scene;

    public void setSteamData(Review review){
        this.review = review;
        usernameValue.setText(review.getUsername());
        contentReview.setText(review.getContent());
        creationDateValue.setText(review.getCreationDate().toString());

        helpfulValue.setText(String.valueOf(review.getHelpful()));
        if(review.getPositive())
            thumbImage.setImage(new Image("/images/Like-icon.png"));
        else
            thumbImage.setImage(new Image("/images/Dislike-icon.png"));
    }

    public void setGogData(Review review){
        this.review = review;
        usernameValue.setText(review.getUsername());
        contentReview.setText(review.getContent());
        creationDateValue.setText(review.getCreationDate().toString());

        titleValue.setText(review.getTitle());
        ratingValue.setText(review.getRating() + "/5");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                    try {
                        switchToUser(mouseEvent, usernameValue.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        firstPopup = true;
        reviewVbox.setOnMouseEntered(mouseEvent -> {
            if(firstPopup){
                firstPopup=false;
                contentPopup = new Label(contentReview.getText());
                contentPopup.setWrapText(true);
                contentPopup.setTextAlignment(TextAlignment.CENTER);
                VBox vBox;
                if(titleValue!=null) {
                    titlePopup = new Label(titleValue.getText());
                    titlePopup.setWrapText(true);
                    titlePopup.setTextAlignment(TextAlignment.CENTER);
                    titlePopup.setStyle("-fx-font-weight: bold;");
                    vBox = new VBox(titlePopup, contentPopup);
                }
                else{
                    vBox = new VBox(contentPopup);
                }
                vBox.setPrefWidth(300);
                vBox.setStyle("-fx-background-color: white;");
                popOver = new PopOver(vBox);
            }
            popOver.show(contentReview);
        });

        reviewVbox.setOnMouseExited(mouseEvent -> {
            popOver.hide();
        });
    }

    public void switchToUser(MouseEvent event, String user) throws IOException {
        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(user, false);

        scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

}
