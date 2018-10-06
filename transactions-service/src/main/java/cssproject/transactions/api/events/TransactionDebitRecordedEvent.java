package cssproject.transactions.api.events;

import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.Map;
import java.util.UUID;

public class TransactionDebitRecordedEvent extends TransactionEvent {

    public TransactionDebitRecordedEvent(EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        super(type, payload, aggregate, command_id, transactionDAO);
    }

    @UnitOfWork
    @Override
    public void apply(){
        Transaction t = transactionDAO.findById(command_id.toString()).get();
        t.setStatus(type.getType());
        t = transactionDAO.update(t);
        payload.clear();
        payload.put("creditor_id", t.getCreditor());
        payload.put("amount", t.getAmount());
        updateVersion();
    }

    @Override
    public void handle() {

    }

}
