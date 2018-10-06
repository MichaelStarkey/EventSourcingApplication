package cssproject.transactions.api.events;

import cssproject.transactions.TransactionsApplication;
import cssproject.transactions.db.TransactionDAO;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TransactionEvent extends AbstractEvent{

    protected final TransactionDAO transactionDAO;
    public static AtomicLong service_version = new AtomicLong(0);

    TransactionEvent(EventType type, Map payload, String aggregate, UUID command_id, TransactionDAO transactionDAO){
        this.type = type;
        this.payload = payload;
        this.aggregate = aggregate;
        this.command_id = command_id;
        this.transactionDAO = transactionDAO;
        this.version = service_version.get();
    }

    public void updateVersion(){
        service_version.incrementAndGet();
        setVersion(service_version.get());
    }

    public abstract void apply();

    public abstract void handle();

}
