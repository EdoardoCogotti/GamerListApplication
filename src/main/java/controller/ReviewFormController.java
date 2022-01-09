package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ReviewFormController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextArea reviewTextArea;

    public void addMyReview() throws IOException {
        String content = reviewTextArea.getText();
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();
        // TO_DO add my review in db
        System.out.print("ADD REVIEW :" + username + " " + gameName + " " + content);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyReview.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());

        MyReviewController myReviewController = loader.getController();
        myReviewController.setContent(content);
    }

    public void setContent(String content) {
        reviewTextArea.setText(content);
    }
}
