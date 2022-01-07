package model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Game {

    // fields
    private String name;
    private String store;
    private String publisher;
    private String developer;
    private List<String> genres = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private JSONObject gameDetails;

    // setter e getter
    public void setName(String name) {this.name = name;}
    public void setStore(String store) {this.store = store;}
    public void setDeveloper(String developer) {this.developer = developer;}
    public void setPublisher(String publisher) {this.publisher = publisher;}
    public void setGenres(List<String> genres) {this.genres = genres;}
    public void setLanguages(List<String> languages) {this.languages = languages;}
    public void setGameDetails(JSONObject gameDetails){this.gameDetails=gameDetails;}

    public String getName() {return name;}
    public String getStore() {return store;}
    public String getDeveloper() {return developer;}
    public String getPublisher() {return publisher;}
    public List<String> getGenres() {return genres;}
    public List<String> getLanguages() {return languages;}
    public JSONObject getGameDetails() {return gameDetails;}

    // metodi statici che comunicano con il backend

}
