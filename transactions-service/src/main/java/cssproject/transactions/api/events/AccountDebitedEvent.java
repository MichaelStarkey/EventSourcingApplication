package cssproject.transactions.api.events;

import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;

import java.util.Map;
import java.util.UUID;

import static cssproject.transactions.api.events.AbstractEvent.EventType.DEBIT_RECORDED;

public class AccountDebitedEvent extends TransactionEvent {

    public AccountDebitedEvent(EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        super(type, payload, aggregate, command_id, transactionDAO);
    }

    @Override
    public void apply() {

    }

    @Override
    public void handle(){
        TransactionDebitRecordedEvent event = new TransactionDebitRecordedEvent(DEBIT_RECORDED, payload, "transactions", command_id, transactionDAO);
        event.apply();
        event.yield();
    }
}
