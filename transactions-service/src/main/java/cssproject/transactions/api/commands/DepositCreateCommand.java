package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.AbstractEvent;
import cssproject.transactions.api.events.ConcreteEvent;
import cssproject.transactions.api.events.DepositCreatedEvent;
import cssproject.transactions.db.TransactionDAO;


import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class DepositCreateCommand extends AbstractTransactionCommand {

    public DepositCreateCommand(CommandType type, Map payload, TransactionDAO transactionDAO){
        super(type, payload, transactionDAO);
    }

    public void apply(){
	System.out.println("I am hereere");
        DepositCreatedEvent event = new DepositCreatedEvent(AbstractEvent.EventType.DEPOSIT_CREATED, payload, "transactions", id, transactionDAO);
        event.apply();
        event.yield();
    }


}
