package model;

public class GamerListElement {

    // fields
    private String name;
    private String publisher;
    private String developer;
    private int friendsCount;

    public String getName() {
        return name;
    }
    public String getPublisher() {
        return publisher;
    }
    public String getDeveloper() {
        return developer;
    }
    public int getFriendsCount() {
        return friendsCount;
    }

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
