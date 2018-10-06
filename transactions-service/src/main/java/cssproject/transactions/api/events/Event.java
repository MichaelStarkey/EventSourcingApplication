package cssproject.transactions.api.events;

import java.util.Map;
import java.util.UUID;

public interface Event {

    public void setType(String type);
    public String getType();
    public void setPayload(Map type);
    public Map getPayload();
    public void setAggregate(String topic);
    public String getAggregate();
    public void setCommand_id(UUID id);
    public UUID getCommand_id();
    public long getVersion();
    public void setVersion(long version);

}


