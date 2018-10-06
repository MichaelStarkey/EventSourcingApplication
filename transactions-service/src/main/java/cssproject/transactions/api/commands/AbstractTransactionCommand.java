package cssproject.transactions.api.commands;

import cssproject.transactions.api.events.Event;
import cssproject.transactions.db.TransactionDAO;

import javax.xml.ws.Response;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractTransactionCommand extends AbstractCommand {

    protected final UUID id;
    protected final TransactionDAO transactionDAO;

    AbstractTransactionCommand(CommandType type, Map payload, TransactionDAO transactionDAO){
        this.type = type;
        this.payload = payload;
        this.id = UUID.randomUUID();
        this.transactionDAO = transactionDAO;
    }

    public abstract void apply();

    public UUID getId(){
        return this.id;
    }


}
