package cssproject.transactions.api.events;


import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.Map;
import java.util.UUID;

public class DepositCreatedEvent extends TransactionEvent {

    public DepositCreatedEvent(EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        super(type, payload, aggregate, command_id, transactionDAO);
    }

    @UnitOfWork
    @Override
    public void apply(){
        Transaction t = transactionDAO.create(new Transaction(command_id.toString(), payload.get("account_id").toString(), "DEPOSIT", (Integer) payload.get("amount"), type.name()));
        System.out.println("created " + t);
        updateVersion();
    }

    @Override
    public void handle() {

    }

}
