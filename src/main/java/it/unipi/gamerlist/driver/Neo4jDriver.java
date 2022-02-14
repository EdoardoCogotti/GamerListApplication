package it.unipi.gamerlist.driver;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDriver {

    private static Neo4jDriver neoInstance = null;
    private final Driver driver;

    private Neo4jDriver() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "root"));
    }

    // singleton Pattern
    public static Neo4jDriver getInstance() {
        if(neoInstance == null)
            neoInstance = new Neo4jDriver();
        return neoInstance;
    }

    public Driver getDriver() {
        if(neoInstance == null)
            throw new RuntimeException("ERROR: Connection doesn't exist (getInstance() method not called)");
        else
            return neoInstance.driver;
    }

    public void close() {
        if(neoInstance == null)
            throw new RuntimeException("ERROR: Connection doesn't exist (getInstance() method not called)");
        else
            neoInstance.driver.close();
    }

}