package it.unipi.gamerlist.model;

public class GamerListElement {

    private String name;
    private String publisher;
    private String developer;
    private int friendsCount;

    public GamerListElement() {

    }

    public GamerListElement(String name, String publisher, String developer, int friendsCount) {
        this.name = name;
        this.publisher = publisher;
        this.developer = developer;
        this.friendsCount = friendsCount;
    }

    public String getName() {
        return name;
    }
    public String getPublisher() {
        return publisher;
    }
    public String getDeveloper() {
        return developer;
    }
    public int getFriendsCount() {return friendsCount;}

    public void setName(String name) {
        this.name = name;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public void setDeveloper(String developer) {
        this.developer = developer;
    }
    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }
}
