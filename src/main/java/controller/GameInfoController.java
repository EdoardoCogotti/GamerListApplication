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

public class GameInfoController implements Initializable {

    @FXML
    private GridPane grid;
    @FXML
    private Pane myReview;
    @FXML
    private Label gameValue, storeValue, developerValue, publisherValue,
            genresValue, languagesValue, gameDetailsValue;
    @FXML
    private Button gamelistButton;

    private boolean inGamelist;
    private boolean reviewed;

    private List<Review> reviews = new ArrayList<>() ;
    private List<Review> getSteamData(){
        List<Review> reviews = new ArrayList<>();

        for(int i=0; i<13; i++){
            Review review = new Review();
            review.setUsername("edo");
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
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
            review.setCreationDate(LocalDate.now());
            reviews.add(review);
        }
        return reviews;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    }

    public void setGameScene(String name){
        gameValue.setText(name);
        //TO_DO get game info from db

        //TO_DO get reviews from db

        //TO_DO check if game in user gamerlist
        inGamelist = true;
        if(inGamelist)
            gamelistButton.setText("REMOVE FROM GAMELIST");
        else
            gamelistButton.setText("ADD IN GAMELIST");

        //TO_DO check if game reviewed by the username
        reviewed= true;
        try {
            FXMLLoader loader_review = new FXMLLoader();
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

}
