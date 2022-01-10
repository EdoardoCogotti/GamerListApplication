package controller;

import model.Game;
import model.User;

public final class Session {

    private static Session session = null;
    private User loggedUser = null;
    private Game currentGame;

    private Session() {}

    public static Session getInstance() {
        if(session == null)
            session = new Session();
        return session;
    }

    public void setLoggedUser(String nickname) {
        if(session != null) {
            session.loggedUser = new User(nickname);
        } else {
            throw new RuntimeException("You have to call getInstance once");
        }
    }

    public User getLoggedUser() {
        if(session != null) {
            return session.loggedUser;
        } else {
            throw new RuntimeException("You have to call getInstance once");
        }
    }

    public void setCurrentGame(Game g) {
        if(session != null) {
            session.currentGame = g;
        } else {
            throw new RuntimeException("You have to call getInstance once");
        }
    }

    public Game getCurrentGame() {
        if(session != null) {
            return session.currentGame;
        } else {
            throw new RuntimeException("You have to call getInstance once");
        }
    }

}