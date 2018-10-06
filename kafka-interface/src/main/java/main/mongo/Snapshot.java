package main.mongo;

import java.util.ArrayList;
import java.util.Map;

public class Snapshot {
    private long version;
    private String aggregate;
    private ArrayList<Map> snapshot;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getAggregate() {
        return aggregate;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public ArrayList<Map> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ArrayList<Map> snapshot) {
        this.snapshot = snapshot;
    }
}
