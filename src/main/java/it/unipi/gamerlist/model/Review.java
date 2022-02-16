package it.unipi.gamerlist.model;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import it.unipi.gamerlist.driver.MongoDriver;
import it.unipi.gamerlist.driver.Neo4jDriver;
import it.unipi.gamerlist.utils.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.*;
import static org.neo4j.driver.Values.parameters;

public class Review {

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

        //GOG
        if(this.store.equals("GOG")){
            this.rating = doc.getInteger("rating"); 
            this.title = doc.getString("title"); 
        }
        //Steam
        if(this.store.equals("Steam")){
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
        Logger logger = Log.getLogger();

        //Convert to document and replace original document in MongoDB;
        Document newReviewDoc = this.toDocument();
        InsertOneResult ret = reviewColl.insertOne(newReviewDoc);
        if(ret == null){
            logger.log(Level.INFO, "ERROR insert review" + id + " in MongoDB");
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

        boolean positive;
        if(!this.store.equals("GOG"))
            positive=this.positive;
        else
            positive=(this.rating>=3);
        int res = addReview(this.username, this.gamename, positive);
        if(res==-1)
            logger.log(Level.INFO, "ERROR insert review" + id + " in Neo4j");
    }

    public static Review get(String gamename, String username){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        //System.out.println("Searching review by "+username+" of "+gamename);
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
        Logger logger = Log.getLogger();

        //find a review by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document newReviewDoc = this.toDocument();
        Document ret = reviewColl.findOneAndReplace(bsonFilter, newReviewDoc);
        if(ret == null){
            logger.log(Level.INFO, "ERROR update review" + id + " in MongoDB");
            throw new RuntimeException("ERROR: you are trying to update a review that isn't in the MongoDB");
        }

        // insert info in the graphDB if needed
        boolean positive;
        if(!this.store.equals("GOG"))
            positive=this.positive;
        else
            positive=(this.rating>=3);
        int res = updateReview(this.username, this.gamename, positive);
        if(res==-1)
            logger.log(Level.INFO, "ERROR insert review" + id + " in Neo4j");
    }

    public void delete(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewColl =  mgDriver.getCollection("reviews");
        Logger logger = Log.getLogger();

        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = reviewColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            logger.log(Level.INFO, "ERROR delete review" + id + " in MongoDB");
            System.err.println("ERROR: Unable to delete the review due to an error: " + me);
        }

        //Delete the review from the game to
        Game game = Game.getGamesByNamePart(this.gamename).get(0);
        game.deleteReview(this.username);
        game.update();

        int res = removeReview(this.username, this.gamename);
        if(res==-1)
            logger.log(Level.INFO, "ERROR insert review" + id + " in Neo4j");
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
        //if(this.store.equals("Steam")){
        else{
            //Steam
            doc.append("positive", this.positive); 
        }

        return doc;
    }

    public static List<Review> getReviewsByGame(Game game){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");
        List<Review> reviews = new ArrayList<>();

        try(MongoCursor<Document> reviewDocs =
                    reviewsColl.find(Filters.eq("game_name", game.getName())).iterator())
        {
            while(reviewDocs.hasNext()){
                reviews.add(new Review(reviewDocs.next()));
            }
        }

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
            reviews.add(rev);
        }

        return reviews;
    }

    public static int getRankingPosition(String username, String type) {

        String aggregator = "$"+type;

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
                        Aggregates.group("$username", new BsonField("helpful", new BsonDocument(aggregator, new BsonString("$helpful")))),
                        Aggregates.sort(Sorts.ascending("helpful"))
                )
        );
        MongoCursor<Document> iterator = result.iterator();
        long later = System.currentTimeMillis();
        //System.out.println("1: "+ ( later - earlier));

        int pos = 0;
        int length = 0;

        while (iterator.hasNext()) {

            Document doc = iterator.next();
            if(doc.getString("_id").equals(username)){
                pos = length;
            }
            length++;

        }

        //System.out.println(pos);
        //System.out.println(length);

        return (pos*100)/length;
    }

    // percentage of negative reviews that have a low score (<=10 helpful)
    public static int getPercentageUnvalidNegativeReviews(String username) {

        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> reviewsColl =  mgDriver.getCollection("reviews");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate=null;
        try {
            myDate = sdf.parse(String.valueOf(LocalDate.now().minusYears(5)));
        }
        catch(ParseException e){e.printStackTrace();}

        Bson dateMatch = gte("creation_date", myDate);

        Bson usernameMatch = eq("username", username);

        Bson helpfulMatch = lte("helpful", 10);

        Bson negativeRatingMatch = or(
                and(exists("positive"),eq("positive", false)),
                and(exists("rating"),lt("rating", 3))
        );

        Bson helpfulUnwind = unwind("$helpful");

        Bson groupByHelpful = new Document("$group",
                new Document("_id", new Document("username", "$username")
                        .append("totReviews", "$totReviews"))
                        .append("negReviews", new Document("$sum", 1)));

        AggregateIterable<Document> result = reviewsColl.aggregate(
                Arrays.asList(
                        Aggregates.match(and(dateMatch, usernameMatch,negativeRatingMatch)),
                        Aggregates.group("$username",
                                new BsonField("totReviews", new BsonDocument("$sum", new BsonInt32(1))),
                                new BsonField("helpful", new BsonDocument("$push", new BsonString("$helpful")))
                                ),
                        helpfulUnwind,
                        Aggregates.match(helpfulMatch),
                        groupByHelpful
                )
        );

        MongoCursor<Document> iterator = result.iterator();

        //System.out.println(iterator.hasNext());
        while(iterator.hasNext()) {
            Document doc = iterator.next();
            //System.out.println(doc.toJson());
            int tot= ((Document)doc.get("_id")).getInteger("totReviews");
            int n = doc.getInteger("negReviews");
            return (int) Math.round(100.0*n/tot);
        }
        return -1;
    }

    // METODI NEO4J

    //questa funzione inserisce un gioco nella lista dei recensiti di un utente e definisce se recensione pos o neg
    public static int addReview(final String user, final String game, boolean review) {

        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run(
                        "MATCH (a:User), (b:Game)\n"+
                                "WHERE a.username = $A AND b.name = $B\n" +
                                "MERGE (a)-[r:HAS_REVIEWED {positive: $C}]->(b)\n" +
                                "RETURN id(r)",
                            parameters( "A", user, "B", game, "C", review));
                if (r.hasNext()) {
                    return r.single().get(0).asInt();
                }
                return -1;
            });
        }
        return res;
    }

    public static int updateReview(final String user, final String game, boolean review) {

        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run(
                        "MATCH (a:User)-[r:HAS_REVIEWED]-(b:Game)\n"+
                                "WHERE a.username = $A AND b.name = $B\n" +
                                "SET r.positive= $C\n" +
                                "RETURN id(r)",
                        parameters( "A", user, "B", game, "C", review));
                if (r.hasNext()) {
                    return r.single().get(0).asInt();
                }
                return -1;
            });
        }
        return res;
    }

    //questa funzione rimuove le recensioni fatte da un utente a un gioco
    public static int removeReview(final String user, final String game) {
        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r =tx.run(
                        "MATCH (user {username: $A})-[r:HAS_REVIEWED]->(game {name: $B})\n" +
                                "DELETE r\n"+
                                "RETURN id(r)",
                        parameters( "A", user, "B", game));
                if (r.hasNext()) {
                    return r.single().get(0).asInt();
                }
                return -1;
            });
        }
        return res;
    }

}
