package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import model.Game;
import model.Review;
import org.neo4j.driver.internal.InternalPath;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

public class ReviewFormController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextArea reviewTextArea;

    private boolean editFlag;

    public void addMyReview() throws IOException {
        String content = reviewTextArea.getText();
        String username = Session.getInstance().getLoggedUser().getUsername();
        String gameName = Session.getInstance().getCurrentGame().getName();

        if(editFlag)
            ;//TO_DO update my review in db
        else{
            // TO_DO FRA add my review in db
            System.out.print("ADD REVIEW :" + username + " " + gameName + " " + content);
            Review review = new Review(
                    gameName,
                    username,
                    new Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    "GamerList",
                    content
            );
            System.out.println(gameName);
            review.insert(Game.getGamesByNamePart(gameName).get(0));
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyReview.fxml"));

        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(loader.load());

        MyReviewController myReviewController = loader.getController();
        myReviewController.setContent(content);
    }

    public void setContent(String content) {
        reviewTextArea.setText(content);
    }
    public void setEditFlag(boolean editFlag) {this.editFlag = editFlag;}
}
