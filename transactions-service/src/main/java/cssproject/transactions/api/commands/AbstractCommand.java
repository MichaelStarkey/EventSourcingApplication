package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.*;

import javax.xml.ws.Response;
import java.util.LinkedList;
import java.util.Map;

public abstract class AbstractCommand implements Command {

    protected CommandType type;
    protected Map payload;

    public String getType() {
        return type.name();
    }

    public CommandType getRawType(){
        return type;
    }

    public void setType(String type){
        this.type = CommandType.valueOf(type);
    }

    public Map getPayload() {
        return payload;
    }

    public void setPayload(Map payload){
        this.payload = payload;
    }

    public enum CommandType {
        CREATE,
        CREATE_DEPOSIT,
        CREATE_WITHDRAWAL;
    }

}
