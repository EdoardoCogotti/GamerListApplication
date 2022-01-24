package model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import utils.MongoDriver;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

public class Game {

    //General parameters
    private ObjectId id;
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
        this.id = new ObjectId();
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
        this.reviews = new ArrayList<ReviewCompact>();

        //TO_CHECK
    }

    public Game(Document doc){
        //We always copy in the object the fileds that are present both in GOG and Steam
        this.name = doc.getString("name");
        //System.out.println(this.name);

        this.id = doc.getObjectId("_id");
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
            helpful =  review.getInteger("helpful");
            username= review.getString("name");
            if(store.equals("GOG")){
                rating = review.getInteger("rating");
            }else{
                positive = review.getBoolean("positive");
            }
            this.reviews.add(new ReviewCompact(store, creation_date, username, rating, helpful, positive));
        }
        //System.out.println(this.reviews.get(1).positive);
    }

    // setter e getter
    //SET functions
    public void setId(ObjectId id){  this.id = id;}

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

    public void setTotalReviews(int newValue){ this.tot_reviews = newValue;}

    public void setSinglePlayer(boolean newValue) { this.single_player = newValue;}

    public void setMultiPlayer(boolean newValue) {  this.multi_player = newValue;}

    public void setCoop(boolean newValue) { this.coop = newValue;}

    public void setControllerSupport(boolean newValue) {    this.controller_support = newValue;}

    public void setCloudSaves(boolean newValue) {   this.cloud_saves = newValue;}

    public void setAchievement(boolean newValue) {  this.achievement = newValue;}

    //GOG SET
    public void  setPlayerRating(Double newValue){   
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.player_rating = newValue;
    }

    public void  setOses(List<String> newValue){   
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.oses = newValue;
    }

    public void  setSize(String newValue){ 
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.size = newValue;
    }

    public void  setInDevelopment(boolean newValue){  
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG game field in a Steam game");
        } 
        this.in_development = newValue;
    }


    //STEAM SET
    public void  setGameDescription(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.game_description = newValue;
    }

    public void  setMinimumRequirements(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.minimum_requirements = newValue;
    }

    public void  setRecommendedRequirements(String newValue){ 
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam game field in a GOG game");
        } 
        this.recommended_requirements = newValue;
    }

    //GET functions

    public ObjectId getId(){ return  this.id;}

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

    public int getTotReviews(){  return  this.tot_reviews;}

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

    public String getGameDetailsString(){
        String details = "";
        details+= "single_player: " + String.valueOf(this.single_player) + "\n";
        details+= "multi_player: " + String.valueOf(this.multi_player) + "\n";
        details+= "coop: " + String.valueOf(this.coop) + "\n";
        details+= "controller_support: " + String.valueOf(this.controller_support) + "\n";
        details+= "cloud_saves: " + String.valueOf(this.cloud_saves) + "\n";
        details+= "achievement: " + String.valueOf(this.achievement) + "\n";
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
    public List<ReviewCompact> getReviews(){ return this.reviews;}

    protected void addReview(ReviewCompact rc){
        this.reviews.add(rc);
        this.tot_reviews++;
    }

    protected void deleteReview(String username){
        ReviewCompact rcToRemove = null;
        for(ReviewCompact rc : this.reviews){
            System.out.println(rc.getName());
            if(rc.getName().equals(username)){
                rcToRemove = rc;
            }
        }
        if(rcToRemove == null){
            throw new RuntimeException(username+" never reviewed the game "+this.name);
        }
        this.reviews.remove(rcToRemove);
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
            reviewDoc.append("helpful", review.getHelpfull());
            reviewDoc.append("name", review.getName());

            if(this.store.equals("GOG")){
                reviewDoc.append("rating", review.getRating());
            }else{
                //Steam
                reviewDoc.append("positive", review.getPositive());
            }

            reviewsJsonArrey.add(reviewDoc);
        }
        doc.append("reviews", reviewsJsonArrey);

        return doc;
    }

    public void insert(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        //Convert to document and replace original document in MongoDB;
        InsertOneResult ret = gamesColl.insertOne(this.toDocument());
        if(ret == null){
            throw new RuntimeException("ERROR: it was't possible to insert the game in MongoDB");
        }

        //TODO: insert info in the graphDB if needed

    }

    //Save a Game info on the DB
    public void update(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        //find a game by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document ret = gamesColl.findOneAndReplace(bsonFilter, this.toDocument());
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

    /*
    Top k games with positive reviews for each genre in the last year
    (per steam positive/negative e per gog rating>=3)
    */
    public static ArrayList<Document> getTopKByGenre(int k) throws ParseException {
        ArrayList<Document> listGames = new ArrayList<Document>();
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        // to reduce fields in the unwind phase
        Bson projection = project(fields(
                include("name", "reviews.rating", "reviews.positive", "reviews.creation_date", "genres"),
                excludeId()));

        // review unwind
        Bson reviewUnwind = unwind("$reviews");

        //review of the last year
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = sdf.parse(String.valueOf(LocalDate.now().minusYears(5)));
        Bson myDateMatch = gte("reviews.creation_date", myDate);

        //rating >=3 oppure positive
        Bson myRatingMatch = or(
                and(exists("reviews.positive"),eq("reviews.positive", true)),
                and(exists("reviews.rating"),gte("reviews.rating", 3))
        );

        Bson myMatch = match(and(myDateMatch,myRatingMatch));

        // group by name and genre considering the number of positive reviews
        Bson groupByNameAndGenre = new Document("$group",
                new Document("_id", new Document("name", "$name")
                        .append("genres", "$genres"))
                        .append("totalPositiveReview", new Document("$sum", 1)));

        // consider each game for each of own genres
        Bson genreUnwind = unwind("$_id.genres");

        // group by genre and get an array of documents in form {gamename, totalPositiveReview)
        Bson groupByGenre = new Document("$group",
                new Document("_id", "$_id.genres")
                        .append("totalPositiveReviewArray", new Document("$push",
                                new Document("totalPositiveReview","$totalPositiveReview").append("name","$_id.name"))));

        // sort in descending order
        Bson sort = new Document("$sort",
                new Document("_id.genres",1).append("totalPositiveReview",-1));

        // consider only the first k elements of each array in each document
        List array = Arrays.asList("$totalPositiveReviewArray",k);
        Bson projectionFields = new Document("$project",
                new Document("totalPositiveReviewArray",
                    new Document("$slice", array)));

        // unwind w.r.t. totalPositiveReviewArray
        Bson topKUnwind = unwind("$totalPositiveReviewArray");

        try {
            MongoCursor<Document> cursor = gamesColl.aggregate(Arrays.asList(
                    projection,
                    reviewUnwind,
                    myMatch, //date and rating
                    groupByNameAndGenre,
                    genreUnwind,
                    sort,
                    groupByGenre,
                    projectionFields
                    //,topKUnwind*/
            )).iterator();

            try {
                while (cursor.hasNext()) {

                    Document document = cursor.next();
                    System.out.println(document.toJson());
                    listGames.add(document);
                }
            } finally {
                cursor.close();
            }

            return listGames;
        }
        catch(Exception e){e.printStackTrace();}
        return null;

    }

    public static ArrayList<Document> getTopKByGenre(int k, String genre) throws ParseException {
        ArrayList<Document> listGames = new ArrayList<Document>();
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("games");

        Bson matchGenre = match(in("genres", genre));

        // to reduce fields in the unwind phase
        Bson projection = project(fields(
                include("name", "reviews.rating", "reviews.positive", "reviews.creation_date", "genres"),
                excludeId()));

        // review unwind
        Bson reviewUnwind = unwind("$reviews");

        //review of the last year
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = sdf.parse(String.valueOf(LocalDate.now().minusYears(5)));
        Bson myDateMatch = gte("reviews.creation_date", myDate);

        //rating >=3 oppure positive
        Bson myRatingMatch = or(
                and(exists("reviews.positive"),eq("reviews.positive", true)),
                and(exists("reviews.rating"),gte("reviews.rating", 3))
        );

        Bson myMatch = match(and(myDateMatch,myRatingMatch));

        // group by name and genre considering the number of positive reviews
        Bson groupByName = new Document("$group",
                new Document("_id", new Document("name", "$name"))
                        .append("totalPositiveReview", new Document("$sum", 1)));

        // consider each game for each of own genres
        Bson genreUnwind = unwind("$_id.genres");

        // group by genre and get an array of documents in form {gamename, totalPositiveReview)
        Bson groupByGenre = new Document("$group",
                new Document("_id", "$_id.genres")
                        .append("totalPositiveReviewArray", new Document("$push",
                                new Document("totalPositiveReview","$totalPositiveReview").append("name","$_id.name"))));

        // sort in descending order
        Bson sort = sort(descending("totalPositiveReview"));

        Bson limit = limit(k);

        try {
            MongoCursor<Document> cursor = gamesColl.aggregate(Arrays.asList(
                    matchGenre,
                    projection,
                    reviewUnwind,
                    myMatch, //date and rating
                    groupByName,
                    sort,
                    limit
            )).iterator();

            //System.out.println("inizio");
            try {
                while (cursor.hasNext()) {

                    Document document = cursor.next();
                    System.out.println(document.toJson());
                    listGames.add(document);
                }
            } finally {
                cursor.close();
            }
            //System.out.println("fine");

            return listGames;
        }
        catch(Exception e){e.printStackTrace();}
        return null;

    }

    public static ArrayList<Document> getAll(){
        ArrayList<Document> listGames = new ArrayList<Document>();
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> gamesColl =  mgDriver.getCollection("users");

        String field = "state"; //languages
        // to reduce fields in the unwind phase
        Bson projection = project(fields(
                include(field)
        ));

        // review unwind
        Bson reviewUnwind = unwind("$"+field);

        // group by name and genre considering the number of positive reviews
        Bson groupByField = group("$"+field, sum("tot", "$_id"));

        Bson push = new Document("$group",
                        new Document("_id", "$tot")
                                .append(field+"Array", new Document("$push", "$_id")));
        //new Document("languages","$_id"))));

        Bson sort = sort(ascending("_id"));

        Bson gogMatch = match(eq("store", "GOG"));
        Bson steamMatch = match(eq("store", "Steam"));

        try {
            MongoCursor<Document> cursor = gamesColl.aggregate(Arrays.asList(
                    //steamMatch,
                    //projection, gg
                    //reviewUnwind, gg
                    groupByField,
                    sort,
                    push
            )).iterator();

            try {
                while (cursor.hasNext()) {

                    Document document = cursor.next();
                    System.out.println(document.toJson());
                    listGames.add(document);
                }
            } finally {
                cursor.close();
            }

            return listGames;
        }
        catch(Exception e){e.printStackTrace();}
        return null;
    }
}
