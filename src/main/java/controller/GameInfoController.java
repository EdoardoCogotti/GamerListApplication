package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import model.Game;
import model.Review;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.cell.ColorGridCell;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private List<Review> getSteamData(){
        List<Review> reviews = new ArrayList<>();

        for(int i=0; i<13; i++){
            Review review = new Review();
            review.setUsername("edo");
            review.setStore("Steam");
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti" +
                    "Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti" +
                    "Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
            //review.setContent("Un bel giochino, davvero niente male complimenti");
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
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti"
            + "Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
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
            review.setContent("Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti"
            +"Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti Un bel giochino, davvero niente male complimenti");
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
        g.setUrl("https://store.steampowered.com/app/10090/Call_of_Duty_World_at_War/");
        g.setStore("Steam");
        g.setDeveloper("Valve inc.");
        g.setPublisher("Valve inc.");
        List<String> genres = new ArrayList<>();
        genres.add("adventure");
        for(int i=0; i<6; i++){
            genres.add("puzzle");
        }

        g.setGenres(genres);
        List<String> languages = new ArrayList<>();
        languages.add("english");
        languages.add("spanish");
        languages.add("italian");
        for(int i=0; i<5; i++){
            languages.add("japanese");
        }
        g.setLanguages(languages);
        g.setSinglePlayer(true);
        g.setMultiPlayer(false);
        g.setCoop(false);
        g.setControllerSupport(true);
        g.setCloudSaves(false);
        g.setAchievement(true);

        g.setAchievement(10);
        g.setRating("PEGI Rating: 12+ (Violence)");

        g.setSize("2.5 GB");
        g.setInDevelopment(false);
        List<String> oses = new ArrayList<>();
        for(int i=0; i<3; i++)
            oses.add("Windows (7 8 10)");
        g.setOses(oses);

        g.setGameDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In pretium mauris quis sollicitudin egestas. Fusce ultricies vitae erat in congue. Morbi id augue lobortis, ornare erat ultrices, maximus odio. Sed et lorem sed lacus porta ullamcorper eu id sem. Nam ac pharetra orci, sed elementum purus. Donec sed purus et risus fringilla condimentum id nec enim. Donec venenatis ut orci at vulputate. Cras tempus semper mauris at lobortis. Nulla facilisi. Phasellus eu mi quis lectus commodo interdum ornare nec velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. ");
        g.setMinimumRequirements("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In pretium mauris quis sollicitudin egestas. Fusce ultricies vitae erat in congue. Morbi id augue lobortis, ornare erat ultrices, maximus odio. Sed et lorem sed lacus porta ullamcorper eu id sem. Nam ac pharetra orci, sed elementum purus. Donec sed purus et risus fringilla condimentum id nec enim. Donec venenatis ut orci at vulputate. Cras tempus semper mauris at lobortis. Nulla facilisi. Phasellus eu mi quis lectus commodo interdum ornare nec velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. ");
        g.setRecommendedRequirements("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In pretium mauris quis sollicitudin egestas. Fusce ultricies vitae erat in congue. Morbi id augue lobortis, ornare erat ultrices, maximus odio. Sed et lorem sed lacus porta ullamcorper eu id sem. Nam ac pharetra orci, sed elementum purus. Donec sed purus et risus fringilla condimentum id nec enim. Donec venenatis ut orci at vulputate. Cras tempus semper mauris at lobortis. Nulla facilisi. Phasellus eu mi quis lectus commodo interdum ornare nec velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. ");

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
        ratingValue.setText(g.getRating());
        achievementsValue.setText(String.valueOf(g.getAchievements()));

        if(g.getStore().equals("Gog")) {
            sizeValue.setText(String.valueOf(g.getSize()));
            inDevelopmentValue.setText(String.valueOf(g.getInDevelopment()));
            List<String> osList = g.getOses();
            StringBuilder s = new StringBuilder();
            for (String os : osList)
                s.append(os).append(",\n");
            osValue.setText(s.toString());

            steamBoxDescription.setManaged(false);
            steamBoxMinimumRequirements.setManaged(false);
            steamBoxRecommendedRequirements.setManaged(false);
            steamBoxDescription.setVisible(false);
            steamBoxMinimumRequirements.setVisible(false);
            steamBoxRecommendedRequirements.setVisible(false);
        }
        else{
            descriptionValue.setText(g.getGameDescription());
            minimumRequirementValue.setText(g.getMinimumRequirements());
            recommendRequirementValue.setText(g.getRecommendedRequirements());

            gogBoxSize.setManaged(false);
            gogBoxInDevelopment.setManaged(false);
            gogBoxOses.setManaged(false);
            gogBoxSize.setVisible(false);
            gogBoxInDevelopment.setVisible(false);
            gogBoxOses.setVisible(false);
        }

        if(g.getStore().equals("Steam")) //a
            reviews.addAll(getSteamData());
        else if(g.getStore().equals("Gog"))
            reviews.addAll(getGogData());
        else
            reviews.addAll(getGamerListData());
        */


        /*
        int col=0;
        int row=1;
        try {
            for(Review r : reviews){
                FXMLLoader loader = new FXMLLoader();
                AnchorPane anchorPane;
                if(r.getStore().equals("Steam")) {
                    loader.setLocation(getClass().getResource("/ReviewItemSteam.fxml"));
                }
                else if(r.getStore().equals("Gog")) {
                    loader.setLocation(getClass().getResource("/ReviewItemGoG.fxml"));
                }
                else {
                    loader.setLocation(getClass().getResource("/ReviewItemGamerlist.fxml"));
                }
                anchorPane = loader.load();

                // TRE LOADER
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

        /*
        gridView = new GridView<>(anchors);
        reviewVBox.getChildren().add(gridView);
        gridView.setCellFactory(new Callback<GridView<AnchorPane>, GridCell<AnchorPane>>() {
            public GridCell<AnchorPane> call(GridView<AnchorPane> gridView) {
                return new AnchorPane();
            }
        });
         */

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

                /*
                anchors.add(anchorPane);
                gridView.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridView.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridView.setMaxWidth(Region.USE_COMPUTED_SIZE);
                gridView.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridView.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridView.setMaxHeight(Region.USE_COMPUTED_SIZE);
                */

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
            //System.out.println(review.getContent());
            if(review.getGamename().equals(game.getName())){
                reviewed = true;
            }
        }

        before = System.currentTimeMillis();
        try {
            FXMLLoader loader_review = new FXMLLoader();
            AnchorPane anchorPane;
            //TO_DO Must initialize the review with the currently selected one
            if (reviewed) {
                loader_review.setLocation(getClass().getResource("/MyReview.fxml"));
                anchorPane = loader_review.load();

                //MyReviewController myReviewController = loader_review.getController();
                //myReviewController.setContent(content);
            } else {
                loader_review.setLocation(getClass().getResource("/ReviewForm.fxml"));
                anchorPane = loader_review.load();

                ReviewFormController reviewFormController = loader_review.getController();
                reviewFormController.setEditFlag(false); //it won't be reviewed in this case
            }
            myReview.getChildren().add(anchorPane);
        }
        catch (IOException e){e.printStackTrace();}

        later = System.currentTimeMillis();
        System.out.println("personal review: "+ (later-before));
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
