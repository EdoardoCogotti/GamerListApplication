package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Review;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyReviewController implements Initializable {

    @FXML
    private Label contentReview;
    @FXML
    private AnchorPane anchorPane;

    public void initialize(URL location, ResourceBundle resources) {
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();

        Review review = Review.get(gameName, username);

        System.out.println(contentReview.getText());
        System.out.println(review.getContent());
        //If a review was already made, display it
        if(review != null){
            contentReview.setText(review.getContent());
        }
    }

    public void editMyReview() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReviewForm.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());

        ReviewFormController reviewFormController = loader.getController();
        reviewFormController.setContent(contentReview.getText());
        reviewFormController.setEditFlag(true);
    }

    public void deleteMyReview() throws IOException {
        // DONE FRA delete review in db
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();

        Review review = Review.get(gameName, username);
        review.delete();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReviewForm.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());

        ReviewFormController reviewFormController = loader.getController();
        reviewFormController.setEditFlag(false);
    }

    public void setContent(String content) {
        contentReview.setText(content);
    }
}
