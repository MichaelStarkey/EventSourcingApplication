package main.kafka.Messaging;

import java.util.Map;

public class EventMessage {

    private String aggregate;
    private String type;
    private String command_id;
    private long version;
    private Map payload;

    public EventMessage(String aggregate, String type, String command_id, int version, Map payload){
        this.aggregate = aggregate;
        this.type = type;
        this.command_id = command_id;
        this.payload = payload;
        this.version = version;
    }

    public EventMessage(){

    }

    public String getAggregate() {
        return aggregate;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand_id() {
        return command_id;
    }

    public void setCommand_id(String command_id) {
        this.command_id = command_id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Map getPayload() {
        return payload;
    }

    public void setPayload(Map payload) {
        this.payload = payload;
    }
}
