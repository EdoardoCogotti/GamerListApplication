package model;

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
import driver.MongoDriver;
import driver.Neo4jDriver;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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

    /*private List<String> followed;
    private List<String> following;
    private String favoriteGenre;*/

    private String DUMMY;

    public User(){


    }

    //    costruttore da username
   public User(String username){
        this.username=username;

        // DONE
        User foundUser = getUserByName(username);
        if (foundUser != null) {    //&& !foundUser.admin l'utente può essere admin
            //this.id = foundUser.getId();
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

            /*this.following = foundUser.getFollowing();
            this.followed = foundUser.getFollowed();
            this.mostPlayedGenre = foundUser.getMostPlayedGenre();*/

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

        //this.mostPlayedGenre = "Action"; // DONE doc.getString("most_played_genre");
        /*this.followed = doc.getList("followed", String.class);
        this.following = doc.getList("following", String.class);*/

        /*ArrayList<Document> followingList = (ArrayList<Document>) doc.get("following");
        this.following = new ArrayList<>();
        for(Document followingItem :followingList) {
            String username = followingItem.getString("username");
            this.following.add(username);
        }

        ArrayList<Document> followedList = (ArrayList<Document>) doc.get("follower");
        this.followed = new ArrayList<>();
        for(Document followedItem :followedList) {
            String username = followedItem.getString("username");
            this.followed.add(username);
        }*/

        ArrayList<Document> gameList = (ArrayList<Document>) doc.get("gamerlist");
        this.gamerList = new ArrayList<GamerListElement>();
        for(Document gameItem :gameList) {
            String gName = gameItem.getString("name");
            String gPublisher = gameItem.getString("publisher");
            String gDeveloper = gameItem.getString("developer");
            // DONE
            int gFriendsCount = -1; //User.howManyFollowingPlayGame(this.getUsername(), gName);
            //System.out.println("count: " + gFriendsCount);
            this.gamerList.add(new GamerListElement(gName, gPublisher, gDeveloper, gFriendsCount));
        }
    }

    //    costruttore con tutti gli argomenti
    public User(//int id,   //default added
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
        //this.followed = followed;
        //this.following = following;
        this.sha256 = sha256;
        this.salt = salt;
        //this.favoriteGenre = favoriteGenre;
    }

    public void insert(String favoriteGenre){ //ADDED
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");

        //Convert to document and replace original document in MongoDB;
        InsertOneResult ret = usersColl.insertOne(this.toDocument());
        if(ret == null){
            throw new RuntimeException("ERROR: it was't possible to insert the game in MongoDB");
        }

        //TODO: insert info in the graphDB if needed (DONE)
        addUserToGraph(this.getUsername(), favoriteGenre);
    }

    //      update dell-user nel database
    public void update(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");

        //find a user by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);

        //Convert to document and replace original document in MongoDB;
        Document newUsersDoc = this.toDocument();
        Document ret = usersColl.findOneAndReplace(bsonFilter, newUsersDoc);
        if(ret == null){
            throw new RuntimeException("ERROR: you are trying to update an user that isn't in the MongoDB");
        }

        //TODO: insert info in the graphDB if needed

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
            //System.out.println(newUser.getUsername());
            users.add(newUser);
        }

        return users;
    }

    public static User getUserByName(String searchWord ){
        System.out.println(searchWord);
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
        //System.out.println(users.toDocument());
        return users;
    }

    //public static List<GamerListElement> getGameListFromUser (User user) {return user.getGamerList();}

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

    public void insertInGamelist (GamerListElement gle) { //TO_DO FRA PM
        // DONE update in neo4j
        if(!searchInGameList(gle)) {
            this.gamerList.add(gle);
            System.out.println("The game has been inserted.");
            this.update();
        }
        else {
            System.out.println("The game was already in the gamer list.");
        }
        //addInGameList(this.getUsername(), gle.getName());
    }

    public void removeFromGamerList(GamerListElement gle) { //TO_DO FRA PM
        // DONE update in neo4j
        if(searchInGameList(gle)) {
            if(this.gamerList.remove(gle)) {
                System.out.println("Removal of game from user list successful.");
                this.update();
            }
            else
                System.out.println("Removal of game failed.");
        }
        else System.out.println("Game is not in user list.");
        //removeFromList(this.getUsername(), gle.getName());
    }

    public void removeFromGamerList(String name) { //ADDED //TO_DO FRA PM
        // DONE update in neo4j
        for (GamerListElement game : this.gamerList) {
            if(game.getName().equals(name)) {
                this.gamerList.remove(game);
                this.update();
                break;
            }
        }
        //removeFromList(this.getUsername(), name);
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
        //doc.append("following", (this.following != null)? new ArrayList<String>(this.following) : new ArrayList<String>());
        //doc.append("followed", (this.followed != null)? new ArrayList<String>(this.followed) : new ArrayList<String>());

        doc.append("salt", this.salt);
        doc.append("sha256", this.sha256);

        /*List<Document> followingArray = new ArrayList<>();
        for(String s: this.following){
            Document followingDoc = new Document();

            User u = getUserByName(s);
            followingDoc.append("username",u.getUsername());
            followingDoc.append("name",u.getFirstName());
            followingDoc.append("gender",u.getGender());

            followingArray.add(followingDoc);
        }
        doc.append("following", followingArray);

        List<Document> followedArray = new ArrayList<>();
        for(String s: this.followed){
            Document followedDoc = new Document();

            User u = getUserByName(s);
            followedDoc.append("username",u.getUsername());
            followedDoc.append("name",u.getFirstName());
            followedDoc.append("gender",u.getGender());

            followedArray.add(followedDoc);
        }
        doc.append("follower", followedArray);*/

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


    public void delete(){
        MongoDriver mgDriver = MongoDriver.getInstance();
        MongoCollection<Document> usersColl =  mgDriver.getCollection("users");

        //find a user by its id in mongo
        Bson bsonFilter = Filters.eq("_id", this.id);
        try {
            DeleteResult result = usersColl.deleteOne(bsonFilter);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("ERROR: Unable to delete the user due to an error: " + me);
        }

        //ADDED
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (m:User { username: $username}) DETACH DELETE m",
                        parameters( "username", this.username));
                return null;
            });
        }

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
                    System.out.println("Friends' game: ");
                    System.out.println(record.get("users").get("username").asString());
                    //System.out.println(record.get("occurrences").asInt());
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
                //System.out.println("tipo: "+ positiveRev.next().get("users").get("username"));
                while(positiveRev.hasNext()) {
                    Record record = positiveRev.next();
                    //System.out.println("key:" + record.keys());
                    System.out.println("Friends' game: ");
                    System.out.println(record.get("users").get("username").asString());
                    //System.out.println(record.get("occurrences").asInt());
                    list.add(record.get("users").get("username").asString());
                }
                return list;
            });
        }
        return followedList;
    }

    public boolean searchInFollowing(User userFollowing) {
        //for(String search : this.following) {
        for(String search : getFollowingList(this.getUsername())) {
            if(search.equals(userFollowing.getUsername())) return true;
        }
        return false;
    }

    public boolean searchInFollowed(User userFollowed) {
        //for(String search : this.followed) {
        for(String search : getFollowedList(this.getUsername())) {
            System.out.println("username followed: " + search);
            if(search.equals(userFollowed.getUsername())) return true;
        }
        return false;
    }

    /*public void followUser(User toFollow) {
        if(!this.searchInFollowing(toFollow)) {
            this.following.add(toFollow.getUsername()); //toFollow.getUsername())
            toFollow.getFollowed().add(this.getUsername());
            this.update();
            toFollow.update();

            //addFollow(this.getUsername(), toFollow.getUsername()); //ADDED
        }
        else System.out.println("You are already following this user.");
    }

    public void unfollowUser(User toUnfollow) {
        if(this.searchInFollowing(toUnfollow)) {
            this.following.remove(toUnfollow.getUsername());
            toUnfollow.getFollowed().remove(this.getUsername());
            this.update();
            toUnfollow.update();

            //unfollowUser(this.getUsername(), toUnfollow.getUsername()); //ADDED
        }
        else System.out.println("You are not following this user.");
    }*/

    /*public int getFollowedCount() {
        return this.followed.size();
    }

    public int getFollowingCount() {
        return this.following.size();
    }*/

    // METODI NEO4J

    //questa funzione aggiunge un utente al graph
    public static void addUserToGraph( final String user, final String favoriteGenre) //CHECKED
    {
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:User {username: $username, favoriteGenre: $favoriteGenre})",
                        parameters( "username", user, "favoriteGenre", favoriteGenre));
                return null;
            });
        }
    }

    //questa funzione permette ad un utente di seguirne un altro
    public static void addFollow(final String following, final String followed) { //ADDED

        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        /*"MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:User)\n" +
                                "WHERE a.username = $A AND b.username = $B\n" +
                                "MERGE (a)-[r:FOLLOWING]->(b)\n" +
                                "RETURN type(r)",*/
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
                        "MATCH (a:User)-[:FOLLOWING*2]-(user)\n" +
                                "WHERE a.username = $A " +
                                "AND NOT user.username = a.username " +
                                "AND user.favorite_genre = a.favorite_genre " +
                                "AND NOT (a)-[:FOLLOWING]-(user)\n" +
                                "RETURN DISTINCT user.username as username\n" +
                                "LIMIT 5",
                        parameters( "A", username));
                while(sameGenre.hasNext()) {
                    Record record = sameGenre.next();
                    userlist.add(record.get("username").asString());
                    //System.out.println("friend of friend: ");
                    //System.out.println(record.get("username").asString());
                }
                return userlist;
            });
        }
        return suggestedUsers;
    }

    //questa funzione inserisce un gioco nella lista dei giocati di un utente
    public static void addInGameList(final String user, final String game) {

        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:Game)\n" +
                                "WHERE a.username = $A AND b.name = $B\n" +
                                "MERGE (a)-[r:HAS_PLAYED]->(b)\n" +
                                "RETURN type(r)",
                        parameters( "A", user, "B", game));
                return null;
            });
        }
    }

    //questa funzione rimuove un gioco dalla lista dell'utente
    public static void removeFromList(final String user, final String game) {
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (user {username: $A})-[r:HAS_PLAYED]->(game {name: $B})\n" +
                                "DELETE r",
                        parameters( "A", user, "B", game));
                return null;
            });
        }
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
                    System.out.println("Friends' game: ");
                    System.out.println(record.get("name").asString());
                    System.out.println(record.get("occurrences").asInt());
                }
                return count;
            });
        }
        return friendCount;
    }

    // TO_DO
    //questa funzione setta il genere più giocato dall'utente
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

                    System.out.println("utente: " + user + " genere: " + genre);

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
                    System.out.println("Friends' game: ");
                    System.out.println(record.get("name").asString());
                    System.out.println(record.get("occurrences").asInt());
                }
                return null;
            });
        }
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
    //public void setFollowing(List<String> following) {this.following = following;}
    //public void setFollowed(List<String> followed) {this.followed = followed;}
    //public void setFavoriteGenre(String favoriteGenre) {this.favoriteGenre = favoriteGenre;}

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
    //public List<String> getFollowing() {return following;}
    //public List<String> getFollowed() {return followed;}
    //public String getMostPlayedGenre() {return mostPlayedGenre;}
    //public String getFavoriteGenre() {return favoriteGenre;}

    // metodi statici che comunicano con il backend

}
