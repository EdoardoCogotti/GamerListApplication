package it.unipi.gamerlist.model;

import java.util.Date;

//Class used to improve some analytics
public class ReviewCompact {

    //General parameters
    private String platform;
    private Date creation_date;
    private int helpfull;
    private String name;
    
    //GOG
    private int rating;
    
    //STEAM
    private boolean positive;

    public ReviewCompact(
            String new_platform,
            Date new_creation_date,
            String new_username,
            int new_rating,
            int new_helpfull,
            boolean new_positive
    ) {
        this.platform = new_platform;
        this.creation_date = new_creation_date;
        this.name = new_username;
        this.rating = new_rating;
        this.helpfull = new_helpfull;
        this.positive = new_positive;
    }

    //SET
    public void setPlatform(String newValue) {
        this.platform = newValue;
    }
	public void setName(String newValue) {
        this.name = newValue;
	}
    public void setCreationDate(Date newValue) {
        this.creation_date = newValue;
    }
    public void setHelpfull(int newValue) {
        this.helpfull = newValue;
    }

    //GOG SET
    public void setRating(int newValue) {
        this.rating = newValue;
    }

    //STEAM SET
    public void setPositive(boolean newValue) {
        this.positive = newValue;
    }

    //GET
    public String getPlatform() {
        return  this.platform;
    }
	public String getName() {
		return  this.name;
	}
    public Date getCreationDate() {
        return  this.creation_date;
    }
    public int getHelpfull() {
        return  this.helpfull;
    }

    //GOG GETs
    public int getRating() {
        return  this.rating;
    }

    //STEAM GETs
    public boolean getPositive() {
        return  this.positive;
    }
}
