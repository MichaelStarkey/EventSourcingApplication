package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.AbstractEvent;
import cssproject.transactions.api.events.ConcreteEvent;
import cssproject.transactions.api.events.WithdrawalCreatedEvent;
import cssproject.transactions.db.TransactionDAO;


import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class WithdrawalCreateCommand extends AbstractTransactionCommand {

    public WithdrawalCreateCommand(CommandType type, Map payload, TransactionDAO transactionDAO){
        super(type, payload, transactionDAO);
    }

    public void apply(){
        WithdrawalCreatedEvent event = new WithdrawalCreatedEvent(AbstractEvent.EventType.WITHDRAWAL_CREATED, payload, "transactions", id, transactionDAO);
        event.apply();
        event.yield();
    }


}
