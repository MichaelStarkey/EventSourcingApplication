package cssproject.transactions.api.events;

import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.Map;
import java.util.UUID;

public class TransactionCompletedEvent extends TransactionEvent{

    public TransactionCompletedEvent(AbstractEvent.EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        super(type, payload, aggregate, command_id, transactionDAO);
    }

    @UnitOfWork
    @Override
    public void apply() {
        Transaction t = transactionDAO.findById(command_id.toString()).get();
        t.setStatus(AbstractEvent.EventType.COMPLETED.name());
        transactionDAO.update(t);
        updateVersion();
    }

    @Override
    public void handle() {

    }

}
