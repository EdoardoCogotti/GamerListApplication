package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Review;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyReviewController {

    @FXML
    private Label contentReview;
    @FXML
    private AnchorPane anchorPane;

    public void editMyReview() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReviewForm.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());

        ReviewFormController reviewFormController = loader.getController();
        reviewFormController.setContent(contentReview.getText());

        // TO_DO FRA update review in db
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();

        Review review = Review.get(gameName, username);
        review.setContent(contentReview.getText());
        review.update();
    }

    public void deleteMyReview() throws IOException {
        // TO_DO FRA delete review in db
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();

        Review review = Review.get(gameName, username);
        review.delete();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReviewForm.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());
    }

    public void setContent(String content) {
        contentReview.setText(content);
    }
}
