package cssproject.transactions.resources;

import com.codahale.metrics.annotation.Timed;
import cssproject.transactions.api.commands.AbstractTransactionCommand;
import cssproject.transactions.api.commands.ConcreteCommand;
import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/transactions/commands/")
@Produces(MediaType.APPLICATION_JSON)
public class CommandResource {

    private final TransactionDAO transactionDAO;

    public CommandResource(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response commandHandler(ConcreteCommand command){
	AbstractTransactionCommand newCommand = command.getCommand(transactionDAO);
        newCommand.apply();
        return Response.accepted(UriBuilder.fromResource(CommandResource.class)
                .build(newCommand.getId()))
                .build();
    }
}
