package cssproject.transactions.resources;

import com.codahale.metrics.annotation.Timed;
import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.server.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/transactions/users/{user_id}")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {

    private TransactionDAO dao;

    public TransactionsResource(TransactionDAO dao) {
        this.dao = dao;
    }

    @GET
    @UnitOfWork
    public List<Transaction> getTransactionByUserId(@PathParam("user_id") String userId) {
        return dao.findByUserId(userId);
    }
}
