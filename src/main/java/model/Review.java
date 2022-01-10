package model;

import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;

public class Review {

    // fields
    private String username;
    private String gamename;
    private LocalDate creationDate;
    private String content;
    private String store;

    //GOG
    private int rating;
    private String title;

    //Steam
    private int helpfull;
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
        int new_helpfull,
        boolean new_positive
    ){
        this.gamename = new_gamename; 
        this.username = new_username; 
        this.creationDate = new_creation_date; 
        this.store = new_store; 
        this.content = new_content; 

        //GOG
        this.rating = new_rating; 
        this.title = new_title; 

        //Steam
        this.helpfull = new_helpfull; 
        this.positive = new_positive; 
    }

    public Review(Document doc){
        this.gamename = doc.getString("name"); 
        this.username = doc.getString("username"); 
        this.creationDate = doc.getDate("creation_date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();; 
        this.content = doc.getString("content"); 
        this.store = (doc.getString("title") == null) ? "Steam" : "GOG" ; 

        if(this.store == "GOG"){
            //GOG
            this.rating = doc.getInteger("rating"); 
            this.title = doc.getString("title"); 
        }else{
            //Steam
            this.helpfull = doc.getInteger("helpfull"); 
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
    public void setTitle(String title) {this.title = title;}

    //GOG GET
    public void setGamename(String gamename) {this.gamename = gamename;}
    public void setHelpfull(int helpfull) {this.helpfull = helpfull;}

    //Steam GET
    public void setPositive(boolean positive) {this.positive = positive;}
    public void setRating(int rating) {this.rating = rating;}

    public String getContent() {
        return content;
    }
    public String getUsername() {
        return username;
    }
    public String getTitle() {return title;}
    public int getHelpfull() {return helpfull;}
    public boolean getPositive() {return positive;}
    public int getRating() {return rating;}
    public LocalDate getCreationDate() {return creationDate;}
    public String getGamename() {return gamename;}

    // metodi statici che comunicano con il backend


    @Override
    public String toString() {
        return "Review{" +
                "gamename='" + gamename + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
