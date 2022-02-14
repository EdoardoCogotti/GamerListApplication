package it.unipi.gamerlist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import it.unipi.gamerlist.model.Game;
import it.unipi.gamerlist.model.GamerListElement;
import it.unipi.gamerlist.model.Review;
import org.controlsfx.control.GridView;
import it.unipi.gamerlist.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GameInfoController implements Initializable {

    @FXML
    private GridPane grid;
    @FXML
    private Pane myReview;
    @FXML
    private Label gameValue, storeValue, developerValue, publisherValue, 
                gameDetailsValue, releaseDateValue, ratingValue, achievementsValue, totReviewsValue;
    @FXML
    private Text genresValue, languagesValue, urlValue;

    @FXML
    private Label inDevelopmentValue, sizeValue;
    @FXML
    private Text osValue;

    @FXML
    private Text descriptionValue, minimumRequirementValue, recommendRequirementValue;

    @FXML
    private HBox steamBoxRecommendedRequirements, steamBoxMinimumRequirements, steamBoxDescription;
    @FXML
    private HBox gogBoxOses, gogBoxSize, gogBoxInDevelopment;
    @FXML
    private HBox ratingBox;

    @FXML
    private Button gamelistButton,deleteButton ;
    @FXML
    private Button prevButton, nextButton;

    private GridView<AnchorPane> gridView;
    private ObservableList<AnchorPane> anchors = FXCollections.observableArrayList();

    private boolean inGamelist;
    private boolean reviewed;
    private final int ITEM_PER_PAGE=12;
    private int itemCounter;

    private List<Review> reviews = new ArrayList<>() ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void viewReviews(Game game){
        int col=0;
        int row=1;
        int counter=0;
        try {
            for(Review r : reviews){
                FXMLLoader loader = new FXMLLoader();
                if(r.getStore().equals("Steam")) //a
                    loader.setLocation(getClass().getResource("/ReviewItemSteam.fxml"));
                else if(r.getStore().equals("GOG"))
                    loader.setLocation(getClass().getResource("/ReviewItemGoG.fxml"));
                else
                    loader.setLocation(getClass().getResource("/ReviewItemGamerlist.fxml"));
                AnchorPane anchorPane = loader.load();
                ReviewItemController reviewItemController = loader.getController();
                if(r.getStore().equals("Steam")) //a
                    reviewItemController.setSteamData(r);
                else if(r.getStore().equals("GOG"))
                    reviewItemController.setGogData(r);
                else
                    reviewItemController.setGamerlistData(r);

                if(col==3){
                    col=0;
                    row++;
                }

                if(row==5) { //gg
                    break;
                }
                counter++;

                grid.add(anchorPane, col++, row); // (child, column, row)
                GridPane.setMargin(anchorPane, new Insets(20));
            }
            grid.setMinWidth(Region.USE_COMPUTED_SIZE);
            grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
            grid.setMaxWidth(Region.USE_COMPUTED_SIZE);

            grid.setMinHeight(Region.USE_COMPUTED_SIZE);
            grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
            grid.setMaxHeight(Region.USE_COMPUTED_SIZE);
        } catch (Exception e) {e.printStackTrace();}

        prevButton.setDisable(true);
        if(reviews.size()<=ITEM_PER_PAGE)
            nextButton.setDisable(true);
    }

    public void setGameScene(String name){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);

        String currentUser = Session.getInstance().getLoggedUser().getUsername();

        //Get game info from db
        Game game = Game.getGamesByNamePart(name).get(0);

        Session.getInstance().setCurrentGame(game);

        this.gameValue.setText(game.getName());
        this.urlValue.setText(game.getUrl());
        this.releaseDateValue.setText(df.format(game.getReleaseDate()));
        this.storeValue.setText(game.getStore());
        this.developerValue.setText(game.getDeveloper());
        this.publisherValue.setText(game.getPublisher());
        this.genresValue.setText(game.getGenres().stream()
                .collect(Collectors.joining(", ", "", "")));
        this.languagesValue.setText(game.getLanguages().stream()
                .collect(Collectors.joining(", ", "", "")));
        this.gameDetailsValue.setText(game.getGameDetailsString());
        this.totReviewsValue.setText(String.valueOf(game.getTotReviews()));
        this.achievementsValue.setText(String.valueOf(game.getAchievements()));
        this.ratingValue.setText(game.getRating());
        if(this.ratingValue.getText()==null){
            ratingBox.setManaged(false);
            ratingBox.setVisible(false);
        }
        if(game.getStore().equals("GOG")){
            this.osValue.setText(game.getOses().stream().collect(Collectors.joining(", ", "", "")));
            this.sizeValue.setText(game.getSize());
            this.inDevelopmentValue.setText((game.getInDevelopment())?"true":"false");

            steamBoxDescription.setManaged(false);
            steamBoxMinimumRequirements.setManaged(false);
            steamBoxRecommendedRequirements.setManaged(false);
            steamBoxDescription.setVisible(false);
            steamBoxMinimumRequirements.setVisible(false);
            steamBoxRecommendedRequirements.setVisible(false);
        }
        if(game.getStore().equals("Steam")){
            this.descriptionValue.setText(game.getGameDescription());
            this.minimumRequirementValue.setText(game.getMinimumRequirements());
            this.recommendRequirementValue.setText(game.getRecommendedRequirements());

            gogBoxSize.setManaged(false);
            gogBoxInDevelopment.setManaged(false);
            gogBoxOses.setManaged(false);
            gogBoxSize.setVisible(false);
            gogBoxInDevelopment.setVisible(false);
            gogBoxOses.setVisible(false);
        }

        //Get reviews from db
        long before = System.currentTimeMillis();
        this.reviews = Review.getReviewsByGame(game);
        long later = System.currentTimeMillis();
        System.out.println("review time:" + (later-before));
        //exclude review by current user (it's visualize on top instead)
        Review toRemove = null;
        for(Review review : this.reviews) {
            if(review.getUsername().equals(currentUser)){
                toRemove = review;
            }
        }
        this.reviews.remove(toRemove);
        this.viewReviews(game);

        if(Session.getInstance().getLoggedUser().getAdmin()) {
            gamelistButton.setVisible(false);
            gamelistButton.setManaged(false);
        }else {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);

            //check if game in user gamerlist
            inGamelist = Session.getInstance().getLoggedUser().searchInGameList(game.getName()); //true;
            if (inGamelist)
                gamelistButton.setText("REMOVE FROM GAMELIST");
            else
                gamelistButton.setText("ADD IN GAMELIST");
        }

        //Check if game reviewed by the username
        reviewed = false;
        List<Review> reviewByCurrentUser = Review.getReviewsByUser(currentUser);
        for(Review review : reviewByCurrentUser) {
            if(review.getGamename().equals(game.getName())){
                reviewed = true;
            }
        }

        //before = System.currentTimeMillis();
        try {
            FXMLLoader loader_review = new FXMLLoader();
            AnchorPane anchorPane;
            //Must initialize the review with the currently selected one
            if (reviewed) {
                loader_review.setLocation(getClass().getResource("/MyReview.fxml"));
                anchorPane = loader_review.load();
            } else {
                loader_review.setLocation(getClass().getResource("/ReviewForm.fxml"));
                anchorPane = loader_review.load();

                ReviewFormController reviewFormController = loader_review.getController();
                reviewFormController.setEditFlag(false); //it won't be reviewed in this case
            }
            myReview.getChildren().add(anchorPane);
        }
        catch (IOException e){e.printStackTrace();}

        //later = System.currentTimeMillis();
        //System.out.println("personal review: "+ (later-before));
    }

    public void updateGamelist(){
        if(inGamelist){
            inGamelist=false;
            gamelistButton.setText("ADD IN GAMELIST");

            //remove from gamelist
            Session.getInstance().getLoggedUser().removeFromGamerList(gameValue.getText());
        }
        else{
            inGamelist=true;
            gamelistButton.setText("REMOVE FROM GAMELIST");

            //insert in gamelist
            GamerListElement gle = new GamerListElement(gameValue.getText(), publisherValue.getText(), developerValue.getText(), -1);
            Session.getInstance().getLoggedUser().insertInGamelist(gle);
        }
    }

    public void deleteGame(){
        String gamename = gameValue.getText();

        //Delete game in db
        Game game = Game.getGamesByNamePart(gamename).get(0);
        game.delete();
    }

    public void prev(){
        newPage(true);
        nextButton.setDisable(false);
    }
    public void next(){
        newPage(false);
        prevButton.setDisable(false);
    }

    public void newPage(boolean isPrev) {
        grid.getChildren().clear();
        int col=0;
        int row=1;
        int counter;
        if(isPrev)
            counter=itemCounter-ITEM_PER_PAGE;
        else
            counter=itemCounter+ITEM_PER_PAGE;

        int toIndex = Math.min(counter+ITEM_PER_PAGE,reviews.size());
        if(reviews.size()<=counter+ITEM_PER_PAGE)
            nextButton.setDisable(true);
        if(counter<=0){
            counter=0;
            prevButton.setDisable(true);
        }
        try {
            for(Review r : reviews.subList(counter,toIndex)){
                FXMLLoader loader = new FXMLLoader();
                AnchorPane anchorPane=null;
                if(r.getStore().equals("Steam"))//a
                    loader.setLocation(getClass().getResource("/ReviewItemSteam.fxml"));
                else if(r.getStore().equals("Gog"))
                    loader.setLocation(getClass().getResource("/ReviewItemGoG.fxml"));
                else
                    loader.setLocation(getClass().getResource("/ReviewItemGamerlist.fxml"));
                anchorPane = loader.load();

                ReviewItemController reviewItemController = loader.getController();
                if(r.getStore().equals("Steam")) //a
                    reviewItemController.setSteamData(r);
                else if(r.getStore().equals("Gog"))
                    reviewItemController.setGogData(r);
                else
                    reviewItemController.setGamerlistData(r);

                if(col==3){
                    col=0;
                    row++;
                }

                grid.add(anchorPane, col++, row); // (child, column, row)

                GridPane.setMargin(anchorPane, new Insets(20));
            }
        } catch (Exception e) {e.printStackTrace();}

        if(isPrev)
            itemCounter-=ITEM_PER_PAGE;
        else
            itemCounter+=ITEM_PER_PAGE;
    }
}
