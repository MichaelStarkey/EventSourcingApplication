package cssproject.transactions.resources;

import com.codahale.metrics.annotation.Timed;
import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/transactions/{transaction_id}")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private TransactionDAO dao;

    public TransactionResource(TransactionDAO dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    @UnitOfWork
    public Transaction getTransaction(@PathParam("transaction_id") String transactionId) {
        return findSafely(transactionId);
    }

    private Transaction findSafely(String transactionId) {
        return dao.findById(transactionId).orElseThrow(() -> new NotFoundException("No such transaction."));

    }
}