package driver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDriver {

    private static MongoDriver driverInstance = null;
    private MongoClient client;
    private MongoDatabase database;

    private MongoDriver() {
        client = MongoClients.create("mongodb://localhost:27017/");
        database = client.getDatabase("GamerList");
    }

    // Singleton Pattern
    public static MongoDriver getInstance() {
        if(driverInstance == null)
            driverInstance = new MongoDriver();
        return driverInstance;
    }

    public MongoCollection<Document> getCollection(String collection) {
        if(driverInstance == null)
            throw new RuntimeException("ERROR: Connection doesn't exist (getInstance() method not called)");
        else
            return driverInstance.database.getCollection(collection);
    }

    public void close() {
        if(driverInstance == null)
            throw new RuntimeException("ERROR: Connection doesn't exist (getInstance() method not called)");
        else{
            driverInstance.client.close();
            driverInstance = null;
        }
    }

}