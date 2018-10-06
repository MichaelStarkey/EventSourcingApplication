package cssproject.transactions.db;

import cssproject.transactions.core.Transaction;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class TransactionDAO extends AbstractDAO<Transaction> {

    public TransactionDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public List<Transaction> findByUserId(String id) {
        return list(namedQuery("cssproject.transactions.core.Transaction.findByUserId").setParameter("userId", id));
    }

    public Transaction create(Transaction transaction) {
        return persist(transaction);
    }

    public Transaction update(Transaction transaction) {
        return persist(transaction);
    }

    public List<Transaction> findAll() {
        return list(namedQuery("cssproject.transactions.core.Transaction.findAll"));
    }
}
