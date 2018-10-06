package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.AbstractEvent;
import cssproject.transactions.api.events.ConcreteEvent;
import cssproject.transactions.api.events.TransactionCreatedEvent;
import cssproject.transactions.db.TransactionDAO;


import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class TransactionCreateCommand extends AbstractTransactionCommand {

    public TransactionCreateCommand(CommandType type, Map payload, TransactionDAO transactionDAO){
        super(type, payload, transactionDAO);
    }

    public void apply(){
        TransactionCreatedEvent event = new TransactionCreatedEvent(AbstractEvent.EventType.TRANSACTION_CREATED, payload, "transactions", id, transactionDAO);
        event.apply();
        event.yield();
    }


}
