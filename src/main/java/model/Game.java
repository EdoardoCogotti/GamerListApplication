package model;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ducange.backend.Model.ReviewCompact;

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
    private List<String> achievements;
    private List<String> genres;
    private String rating;
    private int tot_reviews;
    //General parameters: details
    private JSONObject gameDetails;

    //GOG specific parameters
    private int player_rating;
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
            ArrayList<String> new_achievements,
            ArrayList<String> new_genres,
            String new_rating,
            int new_tot_reviews,
            JSONObject new_gameDetails,
            int new_player_rating,
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
        this.gameDetails = new_gameDetails;
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
        System.out.println(this.id);
        this.store = doc.getString("store");
        this.url = doc.getString("url");
        this.name = doc.getString("name");
        this.release_date = doc.getDate("release_date");
        this.developer = doc.getString("developer");
        this.publisher = doc.getString("publisher");
        this.languages = new ArrayList<String>(doc.getList("languages", String.class));
        this.tot_reviews = doc.getInteger("tot_reviews");
        this.genres = new ArrayList<String>(doc.getList("genres", String.class));
        this.rating = doc.getString("rating");
        this.tot_reviews = doc.getInteger("tot_reviews");
        this.gameDetails = (JSONObject)doc.get("game_details");

        //if we have a GOG game, we import some fields over others
        if(this.store == "GOG"){
            this.player_rating = doc.getInteger("player_rating");
            this.oses = new ArrayList<String>(doc.getList("oses", String.class));
            this.size = doc.getString("size");
            this.in_development = doc.getBoolean("in_development");
        }

        //if we have a Steam game, we import steam specific fields
        if(this.store == "Steam"){
            this.game_description = doc.getString("game_description");
            this.minimum_requirements = doc.getString("minimum_requirements");
            this.recommended_requirements = doc.getString("recommended_requirements");
        }

        //Load all the reviews
        ArrayList<Document> reviews = (ArrayList<Document>) doc.get("reviews");
        this.reviews = new ArrayList<ReviewCompact>();
        int rating = -1, helpfull = -1;
        boolean positive = false;
        for(Document review :reviews){
            String store = (review.getDate("rating") == null)?"Steam":"GOG";
            Date creation_date = review.getDate("creation_date");
            if(store == "GOG"){
                rating = review.getInteger("rating");
            }else{
                helpfull =  Integer.parseInt(review.getString("helpfull"));
                positive =  review.getString("rating") == "1";
            }
            this.reviews.add(new ReviewCompact(store, creation_date, rating, helpfull, positive));
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

    public void setAchievement(List<String> newValue){  this.achievements = newValue;}

    public void setGenres(List<String> newValue){   this.genres = newValue;}

    public void setRating(String newValue){ this.rating = newValue;}

    public void setGameDetails(JSONObject gameDetails){ this.gameDetails=gameDetails;}

    //GOG GET
    public void  player_rating(int newValue){   this.player_rating = newValue;}

    public void  oses(List<String> newValue){   this.oses = newValue;}

    public void  size(String newValue){ this.size = newValue;}

    public void  in_development(boolean newValue){  this.in_development = newValue;}


    //STEAM GET
    public void  game_description(String newValue){ this.game_description = newValue;}

    public void  minimum_requirements(String newValue){ this.minimum_requirements = newValue;}

    public void  recommended_requirements(String newValue){ this.recommended_requirements = newValue;}







    //GET functions

    public int getId(){ return  this.id;}

    public String getStore(){   return  this.store;}

    public String getUrl(){ return  this.url;}

    public String getName(){    return  this.name;}

    public Date getReleaseDate(){   return  this.release_date;}

    public String getDeveloper(){   return  this.developer;}

    public String getPublisher(){   return  this.publisher;}

    public List<String> getLanguages(){ return  this.languages;}

    public List<String> getAchievements(){  return  this.achievements;}

    public List<String> getGenres(){    return  this.genres;}

    public String getRating(){  return  this.rating;}

    public JSONObject getGameDetails(){ return gameDetails;}
    
    //GOG specific GET

    public int getPlayerRating(){   return  this.player_rating;}

    public List<String> getOses(){  return  this.oses;}

    public String getSize(){    return  this.size;}

    public boolean getInDevelopment(){  return  this.in_development;}


    //GOG specific GET

    public String getGameDescription(){ return  this.game_description;}

    public String getMinimumRequirements(){ return  this.minimum_requirements;}

    public String getRecommendedRequirements(){ return  this.recommended_requirements;}

}
