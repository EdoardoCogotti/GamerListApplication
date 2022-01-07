package model;

import java.time.LocalDate;

public class Review {

    // fields
    private String username;
    private String gamename;
    private String content;
    private LocalDate creationDate;

    // steam
    private int helpful;
    private boolean positive;

    // gog
    private String title;
    private int rating;



    // setter e getter

    public void setContent(String content) {
        this.content = content;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setCreationDate(LocalDate creationDate) {this.creationDate = creationDate;}
    public void setTitle(String title) {this.title = title;}
    public void setGamename(String gamename) {this.gamename = gamename;}
    public void setHelpful(int helpful) {this.helpful = helpful;}
    public void setPositive(boolean positive) {this.positive = positive;}
    public void setRating(int rating) {this.rating = rating;}

    public String getContent() {
        return content;
    }
    public String getUsername() {
        return username;
    }
    public String getTitle() {return title;}
    public int getHelpful() {return helpful;}
    public boolean getPositive() {return positive;}
    public int getRating() {return rating;}
    public LocalDate getCreationDate() {return creationDate;}
    public String getGamename() {return gamename;}

    // metodi statici che comunicano con il backend

}
