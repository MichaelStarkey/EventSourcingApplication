package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.AbstractEvent;
import cssproject.transactions.api.events.Event;

import javax.xml.ws.Response;
import java.util.LinkedList;
import java.util.Map;

public interface Command {

    public void setType(String type);
    public String getType();
    public void setPayload(Map payload);
    public Map getPayload();

}
