package model;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utils.MongoDriver;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Projections.*;

public class Review {

    // fields
    private ObjectId id;
    private String username;
    private String gamename;
    private LocalDate creationDate;
    private String content;
    private String store;
    private int helpful;

    //GOG
    private int rating;
    private String title;

    //Steam
    private boolean positive;

    public Review(){

    }

    public Review(
        String new_gamename,
        String new_username,
        LocalDate new_creation_date,
        String new_store,
        String new_content,
        int new_helpful
    ){
        this.id = new ObjectId();
        this.gamename = new_gamename; 
        this.username = new_username; 
        this.creationDate = new_creation_date; 
        this.store = new_store; 
        this.content = new_content; 
        this.helpful = new_helpful;

        //GOG default
        this.rating = -1;
        this.title = ""; 

        //Steam default
        this.positive = false; 
    }

    public Review(
        String new_gamename,
        String new_username,
        LocalDate new_creation_date,
        String new_store,
        String new_content,
        int new_rating,
        String new_title,
        int new_helpful,
        boolean new_positive
    ){
        this.id = new ObjectId();
        this.gamename = new_gamename; 
        this.username = new_username; 
        this.creationDate = new_creation_date; 
        this.store = new_store; 
        this.content = new_content;
        this.helpful = new_helpful;

        //GOG
        this.rating = new_rating; 
        this.title = new_title; 

        //Steam
        this.positive = new_positive; 
    }

    public Review(Document doc){
        this.id = doc.getObjectId("_id");
        this.gamename = doc.getString("game_name");
        this.username = doc.getString("username"); 
        this.creationDate = doc.getDate("creation_date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); 
        this.content = doc.getString("content"); 
        this.store = doc.getString("store");
        this.helpful = doc.getInteger("helpful");

        if(this.store.equals("GOG")){
            //GOG
            this.rating = doc.getInteger("rating"); 
            this.title = doc.getString("title"); 
        }
        if(this.store.equals("Steam")){
            //Steam
            this.positive = doc.getBoolean("positive");
        }

    }


    // setter e getter

    public void setContent(String content) {
        this.content = content;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setCreationDate(LocalDate creationDate) {this.creationDate = creationDate;}
    public void setGamename(String gamename) {this.gamename = gamename;}
    public void setStore(String store) {this.store = store;}
    public void setHelpful(int helpful) {this.helpful = helpful;}

    //GOG GET
    public void setPositive(boolean positive) {
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam review field in a GOG review");
        }
        this.positive = positive;
    }

    //Steam GET
    public void setTitle(String title) {
        if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG review field in a Steam review");
        }
        this.title = title;
    }
    public void setRating(int rating) {
         if(this.store.equals("Steam")){
            throw new RuntimeException("ERROR: tried to access a GOG review field in a Steam review");
        }   
        this.rating = rating;
    }

    public String getContent() {return this.content;}
    public String getUsername() {return this.username;}
    public LocalDate getCreationDate() {return this.creationDate;}
    public String getGamename() {return this.gamename;}
    public String getStore() { return store; }
    public int getHelpful() {return this.helpful;}

    // Steam GETs
    public boolean getPositive() {return this.positive;}

    // Gog GETs
    public String getTitle() {return this.title;}
    public int getRating() {return this.rating;}

    @Override
    public String toString() {
        return "Review{" +
                "gamename='" + gamename + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void insert(Game game){
        //Add review to the "Reviews" collection
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewColl =  mgDriver.getCollection("reviews");

        //Convert to document and replace original document in MongoDB;
        Document newReviewDoc = this.toDocument();
        InsertOneResult ret = reviewColl.insertOne(newReviewDoc);
        if(ret == null){
            throw new RuntimeException("ERROR: There was an issue inserting the review in MongoDB");
        }

        //Add the compact version of the review to the relative game that is being reviewed
        game.addReview(new ReviewCompact(
                this.store,
                java.util.Date.from(this.creationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                this.username,
                this.rating,
                this.helpful,
                this.positive
        ));
        game.update();
    }

    public static Review get(String gamename, String username){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        System.out.println("Searching review by "+username+" of "+gamename);
        FindIterable<Document> reviewDocs = reviewsColl.find(
                Filters.and(
                        Filters.eq("game_name", gamename),
                        Filters.eq("username", username)
                )
        );
        Review ret = new Review(reviewDocs.first());

        if(reviewDocs.first() == null){
            System.out.println("The user hasn't reviewed this game");
        }
        return ret;
    }

    public void update(){
        //save the changes to the current review (if the review is new, insert it)
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewColl =  mgDriver.getCollection("reviews");

        //find a review by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document newReviewDoc = this.toDocument();
        Document ret = reviewColl.findOneAndReplace(bsonFilter, newReviewDoc);
        if(ret == null){
            throw new RuntimeException("ERROR: you are trying to update a review that isn't in the MongoDB");
        }

        //TODO: insert info in the graphDB if needed
    }

    public void delete(){
        //TO_DO FRA cancellare review anche da array in collezione Games
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewColl =  mgDriver.getCollection("reviews");
        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = reviewColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("ERROR: Unable to delete the review due to an error: " + me);
        }

        //Delete the review from the game to
        Game game = Game.getGamesByNamePart(this.gamename).get(0);
        game.deleteReview(this.username);
        game.update();
    }

    public Document toDocument(){
        Document doc = new Document();

        doc.append("_id", this.id);
        doc.append("game_name", this.gamename); 
        doc.append("username", this.username); 
        doc.append("creation_date", this.creationDate); 
        doc.append("content", this.content);
        doc.append("store", this.store);
        doc.append("helpful", this.helpful);


        if(this.store.equals("GOG")){
            //GOG
            doc.append("rating", this.rating); 
            doc.append("title", this.title); 
        }
        if(this.store.equals("Steam")){
            //Steam
            doc.append("positive", this.positive); 
        }

        return doc;
    }

    public static List<Review> getReviewsByGame(Game game){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");
        List<Review> reviews = new ArrayList<>();
        //FindIterable<Document> reviewDocs = reviewsColl.find(Filters.eq("game_name", game.getName()));

        try(MongoCursor<Document> reviewDocs =
                    reviewsColl.find(Filters.eq("game_name", game.getName())).iterator())
        {
            while(reviewDocs.hasNext()){
                reviews.add(new Review(reviewDocs.next()));
            }
        }

        /*
        if (reviewDocs == null) {
            return reviews;
        }

        for(Document doc : reviewDocs) {
            Review rev = new Review(doc);
            //System.out.println(rev.content);
            reviews.add(rev);
        }*/

        return reviews;
    }

    public static List<Review> getReviewsByUser(String username){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        FindIterable<Document> reviewDocs = reviewsColl.find(Filters.eq("username", username));

        List<Review> reviews = new ArrayList<>();

        if (reviewDocs == null) {
            return reviews;
        }

        for(Document doc : reviewDocs) {
            Review rev = new Review(doc);
            //System.out.println(rev.content);
            reviews.add(rev);
        }

        return reviews;
    }

    public static int getRankingPosition(String username)  {
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate=null;
        try {
            myDate = sdf.parse(String.valueOf(LocalDate.now().minusYears(1)));
        }
        catch(ParseException e){e.printStackTrace();}

        long earlier = System.currentTimeMillis() ;
        AggregateIterable<Document> result = reviewsColl.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.gte("creation_date", myDate)),
                        Aggregates.group("$username", new BsonField("helpful", new BsonDocument("$avg", new BsonString("$helpful")))),
                        Aggregates.sort(Sorts.ascending("helpful"))
                )
        );
        MongoCursor<Document> iterator = result.iterator();
        long later = System.currentTimeMillis();
        System.out.println("1: "+ ( later - earlier));

        //long early1 = System.currentTimeMillis();
        int pos = 0;
        int length = 0;

        while (iterator.hasNext()) {
        //for(Document doc: result){
            //earlier = System.nanoTime();

            Document doc = iterator.next();
            if(doc.getString("_id").equals(username)){
                pos = length;
            }
            length++;

        }

        //long later1 = System.currentTimeMillis();

        System.out.println(pos);
        System.out.println(length);

        return (pos*100)/length;
    }
}
