package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import model.Game;
import model.Review;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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
            genresValue, languagesValue, gameDetailsValue, releseDateValue;
    @FXML
    private Button gamelistButton,deleteButton ;

    private boolean inGamelist;
    private boolean reviewed;

    private List<Review> reviews = new ArrayList<>() ;
    private List<Review> getSteamData(){
        List<Review> reviews = new ArrayList<>();

        for(int i=0; i<13; i++){
            Review review = new Review();
            review.setUsername("edo");
            review.setStore("Steam");
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
            review.setCreationDate(LocalDate.now());
            review.setHelpful(3);
            review.setPositive(false);
            reviews.add(review);
        }
        return reviews;
    }
    private List<Review> getGogData(){
        List<Review> reviews = new ArrayList<>();

        for(int i=0; i<13; i++){
            Review review = new Review();
            review.setUsername("edo");
            review.setStore("Gog");
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
            review.setCreationDate(LocalDate.now());
            review.setTitle("Awesome shooter if you do the following");
            review.setRating(3);
            reviews.add(review);
        }
        return reviews;
    }

    private List<Review> getGamerListData(){
        List<Review> reviews = new ArrayList<>();

        for(int i=0; i<10; i++){
            Review review = new Review();
            review.setUsername("edo");
            review.setStore("Gamerlist");
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
            review.setCreationDate(LocalDate.now());
            reviews.add(review);
        }
        return reviews;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*
        Game g = new Game();
        g.setName("Portal 2");
        g.setStore("Steam");
        g.setDeveloper("Valve inc.");
        g.setPublisher("Valve inc.");
        List<String> genres = new ArrayList<>();
        genres.add("adventure");
        genres.add("puzzle");
        g.setGenres(genres);
        List<String> languages = new ArrayList<>();
        languages.add("english");
        languages.add("spanish");
        languages.add("italian");
        g.setLanguages(languages);
        g.setSinglePlayer(true);
        g.setMultiPlayer(false);
        g.setCoop(false);
        g.setControllerSupport(true);
        g.setCloudSaves(false);
        g.setAchievement(true);

        Session.getInstance().setCurrentGame(g);

        gameValue.setText(g.getName());
        storeValue.setText(g.getStore());
        developerValue.setText(g.getDeveloper());
        publisherValue.setText(g.getPublisher());
        StringBuilder strGenre = new StringBuilder();
        for(String genre : g.getGenres())
            strGenre.append(genre).append("; ");
        strGenre.delete(strGenre.length()-2, strGenre.length());
        genresValue.setText(String.valueOf(strGenre));
        StringBuilder strLang = new StringBuilder("");
        for(String lang : g.getLanguages())
            strLang.append(lang).append("; ");
        strLang.delete(strLang.length()-2, strLang.length());
        languagesValue.setText(String.valueOf(strLang));
        JSONObject gameDetails = g.getGameDetails();
        StringBuilder strGameDetails = new StringBuilder();
        Iterator<String> keys = gameDetails.keys();
        //System.out.println("ciao " + gameDetails);
        while(keys.hasNext()){
            String key = keys.next();

            strGameDetails.append(key);
            strGameDetails.append(": ");
            strGameDetails.append(gameDetails.get(key));
            strGameDetails.append("\n");
        }
        strGameDetails.delete(strGameDetails.length()-1, strGameDetails.length());
        gameDetailsValue.setText(String.valueOf(strGameDetails));

        if(g.getStore().equals("Steam")) //a
            reviews.addAll(getSteamData());
        else if(g.getStore().equals("Gog"))
            reviews.addAll(getGogData());
        else
            reviews.addAll(getGamerListData());

        
        int col=0;
        int row=1;
        try {
            for(Review r : reviews){
                FXMLLoader loader = new FXMLLoader();
                if(g.getStore().equals("Steam")) //a
                    loader.setLocation(getClass().getResource("/ReviewItemSteam.fxml"));
                else if(g.getStore().equals("Gog"))
                    loader.setLocation(getClass().getResource("/ReviewItemGoG.fxml"));
                else
                    loader.setLocation(getClass().getResource("/ReviewItemGamerlist.fxml"));
                AnchorPane anchorPane = loader.load();

                ReviewItemController reviewItemController = loader.getController();
                if(g.getStore().equals("Steam")) //a
                    reviewItemController.setSteamData(r);
                else if(g.getStore().equals("Gog"))
                    reviewItemController.setGogData(r);
                else
                    reviewItemController.setGamerlistData(r);

                if(col==3){
                    col=0;
                    row++;
                }

                grid.add(anchorPane, col++, row); // (child, column, row)

                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_COMPUTED_SIZE);

                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(anchorPane, new Insets(20));
            }
        } catch (IOException e) {e.printStackTrace();}

         */

    }

    private void viewReviews(Game game){
        int col=0;
        int row=1;
        try {
            for(Review r : reviews){
                FXMLLoader loader = new FXMLLoader();
                if(game.getStore().equals("Steam")) //a
                    loader.setLocation(getClass().getResource("/ReviewItemSteam.fxml"));
                else if(game.getStore().equals("Gog"))
                    loader.setLocation(getClass().getResource("/ReviewItemGoG.fxml"));
                else
                    loader.setLocation(getClass().getResource("/ReviewItemGamerlist.fxml"));
                AnchorPane anchorPane = loader.load();

                ReviewItemController reviewItemController = loader.getController();
                if(game.getStore().equals("Steam")) //a
                    reviewItemController.setSteamData(r);
                else if(game.getStore().equals("Gog"))
                    reviewItemController.setGogData(r);
                else
                    reviewItemController.setGamerlistData(r);

                if(col==3){
                    col=0;
                    row++;
                }

                grid.add(anchorPane, col++, row); // (child, column, row)

                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_COMPUTED_SIZE);

                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(anchorPane, new Insets(20));
            }
        } catch (IOException e) {e.printStackTrace();}

    }

    public void setGameScene(String name){
        String currentUser = Session.getInstance().getLoggedUser().getUsername();

        //Get game info from db
        Game game = Game.getGamesByNamePart(name).get(0);

        Session.getInstance().setCurrentGame(game);

        this.gameValue.setText(game.getName());
        this.storeValue.setText(game.getStore());
        this.developerValue.setText(game.getDeveloper());
        this.publisherValue.setText(game.getPublisher());
        this.genresValue.setText(game.getGenres().stream()
                .collect(Collectors.joining(", ", "", "")));
        this.languagesValue.setText(game.getLanguages().stream()
                .collect(Collectors.joining(", ", "", "")));
        this.gameDetailsValue.setText(game.getGameDetailsString());
        //TO_DO this.releseDateValue


        //Get reviews from db
        this.reviews = Review.getReviewsByGame(game);
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

            //TO_DO check if game in user gamerlist
            inGamelist = true;
            if (inGamelist)
                gamelistButton.setText("REMOVE FROM GAMELIST");
            else
                gamelistButton.setText("ADD IN GAMELIST");
        }

        //Check if game reviewed by the username
        reviewed = false;
        List<Review> reviewByCurrentUser = Review.getReviewsByUser(currentUser);
        for(Review review : reviewByCurrentUser) {
            System.out.println(review.getContent());
            if(review.getGamename().equals(game.getName())){
                reviewed = true;
            }
        }

        try {
            FXMLLoader loader_review = new FXMLLoader();
            //TO_DO Must initialize the review with the currently selected one
            if (reviewed) {
                loader_review.setLocation(getClass().getResource("/MyReview.fxml"));
            } else {
                loader_review.setLocation(getClass().getResource("/ReviewForm.fxml"));
            }
            AnchorPane anchorPane = loader_review.load();
            myReview.getChildren().add(anchorPane);
        }
        catch (IOException e){e.printStackTrace();}

    }

    public void updateGamelist(){
        if(inGamelist){
            inGamelist=false;
            gamelistButton.setText("ADD IN GAMELIST");
            //TO_DO remove from gamelist
        }
        else{
            inGamelist=true;
            gamelistButton.setText("REMOVE FROM GAMELIST");
            //TO_DO insert in gamelist
        }
    }

    public void deleteGame(){
        String gamename = gameValue.getText();

        //Delete game in db
        Game game = Game.getGamesByNamePart(gamename).get(0);
        game.delete();
    }
}
