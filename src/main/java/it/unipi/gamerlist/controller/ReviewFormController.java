package it.unipi.gamerlist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import it.unipi.gamerlist.model.Game;
import it.unipi.gamerlist.model.Review;
import it.unipi.gamerlist.utils.Session;

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

        if(editFlag){
            //update my review in db
            Review review = Review.get(gameName, username);
            review.setContent(content);
            review.update();
        }else{
            // add my review in db
            Review review = new Review(
                    gameName,
                    username,
                    new Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    "GamerList",
                    content,
                    0
            );
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
