package utils;

import model.Game;
import org.neo4j.driver.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

public class GraphDriverManager implements AutoCloseable
{
    private final Driver driver;

    public GraphDriverManager(String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    //questa funzione aggiunge un utente al graph
    public void addUserToGraph( final String user)
    {
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:User {username: $username})",
                        parameters( "username", user));
                return null;
            });
        }
    }

    //questa funzione aggiunge un gioco al graph
    public void addGameToGraph( final Game game)
    {
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:Game {name: $N})",
                        parameters( "N", game.getName()));
                int i = 0;
                for(String val : game.getGenres()) {
                    if (i == 0) {
                        tx.run("MERGE (p:Game {name: $N}) \n" +
                                        "SET p.genre = [$G] \n",
                                parameters("N", game.getName(), "G", val));
                        i++;
                    } else {
                        tx.run("MERGE (p:Game {name: $N}) \n" +
                                        "SET p.genre = [$G] + p.genre",
                                parameters("N", game.getName(), "G", val));
                        i++;
                    }
                }

                return null;
            });
        }
    }

    //questa funzione permette ad un utente di seguirne un altro
    public void addFollow(final String following, final String followed) {

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:User)\n" +
                                "WHERE a.username = $A AND b.username = $B\n" +
                                "MERGE (a)-[r:FOLLOWING]->(b)\n" +
                                "RETURN type(r)",
                        parameters( "A", following, "B", followed));
                return null;
            });
        }
    }

    //questa funzione inserisce un gioco nella lista dei giocati di un utente
    public void addInGameList(final String user, final String game) {

        try ( Session session = driver.session() )
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

    //questa funzione inserisce un gioco nella lista dei recensiti di un utente e definisce se recensione pos o neg
    public void addReview(final String user, final String game, boolean review) {

        try ( Session session = driver.session() )
        {
            String reviewType;
            if(review)
                reviewType = "Positive";
            else reviewType = "Negative";
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:Game)\n" +
                                "WHERE a.username = $A AND b.name = $B\n" +
                                "MERGE (a)-[r:HAS_REVIEWED {type: $C}]->(b)\n" +
                                "RETURN type(r)",
                        parameters( "A", user, "B", game, "C", reviewType));
                return null;
            });
        }
    }

    //questa funzione rimuove le recensioni fatte da un utente a un gioco
    public void removeReview(final String user, final String game) {
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (user {username: $A})-[r:HAS_REVIEWED]->(game {name: $B})\n" +
                                "DELETE r",
                        parameters( "A", user, "B", game));
                return null;
            });
        }
    }

    //questa funzione rimuove un gioco dalla lista dell'utente
    public void removeFromList(final String user, final String game) {
        try ( Session session = driver.session() )
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

    //questa funzione permette ad un utente di smettere di seguire un altro utente
    public void unfollowUser(final String followed, final String following) {
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH\n" +
                                "  (a:User),\n" +
                                "  (b:User)\n" +
                                "WHERE a.username = $A AND b.username = $B\n" +
                                "DELETE (a)-[r:FOLLOWING]->(b)\n" +
                                "RETURN type(r)",
                        parameters( "A", following, "B", followed));
                return null;
            });
        }
    }


    //questa funzione setta il genere più giocato dall'utente
    public void setMostPlayed(final String user) {
        try ( Session session = driver.session() )
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

    //questa funzione, per un utente, stampa i seguiti dei seguiti con lo stesso genere piu giocato
    public void followedByFollowedSameGenre(final String user) {

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                Result sameGenre = tx.run(
                        "MATCH (a:User)-[:FOLLOWING*2]-(user)\n" +
                                "WHERE a.username = $A " +
                                "AND NOT user.username = a.username " +
                                "AND user.most_played_genre = a.most_played_genre " +
                                "AND NOT (a)-[:FOLLOWING]-(user)\n" +
                                "RETURN DISTINCT user.username as username\n" +
                                "LIMIT 5",
                        parameters( "A", user));
                while(sameGenre.hasNext()) {
                    Record record = sameGenre.next();
                    System.out.println("friend of friend: ");
                    System.out.println(record.get("username").asString());
                }
                return null;
            });
        }
    }

    //questa funzione, per un utente, stampa i giochi con più recensioni positive dai suoi following
    public void bestGamesByFollowing(final String user) {

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                Result positiveRev = tx.run(
                        "MATCH (a:User)-[:FOLLOWING]-(user)-[r:HAS_REVIEWED]-(game)\n" +
                                "WHERE a.username = $A " +
                                "AND r.type = 'Positive'\n" +
                                "AND NOT (a)-[:HAS_PLAYED]-(game)\n" +
                                "RETURN game.name as name, count(*) as occurrences\n" +
                                "ORDER BY occurrences DESC\n" +
                                "LIMIT 3",
                        parameters( "A", user));
                while(positiveRev.hasNext()) {
                    Record record = positiveRev.next();
                    System.out.println("Reviewed game: ");
                    System.out.println(record.get("name").asString());
                    System.out.println(record.get("occurrences").asInt());
                }
                return null;
            });
        }
    }

    //questa funzione, per un utente, stampa i giochi nella sua lista in ordine per numero di following che ci giocano
    public void orderGamesByFollowing(final String user) {

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
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

    //questa funzione, per un utente e un gioco nella sua lista dice il numero di following che ci giocano
    public void howManyFollowingPlayGame(final String user, final String game) {

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                Result positiveRev = tx.run(
                        "MATCH (a:User)-[:FOLLOWING]-(user)-[:HAS_PLAYED]-(game)\n" +
                                "WHERE a.username = $A " +
                                "AND game.name = $B\n" +
                                "AND (a)-[:HAS_PLAYED]-(game)\n" +
                                "RETURN game.name as name, count(*) as occurrences\n",
                        parameters( "A", user, "B", game));
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

}

