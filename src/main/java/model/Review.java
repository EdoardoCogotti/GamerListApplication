package model;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import utils.MongoDriver;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Review {

    // fields
    private ObjectId id;
    private String username;
    private String gamename;
    private LocalDate creationDate;
    private String content;
    private String store;

    //GOG
    private int rating;
    private String title;

    //Steam
    private int helpful;
    private boolean positive;

    public Review(){

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

        //TO_CHECK
        //GOG
        this.rating = new_rating; 
        this.title = new_title; 

        //Steam
        this.helpful = new_helpful;
        this.positive = new_positive; 
    }

    public Review(Document doc){
        this.id = doc.getObjectId("_id");
        this.gamename = doc.getString("name"); 
        this.username = doc.getString("username"); 
        this.creationDate = doc.getDate("creation_date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();; 
        this.content = doc.getString("content"); 
        this.store = (doc.getString("title") == null) ? "Steam" : "GOG" ; //TO_CHECK

        if(this.store.equals("GOG")){
            //GOG
            this.rating = doc.getInteger("rating"); 
            this.title = doc.getString("title"); 
        }
        if(this.store.equals("Steam")){
            //Steam
            this.helpful = doc.getInteger("helpful");
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

    //GOG GET
    public void setPositive(boolean positive) {
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam review field in a GOG review");
        }
        this.positive = positive;
    }
    public void setHelpful(int helpful) {
        if(this.store.equals("GOG")){
            throw new RuntimeException("ERROR: tried to access a Steam review field in a GOG review");
        } 
        this.helpful = helpful;
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

    // Steam GETs
    public int getHelpful() {return this.helpful;}
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

        //TO_CHECK
        //find a review by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

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
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewColl =  mgDriver.getCollection("reviews");
        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = reviewColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("ERROR: Unable to delete the review due to an error: " + me);
        }
    }

    public Document toDocument(){
        Document doc = new Document();

        doc.append("_id", this.id);
        doc.append("game_name", this.gamename); 
        doc.append("username", this.username); 
        doc.append("creation_date", this.creationDate); 
        doc.append("content", this.content); 

        
        if(this.store.equals("GOG")){
            //GOG
            doc.append("rating", this.rating); 
            doc.append("title", this.title); 
        }
        if(this.store.equals("Steam")){
            //Steam
            doc.append("helpful", this.helpful);
            doc.append("positive", this.positive); 
        }

        return doc;
    }

    public static List<Review> getReviewsByGame(Game game){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        FindIterable<Document> reviewDocs = reviewsColl.find(Filters.eq("game_name", game.getName()));

        List<Review> reviews = new ArrayList<>();

        if (reviewDocs == null) {
            return reviews;
        }

        for(Document doc : reviewDocs) {
            Review rev = new Review(doc);
            System.out.println(rev.content);
            reviews.add(rev);
        }

        return reviews;
    }
}
