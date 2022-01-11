package model;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.MongoDriver;

public class Game {

    //General parameters
    private int id;
    private String store;
    private String url;
    private String name;
    private Date release_date;
    private String developer;
    private String publisher;
    private List<String> languages;
    private int achievements;
    private List<String> genres;
    private String rating;
    private int tot_reviews;

    //General parameters: details
    private boolean single_player;
    private boolean multi_player;
    private boolean coop;
    private boolean controller_support;
    private boolean cloud_saves;
    private boolean achievement;

    //GOG specific parameters
    private double player_rating;
    private List<String> oses;
    private String size;
    private boolean in_development;

    //STEAM specific parameters
    private String game_description;
    private String minimum_requirements;
    private String recommended_requirements;

    private List<ReviewCompact> reviews;

    public Game(){

    }

    public Game(
            int new_id,
            String new_store,
            String new_url,
            String new_name,
            Date new_release_date,
            String new_developer,
            String new_publisher,
            ArrayList<String> new_languages,
            int new_achievements,
            ArrayList<String> new_genres,
            String new_rating,
            int new_tot_reviews,
            boolean new_single_player,
            boolean new_multi_player,
            boolean new_coop,
            boolean new_controller_support,
            boolean new_cloud_saves,
            boolean new_achievement,
            double new_player_rating,
            ArrayList<String> new_oses,
            String new_size,
            boolean new_in_development,
            String new_game_description,
            String new_minimum_requirements,
            String new_recommended_requirements
    ){
        this.id = new_id;
        this.store = new_store;
        this.url = new_url;
        this.name = new_name;
        this.release_date = new_release_date;
        this.developer = new_developer;
        this.publisher = new_publisher;
        this.languages = new_languages;
        this.achievements = new_achievements;
        this.genres = new_genres;
        this.rating = new_rating;
        this.tot_reviews = new_tot_reviews;
        this.single_player = new_single_player;
        this.multi_player = new_multi_player;
        this.coop = new_coop;
        this.controller_support = new_controller_support;
        this.cloud_saves = new_cloud_saves;
        this.achievement = new_achievement;
        this.player_rating = new_player_rating;
        this.oses = new_oses;
        this.size = new_size;
        this.in_development = new_in_development;
        this.game_description = new_game_description;
        this.minimum_requirements = new_minimum_requirements;
        this.recommended_requirements = new_recommended_requirements;
    }

    public Game(Document doc){
        //We always copy in the object the fileds that are present both in GOG and Stream
        this.name = doc.getString("name");
        //System.out.println(this.name);

        this.id = doc.getInteger("_id");
        this.store = doc.getString("store");
        this.url = doc.getString("url");
        this.name = doc.getString("name");
        this.release_date = doc.getDate("release_date");
        this.developer = doc.getString("developer");
        this.publisher = doc.getString("publisher");
        this.tot_reviews = doc.getInteger("tot_reviews");
        this.genres = doc.getList("genres", String.class);
        this.rating = doc.getString("rating");
        this.tot_reviews = doc.getInteger("tot_reviews");
        this.single_player = ((Document)doc.get("game_details")).getBoolean("single_player");
        this.multi_player = ((Document)doc.get("game_details")).getBoolean("multi_player");
        this.coop = ((Document)doc.get("game_details")).getBoolean("coop");
        this.controller_support = ((Document)doc.get("game_details")).getBoolean("controller_support");
        this.cloud_saves = ((Document)doc.get("game_details")).getBoolean("cloud_saves");
        this.achievement = ((Document)doc.get("game_details")).getBoolean("achievement");
        this.languages = doc.getList("languages", String.class);

        //if we have a GOG game, we import some fields over others
        if(this.store.equals("GOG")){
            this.player_rating = doc.getDouble("player_rating");
            this.oses = new ArrayList<String>(doc.getList("oses", String.class));
            this.size = doc.getString("size");
            this.in_development = doc.getBoolean("in_development");
        }

        //if we have a Steam game, we import steam specific fields
        if(this.store.equals("Steam")){
            this.game_description = doc.getString("game_description");
            this.minimum_requirements = doc.getString("minimum_requirements");
            this.recommended_requirements = doc.getString("recommended_requirements");
        }

        //Load all the reviews
        ArrayList<Document> reviews = (ArrayList<Document>) doc.get("reviews");
        this.reviews = new ArrayList<ReviewCompact>();
        int rating = -1, helpful = -1;
        boolean positive = false;
        String username = "";
        for(Document review :reviews){
            String store = (review.getInteger("rating") == null)?"Steam":"GOG";
            Date creation_date = review.getDate("creation_date");
            if(store.equals("GOG")){
                rating = review.getInteger("rating");
                username= review.getString("name");
            }else{
                helpful =  Integer.parseInt(review.getString("helpful"));
                positive =  review.getString("rating") == "1";
            }
            this.reviews.add(new ReviewCompact(store, creation_date, username, rating, helpful, positive));
        }
        //System.out.println(this.reviews.get(1).positive);
    }

    // setter e getter
    //SET functions
    public void setId(int id){
        this.id = id;
    }

    public void setStore (String newValue){ this.store = newValue;}

    public void setUrl(String newValue){    this.url = newValue;}

    public void setName (String newValue){  this.name = newValue;}

    public void setReleaseDate(Date newValue){  this.release_date = newValue;}

    public void setDeveloper(String newValue){  this.developer = newValue;}

    public void setPublisher(String newValue){  this.publisher = newValue;}

    public void setLanguages(List<String> newValue){ this.languages = newValue;}

    public void setAchievement(int newValue){  this.achievements = newValue;}

    public void setGenres(List<String> newValue){   this.genres = newValue;}

    public void setRating(String newValue){ this.rating = newValue;}

    public void setSinglePlayer(boolean newValue) { this.single_player = newValue;}

    public void setMultiPlayer(boolean newValue) {  this.multi_player = newValue;}

    public void setCoop(boolean newValue) { this.coop = newValue;}

    public void setControllerSupport(boolean newValue) {    this.controller_support = newValue;}

    public void setCloudSaves(boolean newValue) {   this.cloud_saves = newValue;}

    public void setAchievement(boolean newValue) {  this.achievement = newValue;}

    //GOG GET
    public void  player_rating(Double newValue){   
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.player_rating = newValue;
    }

    public void  oses(List<String> newValue){   
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.oses = newValue;
    }

    public void  size(String newValue){ 
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.size = newValue;
    }

    public void  in_development(boolean newValue){  
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.in_development = newValue;
    }


    //STEAM GET
    public void  game_description(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.game_description = newValue;
    }

    public void  minimum_requirements(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.minimum_requirements = newValue;
    }

    public void  recommended_requirements(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.recommended_requirements = newValue;
    }







    //GET functions

    public int getId(){ return  this.id;}

    public String getStore(){   return  this.store;}

    public String getUrl(){ return  this.url;}

    public String getName(){    return  this.name;}

    public Date getReleaseDate(){   return  this.release_date;}

    public String getDeveloper(){   return  this.developer;}

    public String getPublisher(){   return  this.publisher;}

    public List<String> getLanguages(){ return  this.languages;}

    public int getAchievements(){  return  this.achievements;}

    public List<String> getGenres(){    return  this.genres;}

    public String getRating(){  return  this.rating;}

    public boolean getSinglePlayer(){   return  this.single_player;}

    public boolean getMultiPlayer(){    return  this.multi_player;}

    public boolean getCoop(){   return  this.coop;}

    public boolean getControllerSupport(){  return  this.controller_support;}

    public boolean getCloudSaves(){ return  this.cloud_saves;}

    public boolean getAchievement(){    return  this.achievement;}

    public JSONObject getGameDetails(){
        JSONObject details = new JSONObject();
        details.put("single_player", this.single_player);
        details.put("multi_player", this.multi_player);
        details.put("coop", this.coop);
        details.put("controller_support", this.controller_support);
        details.put("cloud_saves", this.cloud_saves);
        details.put("achievement", this.achievement);
        return details;
    }
    
    //GOG specific GET

    public Double getPlayerRating(){   
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        return  this.player_rating;
    }

    public List<String> getOses(){  
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        return  this.oses;
    }

    public String getSize(){    
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        return  this.size;
    }

    public boolean getInDevelopment(){  
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        return  this.in_development;
    }


    //Steam specific GET

    public String getGameDescription(){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        }    
        return  this.game_description;
    }

    public String getMinimumRequirements(){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        }    
        return  this.minimum_requirements;
    }

    public String getRecommendedRequirements(){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        }    
        return  this.recommended_requirements;
    }

    //for reviews
    public List<ReviewCompact> getReviews(){ return     this.reviews;}

    public void addReview(ReviewCompact rc){
        this.reviews.add(rc);
        this.tot_reviews++;
    }

    private Document toDocument(){
        Document doc = new Document();

        doc.append("_id", this.id);
        doc.append("url", this.url);
        doc.append("name", this.name);
        doc.append("genres", (this.genres != null)? new ArrayList<String>(this.genres) : new ArrayList<String>());
        doc.append("rating", this.rating);
        doc.append("release_date", this.release_date);
        doc.append("developer", this.developer);
        doc.append("publisher", this.publisher);
        doc.append("languages", (this.languages != null)? new ArrayList<String>(this.languages) : new ArrayList<String>());
        doc.append("achievements", this.achievements);
        doc.append("store", this.store);
        doc.append("tot_reviews", this.tot_reviews);
        //Details
        Document detailsDoc = new Document();
        detailsDoc.append("single_player", this.single_player);
        detailsDoc.append("multi_player", this.multi_player);
        detailsDoc.append("coop", this.coop);
        detailsDoc.append("controller_support", this.controller_support);
        detailsDoc.append("cloud_saves", this.cloud_saves);
        detailsDoc.append("achievement", this.achievement);
        doc.append("game_details", detailsDoc);

        //System.out.println(this.store);
        //System.out.println(this.game_description);
        if(this.store.equals("GOG")){
            doc.append("player_rating", this.player_rating);
            doc.append("oses", this.oses);
            doc.append("size", this.size);
            doc.append("in_development", this.in_development);
        }
        if(this.store.equals("Steam")){
            //Steam
            doc.append("game_description", this.game_description);
            doc.append("minimum_requirements", this.minimum_requirements);
            doc.append("recommended_requirements", this.recommended_requirements);
        }


        List<Document> reviewsJsonArrey = new ArrayList<Document>();
        for(ReviewCompact review : this.reviews){
            Document reviewDoc = new Document();

            reviewDoc.append("creation_date", review.getCreationDate());
            if(this.store.equals("GOG")){
                reviewDoc.append("rating", review.getRating());
            }else{
                //Steam
                reviewDoc.append("helpful", review.gethelpful());
                reviewDoc.append("positive", review.getPositive());
            }

            reviewsJsonArrey.add(reviewDoc);
        }
        doc.append("reviews", reviewsJsonArrey);

        return doc;
    }


    //Save a Game info on the DB
    public void update(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        //find a game by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document newGameDoc = this.toDocument();
        Document ret = gamesColl.findOneAndReplace(bsonFilter, newGameDoc);
        if(ret == null){
            throw new RuntimeException("ERROR: you are trying to update a review that isn't in the MongoDB");
        }

        //TODO: insert info in the graphDB if needed

    }


    public void delete(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        //find a game by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = gamesColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("ERROR: Unable to delete the review due to an error: " + me);
        }
    }


    //Get  list of games
    public static List<Game> getGamesByNamePart(String searchWord ){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        //Building a regex query to find a game with the search word in the name
        BsonDocument regexFilter = Filters
                .regex("name", searchWord, "i")
                .toBsonDocument(null, null);

        FindIterable<Document> gamesDocs = gamesColl.find(regexFilter);

        List<Game> games = new ArrayList<>();

        if (gamesDocs == null) {
            return games;
        }

        for(Document doc : gamesDocs) {
            Game newGame = new Game(doc);
            System.out.println(newGame.getName());
            games.add(newGame);
        }


        return games;
    }

}
