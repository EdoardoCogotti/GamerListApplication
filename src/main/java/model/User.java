package model;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import utils.MongoDriver;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class User {

    // fields
    private int id;
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

    private List<String> followed;
    private List<String> following;
    private String mostPlayedGenre;

    private String DUMMY;

    public User(){


    }

    //    costruttore da username
    public User(String username){
        this.username=username;
        this.admin = false;

        // TO_DO
        User foundUser = getUserByName(username);
        if (foundUser != null && !foundUser.admin) {
            this.id = foundUser.getId();
            this.firstName = foundUser.getFirstName();
            this.lastName = foundUser.getLastName();
            this.gender = foundUser.getGender();
            this.country = foundUser.getCountry();
            this.email = foundUser.getEmail();
            this.phone = foundUser.getPhone();
            this.birthday = foundUser.getBirthday();
            this.registered = foundUser.getRegistered();
            this.gamerList = foundUser.getGamerList();
            this.following = foundUser.getFollowing();
            this.followed = foundUser.getFollowed();
            this.mostPlayedGenre = foundUser.getMostPlayedGenre();
        }
        else {
            //          ???
        }
    }

    //    costruttore da documento
    public User (Document doc) {
        this.id = doc.getInteger("id");
        this.username = doc.getString("username");
        this.firstName = doc.getString("first_name");
        this.lastName = doc.getString("last_name");
        this.gender = doc.getString("gender");
        this.country = doc.getString("country");
        this.email = doc.getString("email");
        this.phone = doc.getString("phone");
        this.birthday = convertToLocalDate(doc.getDate("birthday"));
        this.registered = convertToLocalDate(doc.getDate("registered"));
        this.admin = doc.getBoolean("admin");
        this.mostPlayedGenre = doc.getString("most_played_genre");
        this.followed = doc.getList("followed", String.class);
        this.following = doc.getList("following", String.class);

        ArrayList<Document> gameList = (ArrayList<Document>) doc.get("gamer_list");
        this.gamerList = new ArrayList<GamerListElement>();
        for(Document gameItem :gameList){
            String gName = gameItem.getString("name");
            String gPublisher = gameItem.getString("publisher");
            String gDeveloper = gameItem.getString("developer");
            int gFriendsCount = gameItem.getInteger("friends_count");
            this.gamerList.add(new GamerListElement(gName, gPublisher, gDeveloper, gFriendsCount));
        }

    }

    //    costruttore con tutti gli argomenti
    public User(int id,
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
                List<String> followed,
                List<String> following,
                String DUMMY
    ) {
        this.id = id;
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
        this.followed = followed;
        this.following = following;
        this.DUMMY = DUMMY;
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
            System.out.println(newUser.getUsername());
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

    public static List<GamerListElement> getGameListFromUser (User user) {
        return user.getGamerList();
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

    public void insertInGamelist (GamerListElement gle) {
        if(!searchInGameList(gle)) {
            this.gamerList.add(gle);
            System.out.println("The game has been inserted.");
            this.update();
        }
        else {
            System.out.println("The game was already in the gamer list.");
        }
    }

    public void removeFromGamerList(GamerListElement gle) {
        if(searchInGameList(gle)) {
            if(this.gamerList.remove(gle)) {
                System.out.println("Removal of game from user list successful.");
                this.update();
            }
            else
                System.out.println("Removal of game failed.");
        }
        else System.out.println("Game is not in user list.");
    }

    private Document toDocument(){
        Document doc = new Document();

        doc.append("_id", this.id);
        doc.append("username", this.username);
        doc.append("first_name", this.firstName);
        doc.append("last_name", this.lastName);
        doc.append("gender", this.gender);
        doc.append("country", this.country);
        doc.append("email", this.email);
        doc.append("phone", this.phone);
        doc.append("birthday", this.birthday);
        doc.append("registered", this.registered);
        doc.append("admin", this.admin);
        doc.append("following", (this.following != null)? new ArrayList<String>(this.following) : new ArrayList<String>());
        doc.append("followed", (this.followed != null)? new ArrayList<String>(this.followed) : new ArrayList<String>());

        List<Document> GLEArray = new ArrayList<Document>();
        for(GamerListElement gle : this.gamerList){
            Document gleDoc = new Document();

            gleDoc.append("name", gle.getName());
            gleDoc.append("publisher", gle.getPublisher());
            gleDoc.append("developer", gle.getDeveloper());
            gleDoc.append("friends_count", gle.getFriendsCount());

            GLEArray.add(gleDoc);
        }
        doc.append("gamer_list", GLEArray);

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
    }

    public boolean searchInFollowing(User userFollowing) {
        for(String search : this.following) {
            if(search == userFollowing.getUsername()) return true;
        }
        return false;
    }

    public boolean searchInFollowed(User userFollowed) {
        for(String search : this.followed) {
            if(search == userFollowed.getUsername()) return true;
        }
        return false;
    }

    public void followUser(User toFollow) {
        if(!this.searchInFollowing(toFollow)) {
            this.followed.add(toFollow.getUsername());
            toFollow.getFollowing().add(this.getUsername());
            this.update();
            toFollow.update();
        }
        else System.out.println("You are already following this user.");
    }

    public void unfollowUser(User toUnfollow) {
        if(this.searchInFollowing(toUnfollow)) {
            this.followed.remove(toUnfollow.getUsername());
            toUnfollow.getFollowing().remove(this.getUsername());
            this.update();
            toUnfollow.update();
        }
        else System.out.println("You are not following this user.");
    }

    public int getFollowedCount() {
        return this.followed.size();
    }

    public int getFollowingCount() {
        return this.following.size();
    }


    // setter e getter

    public void setId(int id) {this.id = id;}
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
    public void setFollowing(List<String> following) {this.following = following;}
    public void setFollowed(List<String> followed) {this.followed = followed;}

    public void setDUMMY(String DUMMY) {this.DUMMY = DUMMY;}

    public int getId() {return id;}
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

    public List<GamerListElement> getGamerList() {
        List<GamerListElement> gleList = new ArrayList<>();
        for(GamerListElement gleItem : this.gamerList)
            gleList.add(gleItem);
        return gleList;
    }
    public List<String> getFollowing() {return following;}
    public List<String> getFollowed() {return followed;}
    public String getMostPlayedGenre() {return mostPlayedGenre;}

    // metodi statici che comunicano con il backend


}
