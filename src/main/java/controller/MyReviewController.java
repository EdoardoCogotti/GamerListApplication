package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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
        reviewFormController.setEditFlag(true);
        // TO_DO update review in db
    }

    public void deleteMyReview() throws IOException {
        // TO_DO delete review in db

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
