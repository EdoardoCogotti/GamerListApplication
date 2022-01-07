package com.ducange.backend.Model;

import java.util.Date;

public class ReviewCompact {

    //General parameters
    private String platform;
    //private String username;
    private Date creation_date;

    //GOG
    private int rating;

    //STEAM
    private int helpfull;
    private boolean positive;



    public ReviewCompact(
            String new_platform,
            //String new_username,
            Date new_creation_date,
            int new_rating,
            int new_helpfull,
            boolean new_positive
    ) {
        this.platform = new_platform;
        //this.username = new_username;
        this.creation_date = new_creation_date;
        this.rating = new_rating;
        this.helpfull = new_helpfull;
        this.positive = new_positive;
    }

    //SET
    public void setPlatform(String newValue) {
        this.platform = newValue;
    }

	/*
	public void setUsername(String newValue) {
		this.username = newValue;
	}
	*/

    public void setCreationDate(Date newValue) {
        this.creation_date = newValue;
    }

    //GOG SET
    public void setRating(int newValue) {
        this.rating = newValue;
    }

    //STEAM SET
    public void setHelpfull(int newValue) {
        this.helpfull = newValue;
    }

    public void setPositive(boolean newValue) {
        this.positive = newValue;
    }



    //GET
    public String getplatform() {
        return  this.platform;
    }

	/*
	public String getUsername() {
		return  this.username;
	}
	*/

    public Date getCreationDate() {
        return  this.creation_date;
    }

    //GOG GETs
    public int getRating() {
        return  this.rating;
    }

    //STEAM GETs
    public int getHelpfull() {
        return  this.helpfull;
    }
}
