package it.unipi.gamerlist.model;

import com.mongodb.BasicDBObject;
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
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import it.unipi.gamerlist.driver.MongoDriver;
import it.unipi.gamerlist.driver.Neo4jDriver;
import it.unipi.gamerlist.utils.Log;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.neo4j.driver.Values.parameters;

public class User {

    // fields
    private ObjectId id;
    private String username;
    private String firstName;
    private String lastName;
    private String gender;
    private String country;
    private String email;
    private String phone;
    private LocalDate birthday;
    private LocalDate registered;
    private List<GamerListElement> gamerList;
    private boolean admin;

    private String salt;
    private String sha256;

    private String DUMMY;

    public User(){
    }

    //    costruttore da username
   public User(String username){
        this.username=username;

        User foundUser = getUserByName(username);
        if (foundUser != null) {
            this.id = foundUser.getId();
            this.firstName = foundUser.getFirstName();
            this.lastName = foundUser.getLastName();
            this.gender = foundUser.getGender();
            this.country = foundUser.getCountry();
            this.email = foundUser.getEmail();
            this.phone = foundUser.getPhone();
            this.birthday = foundUser.getBirthday();
            this.registered = foundUser.getRegistered();
            this.admin = foundUser.getAdmin();
            this.gamerList = foundUser.getGamerList();

            this.sha256 = foundUser.getSha256();
            this.salt = foundUser.getSalt();
        }
        else {
            throw new RuntimeException("ERROR: User not found");
        }
    }

    //    costruttore da documento
    public User (Document doc) {
        this.id = doc.getObjectId("_id");
        this.username = doc.getString("username");
        this.firstName = doc.getString("first_name");
        this.lastName = doc.getString("last_name");
        this.gender = doc.getString("gender");
        this.country = doc.getString("state");
        this.email = doc.getString("email");
        this.phone = doc.getString("cell");
        this.birthday = convertToLocalDate(doc.getDate("dob"));
        this.registered = convertToLocalDate(doc.getDate("reg"));
        this.admin = doc.getBoolean("role");

        this.sha256 = doc.getString("sha256");
        this.salt = doc.getString("salt");

        ArrayList<Document> gameList = (ArrayList<Document>) doc.get("gamerlist");
        this.gamerList = new ArrayList<GamerListElement>();
        for(Document gameItem :gameList) {
            String gName = gameItem.getString("name");
            String gPublisher = gameItem.getString("publisher");
            String gDeveloper = gameItem.getString("developer");
            int gFriendsCount = -1; //User.howManyFollowingPlayGame(this.getUsername(), gName);
            this.gamerList.add(new GamerListElement(gName, gPublisher, gDeveloper, gFriendsCount));
        }
    }

    //    costruttore con tutti gli argomenti
    public User(
                String username,
                String firstName,
                String lastName,
                String gender,
                String country,
                String email,
                String phone,
                LocalDate birthday,
                LocalDate registered,
                List<GamerListElement> gamerList,
                boolean admin,
                String salt,
                String sha256
    ) {
        this.id = new ObjectId();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.country = country;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.registered = registered;
        this.gamerList = gamerList;
        this.admin = admin;
        this.sha256 = sha256;
        this.salt = salt;
    }

    // setter e getter
    public void setId(ObjectId id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setGender(String gender) {this.gender = gender;}
    public void setCountry(String country) {this.country = country;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setBirthday(LocalDate birthday) {this.birthday = birthday;}
    public void setRegistered(LocalDate registered) {this.registered = registered;}
    public void setAdmin(boolean admin) {this.admin = admin;}

    public void setDUMMY(String DUMMY) {this.DUMMY = DUMMY;}

    public ObjectId getId() {return id;}
    public String getUsername() {return username;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getGender() {return gender;}
    public String getCountry() {return country;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public LocalDate getBirthday() {return birthday;}
    public LocalDate getRegistered() {return registered;}
    public boolean getAdmin(){return admin;}
    public String getDUMMY() {return DUMMY;}

    public String getSalt() {return salt;}
    public String getSha256() {return sha256;}

    public List<GamerListElement> getGamerList() {
        List<GamerListElement> gleList = new ArrayList<>();
        for(GamerListElement gleItem : this.gamerList)
            gleList.add(gleItem);
        return gleList;
    }

    public void insert(String favoriteGenre){ //ADDED
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");
        Logger logger = Log.getLogger();

        //Convert to document and replace original document in MongoDB;
        InsertOneResult ret = usersColl.insertOne(this.toDocument());
        if(ret == null){
            logger.log(Level.INFO, "ERROR: it was't possible to insert the user " + username + " in MongoDB");
            throw new RuntimeException("ERROR: it was't possible to insert the user in MongoDB");
        }

        //insert info in the graphDB if needed
        int res = addUserToGraph(this.getUsername(), favoriteGenre);
        if(res==-1)
            logger.log(Level.INFO, "ERROR: it was't possible to insert the user " + username + " in Neo4j");
    }

    public void update(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");
        Logger logger = Log.getLogger();

        //find a user by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document newUsersDoc = this.toDocument();
        Document ret = usersColl.findOneAndReplace(bsonFilter, newUsersDoc);
        if(ret == null){
            logger.log(Level.INFO, "ERROR: you are trying to update user " +username+"that isn't in the MongoDB");
            throw new RuntimeException("ERROR: you are trying to update an user that isn't in the MongoDB");
        }

        //insert info in the graphDB if needed
    }

    public void delete(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");
        Logger logger = Log.getLogger();

        //find a user by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = usersColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            logger.log(Level.INFO, "ERROR: Unable to delete " + username + " from MongoDB");
            System.err.println("ERROR: Unable to delete the user due to an error: " + me);
        }

        //ADDED
        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run( "MATCH (m:User { username: $username}) DETACH DELETE m RETURN id(m)",
                        parameters( "username", this.username));
                if (r.hasNext()) {
                    return r.single().get(0).asInt();
                }
                return -1;
            });
        }
        if(res==-1)
            logger.log(Level.INFO, "ERROR: Unable to delete " + username + " from Neo4j");
    }

    public static List<User> getUsersByNamePart(String searchWord ){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");

        //Building a regex query to find a user with the search word in the username
        BsonDocument regexFilter = Filters
                .regex("username", searchWord, "i")
                .toBsonDocument(null, null);

        FindIterable<Document> usersDocs = usersColl.find(regexFilter);

        List<User> users = new ArrayList<>();

        if (usersDocs == null) {
            return users;
        }

        for(Document doc : usersDocs) {
            User newUser = new User(doc);
            users.add(newUser);
        }

        return users;
    }

    public static User getUserByName(String searchWord ){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");

        //Building a regex query to find a user with the search word in the username
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("username", searchWord);

        FindIterable<Document> usersDocs = usersColl.find(searchQuery);

        if(usersDocs.first() == null) {
            System.out.println("user does not exist in database.");
            return null;
        }
        User users = new User(usersDocs.first());
        return users;
    }

    public static List<GamerListElement> getGameListFromUsername (String username) {
        User user = getUserByName(username);
        if (user != null)
            return user.getGamerList();
        else return null;
    }

    public static boolean doesUserExist(String username) {
        User user = getUserByName(username);
        if (user == null) return false;
        return true;
    }

    public boolean searchInGameList (GamerListElement gle) {
        for (GamerListElement game : this.gamerList) {
            if(game == gle)
                return true;
        }
        return false;
    }

    public boolean searchInGameList (String name) { //ADDED
        for (GamerListElement game : this.gamerList) {
            if(game.getName().equals(name))
                return true;
        }
        return false;
    }

    public void insertInGamelist (GamerListElement gle) { //DONE
        Logger logger = Log.getLogger();
        if(!searchInGameList(gle)) {
            this.gamerList.add(gle);
            System.out.println("The game has been inserted.");
            //System.out.println(this.toDocument());
            this.update();
        }
        else {
            logger.log(Level.INFO, "ERROR insert game " + gle.getName()+" in gamerlist of" + username + " in MongoDB");
            System.out.println("The game was already in the gamer list.");
        }

        // update in neo4j
        int res = addInGameList(this.getUsername(), gle.getName());
        if(res==-1)
            logger.log(Level.INFO, "ERROR insert game " + gle.getName()+" in gamerlist of" + username + " in Neo4j");
    }

    public void removeFromGamerList(GamerListElement gle) {
        Logger logger = Log.getLogger();

        if(searchInGameList(gle)) {
            if(this.gamerList.remove(gle)) {
                System.out.println("Removal of game from user list successful.");
                this.update();
            }
            else {
                logger.log(Level.INFO, "ERROR fail to remove game " + gle.getName()+" in gamerlist of" + username + " in MongoDB");
                System.out.println("Removal of game failed.");
            }
        }
        else System.out.println("Game is not in user list.");

        //update in neo4j
        int res = removeFromList(this.getUsername(), gle.getName());
        if(res==-1)
            logger.log(Level.INFO, "ERROR fail to remove game " + gle.getName()+" in gamerlist of" + username + " in Neo4j");
    }

    public void removeFromGamerList(String name) { //ADDED
        Logger logger = Log.getLogger();
        for (GamerListElement game : this.gamerList) {
            if(game.getName().equals(name)) {
                this.gamerList.remove(game);
                this.update();
                break;
            }
        }

        //update in neo4j
        int res = removeFromList(this.getUsername(), name);
        if(res==-1)
            logger.log(Level.INFO, "ERROR fail to remove game " + name+" in gamerlist of" + username + " in Neo4j");
    }

    private Document toDocument(){
        Document doc = new Document();

        doc.append("_id", this.id);
        doc.append("username", this.username);
        doc.append("first_name", this.firstName);
        doc.append("last_name", this.lastName);
        doc.append("gender", this.gender);
        doc.append("state", this.country);
        doc.append("email", this.email);
        doc.append("cell", this.phone);
        doc.append("dob", this.birthday);
        doc.append("reg", this.registered);
        doc.append("role", this.admin);
        doc.append("salt", this.salt);
        doc.append("sha256", this.sha256);

        List<Document> GLEArray = new ArrayList<>();
        for(GamerListElement gle : this.gamerList){
            Document gleDoc = new Document();

            gleDoc.append("name", gle.getName());
            gleDoc.append("publisher", gle.getPublisher());
            gleDoc.append("developer", gle.getDeveloper());
            gleDoc.append("friends_count", gle.getFriendsCount());

            GLEArray.add(gleDoc);
        }
        doc.append("gamerlist", GLEArray);

        return doc;
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static List<String> getFollowingList(String username){
        List<String> followingList;
        try (Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            followingList = session.readTransaction((TransactionWork<List<String>>) tx -> { //ADDED
                List<String> list = new ArrayList<>();
                Result positiveRev = tx.run(
                        "MATCH (u:User {username: $username})-[:FOLLOWING]->(m:User)\n"+
                                "RETURN DISTINCT(m {.username}) AS users",
                        parameters( "username", username));
                while(positiveRev.hasNext()) {
                    Record record = positiveRev.next();
                    list.add(record.get("users").get("username").asString());
                }
                return list;
            });
        }
        return followingList;
    }

    public static List<String> getFollowedList(String username){

        List<String> followedList;
        try (Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            followedList = session.readTransaction((TransactionWork<List<String>>) tx -> { //ADDED
                List<String> list = new ArrayList<>();
                Result positiveRev = tx.run(
                        "MATCH (u:User {username: $username})<-[:FOLLOWING]-(m:User)\n"+
                                "RETURN DISTINCT(m {.username}) AS users",
                        parameters( "username", username));
                while(positiveRev.hasNext()) {
                    Record record = positiveRev.next();
                    list.add(record.get("users").get("username").asString());
                }
                return list;
            });
        }
        return followedList;
    }

    public boolean searchInFollowing(User userFollowing) {
        for(String search : getFollowingList(this.getUsername())) {
            if(search.equals(userFollowing.getUsername())) return true;
        }
        return false;
    }

    public boolean searchInFollowed(User userFollowed) {
        for(String search : getFollowedList(this.getUsername())) {
            System.out.println("username followed: " + search);
            if(search.equals(userFollowed.getUsername())) return true;
        }
        return false;
    }

    // METODI NEO4J

    //questa funzione aggiunge un utente al graph
    public static int addUserToGraph( final String user, final String favoriteGenre) //CHECKED
    {
        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run( "MERGE (p:User {username: $username, favorite_genre: $favoriteGenre}) RETURN id(p)",
                        parameters( "username", user, "favoriteGenre", favoriteGenre));
                if (r.hasNext()) {
                    return r.single().get(0).asInt();
                }
                return -1;
            });
        }
        return res;
    }

    //questa funzione permette ad un utente di seguirne un altro
    public static void addFollow(final String following, final String followed) { //ADDED

        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (a:User), (b:User)\n" +
                                "WHERE a.username = $A AND b.username = $B\n" +
                                "MERGE (a)-[r:FOLLOWING]->(b)",
                        parameters( "A", following, "B", followed));
                return null;
            });
        }
    }

    //questa funzione permette ad un utente di smettere di seguire un altro utente
    public static void unfollowUser(final String following, final String followed) { //ADDED
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH\n" +
                                "(a:User)-[r:FOLLOWING]->(b:User)\n" +
                                "WHERE a.username = $A AND b.username = $B\n" +
                                "DELETE r",
                        parameters( "A", following, "B", followed));
                return null;
            });
        }
    }

    //questa funzione, per un utente, stampa i seguiti dei seguiti con lo stesso genere piu giocato
    public static List<String> followedByFollowedSameGenre(final String username) { //TOCHANGE PER ORA ENTRAMBI I LATI

        List<String> suggestedUsers = new ArrayList<>();
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            suggestedUsers=session.readTransaction((TransactionWork<List<String>>) tx -> { //ADDED
                List<String> userlist = new ArrayList<>();
                Result sameGenre = tx.run(
                        "MATCH (a:User)-[:FOLLOWING*2]->(user)\n" +
                                "WHERE a.username = $A\n" +
                                "AND NOT user.username = a.username\n" +
                                "AND user.favorite_genre = a.favorite_genre\n" +
                                "AND NOT (a)-[:FOLLOWING]->(user)\n" +
                                "RETURN DISTINCT user.username as username\n" +
                                "LIMIT 5",
                        parameters( "A", username));
                if(!sameGenre.hasNext()){
                    sameGenre = tx.run(
                            "MATCH (a:User)-[:FOLLOWING*2]->(user)\n" +
                                    "WHERE a.username = $A\n" +
                                    "AND NOT user.username = a.username\n" +
                                    //"AND user.favorite_genre = a.favorite_genre\n" +
                                    "AND NOT (a)-[:FOLLOWING]->(user)\n" +
                                    "RETURN DISTINCT user.username as username\n" +
                                    "LIMIT 5",
                            parameters( "A", username));
                }
                while(sameGenre.hasNext()) {
                    Record record = sameGenre.next();
                    userlist.add(record.get("username").asString());
                }
                return userlist;
            });
        }
        return suggestedUsers;
    }

    //questa funzione inserisce un gioco nella lista dei giocati di un utente
    public static int addInGameList(final String user, final String game) {

        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run(
                        "MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:Game)\n" +
                                "WHERE a.username = $A AND b.name = $B\n" +
                                "MERGE (a)-[r:HAS_PLAYED]->(b)" +
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

    //questa funzione rimuove un gioco dalla lista dell'utente
    public static int removeFromList(final String user, final String game) {
        int res;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            res = session.writeTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run(
                        "MATCH (user {username: $A})-[r:HAS_PLAYED]->(game {name: $B})\n" +
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

    //questa funzione, per un utente e un gioco nella sua lista dice il numero di following che ci giocano
    public static int howManyFollowingPlayGame(final String user, final String game) { //CHECKED

        int friendCount;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            friendCount = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result positiveRev = tx.run(
                        "MATCH (a:User)-[:FOLLOWING]-(user)-[:HAS_PLAYED]-(game)\n" +
                                "WHERE a.username = $A " +
                                "AND game.name = $B\n" +
                                "AND (a)-[:HAS_PLAYED]-(game)\n" +
                                "RETURN game.name as name, count(*) as occurrences\n",
                        parameters( "A", user, "B", game));
                int count = 0;
                while(positiveRev.hasNext()) {
                    count++;
                    Record record = positiveRev.next();
                }
                return count;
            });
        }
        return friendCount;
    }

    //questa funzione setta il genere più giocato dall'utente
    // USED ONLY IN DEVELOPMENT PHASE
    public static void setMostPlayed(final String user) {
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                Result mostPlayed = tx.run(
                        "MATCH (a:User)-[r:HAS_PLAYED]->(b:Game)\n" +
                                "WHERE a.username = $A\n" +
                                "RETURN b.genre as genre, count(*) as occurences\n" +
                                "ORDER BY occurences DESC\n" +
                                "LIMIT 1",
                        parameters( "A", user));
                if (mostPlayed.hasNext())
                {
                    Record record = mostPlayed.next();
                    String genre = String.valueOf(record.get("genre"));

                    //System.out.println("utente: " + user + " genere: " + genre);

                    Result updateUser = tx.run( "MATCH (a:User {username: $A})\n" +
                                    "SET a.most_played_genre = $B\n" +
                                    "RETURN a",
                            parameters( "A", user, "B", genre));
                }
                return null;
            });
        }
    }

    //questa funzione, per un utente, stampa i giochi nella sua lista in ordine per numero di following che ci giocano
    //USED ONLY IN DEVELOPMENT PHASE
    public static void orderGamesByFollowing(final String user) {

        try (Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.readTransaction((TransactionWork<Void>) tx -> { //ADDED
                Result positiveRev = tx.run(
                        "MATCH (a:User)-[:FOLLOWING]-(user)-[:HAS_PLAYED]-(game)\n" +
                                "WHERE a.username = $A " +
                                "AND (a)-[:HAS_PLAYED]-(game)\n" +
                                "RETURN game.name as name, count(*) as occurrences\n" +
                                "ORDER BY occurrences DESC\n",
                        parameters( "A", user));
                while(positiveRev.hasNext()) {
                    Record record = positiveRev.next();
                    //System.out.println("Friends' game: ");
                    //System.out.println(record.get("name").asString());
                    //System.out.println(record.get("occurrences").asInt());
                }
                return null;
            });
        }
    }
}
