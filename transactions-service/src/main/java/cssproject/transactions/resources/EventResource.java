package cssproject.transactions.resources;

import com.codahale.metrics.annotation.Timed;
import cssproject.transactions.api.events.ConcreteEvent;
import cssproject.transactions.api.events.TransactionEvent;
import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/transactions/events/")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    protected final TransactionDAO transactionDAO;

    public EventResource(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }

    @POST
    @Timed
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eventHandler(ConcreteEvent event){
        TransactionEvent newEvent = event.getEvent(transactionDAO);
        System.out.println(newEvent.getType());
        newEvent.handle();
        return Response.accepted().build();
    }

}
