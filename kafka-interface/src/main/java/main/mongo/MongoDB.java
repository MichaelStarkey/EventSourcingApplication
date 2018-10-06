package main.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import main.kafka.Messaging.EventMessage;
import main.mongo.branches.EventTree;
import org.bson.BsonDocument;
import org.bson.Document;

public class MongoDB {

    private final MongoDatabase db;
    private MongoClient client;
    private EventTree eventTree;

    public MongoDB(){
        MongoClient client = new MongoClient("localhost", 27017);
        this.client = client;
        this.db = client.getDatabase("eventStore");

        this.eventTree = this.loadEventTree();
        if (this.eventTree == null) {
            this.eventTree = new EventTree();
            this.saveEventTree();
        }
    }

    public MongoDatabase getDb() {
        return db;
    }

    public EventTree getEventTree() {
        return this.eventTree;
    }

    public void write(EventMessage message){
        System.out.println("writing " + message.getPayload() + message.getType());
        MongoCollection<Document> c = db.getCollection("events");
        long time = System.currentTimeMillis();


        Document event = new Document("aggregate", message.getAggregate())
                .append("branch", this.eventTree.getCurrentBranch())
                .append("timestamp", time)
                .append("payload", message.getPayload())
                .append("command_id", message.getCommand_id())
                .append("version", message.getVersion())
                .append("type", message.getType());
        c.insertOne(event);
        System.out.println(event);
    }

    public void saveEventTree(){
        MongoCollection<Document> c = this.db.getCollection("eventTree");
        Document eventTree = this.eventTree.toDoc();
        c.replaceOne(new BsonDocument(), eventTree, new UpdateOptions().upsert(true));
    }

    private EventTree loadEventTree(){
        MongoCollection<Document> c = this.db.getCollection("eventTree");
        MongoCursor<Document> cur = c.find().iterator();
        if (!cur.hasNext()) return null;
        Document eventTree = cur.next();
        return new EventTree(eventTree);
    }

    public void close(){
        this.client.close();
    }
}
