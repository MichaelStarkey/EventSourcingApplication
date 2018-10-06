package cssproject.transactions.api.events;

import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;

import java.util.Map;
import java.util.UUID;

public class AccountWithdrawnEvent extends TransactionEvent {

    public AccountWithdrawnEvent(EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        super(type, payload, aggregate, command_id, transactionDAO);
    }


    @Override
    public void apply() {

    }

    @Override
    public void handle(){
        TransactionCompletedEvent event = new TransactionCompletedEvent(EventType.COMPLETED, payload, "transactions", command_id, transactionDAO);
        event.apply();
        event.yield();
    }
}
