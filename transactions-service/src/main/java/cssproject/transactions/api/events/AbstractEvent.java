package cssproject.transactions.api.events;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractEvent implements Event {

    protected String endpoint = "http://localhost:8080/publish/";
    protected String aggregate;
    protected EventType type;
    protected UUID command_id;
    private static int version_id = 0;
    protected long version;
    protected Map payload;

    public String getType(){
        return type.name();
    }

    public void setType(String type){
        this.type = EventType.valueOf(type);
    }

    public Map getPayload(){
        return payload;
    }

    public void setPayload(Map payload){
        this.payload = payload;
    }

    public UUID getCommand_id() {
        return command_id;
    }

    public void setCommand_id(UUID command_id) {
        this.command_id = command_id;
    }

    public String getAggregate() {
        return aggregate;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void yield(){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<AbstractEvent> requestEntity = new HttpEntity<>(this);
        ResponseEntity<String> response = restTemplate.
                exchange(endpoint, HttpMethod.POST, requestEntity, java.lang.String.class);
    }

    public enum EventType implements Serializable {
        TRANSACTION_CREATED,
        DEPOSIT_CREATED,
        WITHDRAWAL_CREATED,
        ACCOUNT_DEBITED,
        ACCOUNT_DEBIT_FAILED,
        ACCOUNT_DEPOSITED,
        ACCOUNT_DEPOSIT_FAILED,
        ACCOUNT_WITHDRAWN,
        ACCOUNT_WITHDRAWAL_FAILED,
        DEBIT_RECORDED,
        ACCOUNT_CREDITED,
        ACCOUNT_CREDIT_FAILED,
        FAILED,
        COMPLETED;

        @JsonValue
        public String getType(){
            return this.name();
        }
    }
}
