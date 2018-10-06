package main.mongo.branches;

import org.bson.Document;

public class Branch {

    private long beginTimestamp;
    private String source;
    private String name;

    public Branch(long timestamp, String name){
        this.beginTimestamp = timestamp;
        this.source = null;
        this.name = name;
    }

    public Branch(long timestamp, String source, String name){
        this.beginTimestamp = timestamp;
        this.source = source;
        this.name = name;
    }

    public Branch(Document branch) {
        this.beginTimestamp = branch.getLong("beginTimestamp");
        this.source = branch.getString("source");
        this.name = branch.getString("name");
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public Document toDoc(){
        return new Document("name", this.name)
                .append("beginTimestamp", this.beginTimestamp)
                .append("source", this.source);
    }
}
