package main.web;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import main.kafka.Messaging.EventMessage;
import main.mongo.Snapshot;
import main.mongo.branches.Branch;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

@RestController
public class MongoController {

    @RequestMapping(value = "/queries/events/{topic}", method = RequestMethod.GET, params = {"from"})
    public ResponseEntity getEvents(@PathVariable("topic") String topic, @RequestParam(value = "from") String version) {
        long ver = Long.parseLong(version);
        ArrayList<EventMessage> output = query_mongo(topic, ver);
        return new ResponseEntity<ArrayList>(output, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/queries/events/{topic}", method = RequestMethod.GET)
    public ResponseEntity getEvents(@PathVariable("topic") String topic) {
        ArrayList<EventMessage> output = query_mongo(topic, 0);
        return new ResponseEntity<ArrayList>(output, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/queries/snapshots/{aggregate}", method = RequestMethod.GET)
    public ResponseEntity getSnapshot(@PathVariable("aggregate") String aggregate) {
        MongoDatabase db = Application.database.getDb();
        MongoCollection<Document> c = db.getCollection("snapshots");

        Bson branch_cond = Application.database.getEventTree().get_conditional();
        Bson filter = and(eq("aggregate", aggregate), branch_cond);

        Document sort = new Document("version", -1);
        Document snapshot = c.find(filter).sort(sort).first();

        if (snapshot != null) {
            Snapshot snap = new Snapshot();
            snap.setAggregate(aggregate);
            snap.setVersion(snapshot.getLong("version"));
            snap.setSnapshot(snapshot.get("snapshot", ArrayList.class));

            return new ResponseEntity<> (snap, HttpStatus.OK);
        }
        return new ResponseEntity<> (null, HttpStatus.OK);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity insert(@RequestBody EventMessage message) {
        MongoDatabase db = Application.database.getDb();
        MongoCollection<Document> c = db.getCollection("events");
        long time = System.currentTimeMillis();

        Map payload = message.getPayload();


        Document event = new Document("aggregate", message.getAggregate())
                .append("timestamp", time)
                .append("payload", payload)
                .append("command_id", message.getCommand_id())
                .append("version", message.getVersion())
                .append("type", message.getType())
                .append("branch", Application.database.getEventTree().getCurrentBranch());
        c.insertOne(event);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/snapshots", method = RequestMethod.POST)
    public ResponseEntity insert_snapshot(@RequestBody Snapshot snapshot) {
        insert_mongo(snapshot);

        return new ResponseEntity<>(snapshot, HttpStatus.OK);
    }

    @RequestMapping(value = "/queries/branches", method = RequestMethod.GET)
    public ResponseEntity get_branches(){
        ArrayList<String> branches = new ArrayList<>(Application.database.getEventTree().getBranches().keySet());
        HashMap output = new HashMap();
        output.put("current", Application.database.getEventTree().getCurrentBranch());
        output.put("branches", branches);
        return new ResponseEntity<HashMap>(output, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/branch/{branch_name}/{timestamp}", method = RequestMethod.PUT)
    public ResponseEntity create_branch(
            @PathVariable("branch_name") String branch_name,
            @PathVariable("timestamp") String str_timestamp
    ){
        long timestamp = Long.parseLong(str_timestamp);
        Branch branch = Application.database.getEventTree().branch(branch_name, timestamp);
        if (branch == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Application.database.saveEventTree();

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/checkout/{branch_name}", method = RequestMethod.PUT)
    public ResponseEntity create_branch(@PathVariable("branch_name") String branch_name){
        Branch branch = Application.database.getEventTree().getBranches().get(branch_name);
        if (branch == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Application.database.getEventTree().checkout(branch_name);
        Application.database.saveEventTree();

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    private void insert_mongo(Snapshot snapshot) {
        MongoDatabase db = Application.database.getDb();
        MongoCollection<Document> c = db.getCollection("snapshots");
        long time = System.currentTimeMillis();

        Document snap = new Document("aggregate", snapshot.getAggregate())
                .append("version", snapshot.getVersion())
                .append("snapshot", snapshot.getSnapshot())
                .append("timestamp", time)
                .append("branch", Application.database.getEventTree().getCurrentBranch());
        c.insertOne(snap);
    }

    private ArrayList<EventMessage> query_mongo(String value, long version) {
        MongoDatabase db = Application.database.getDb();
        MongoCollection<Document> c = db.getCollection("events");

        Bson branch_cond = Application.database.getEventTree().get_conditional();
        Bson filter = and(eq("aggregate", value), gt("version", version), branch_cond);

        Document sort = new Document("timestamp", 1);
        ArrayList<EventMessage> output = new ArrayList<>();

        try (MongoCursor<Document> cur = c.find(filter).sort(sort).iterator()) {
            while (cur.hasNext()) {
                Document next = cur.next();

                String type = next.getString("type");
                String command_id = next.getString("command_id");
                String aggregate = next.getString("aggregate");
                Map payload = next.get("payload", Map.class);
                long e_version = next.getLong("version");

                EventMessage temp = new EventMessage();
                temp.setAggregate(aggregate);
                temp.setType(type);
                temp.setCommand_id(command_id);
                temp.setPayload(payload);
                temp.setVersion(e_version);

                output.add(temp);
            }
        }

        return output;
    }
}
