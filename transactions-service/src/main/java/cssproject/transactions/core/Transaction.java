package cssproject.transactions.core;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transactions")
@NamedQueries(
        {
                @NamedQuery(
                        name = "cssproject.transactions.core.Transaction.findAll",
                        query = "SELECT t FROM Transaction t"
                ),
                @NamedQuery(
                        name = "cssproject.transactions.core.Transaction.findByUserId",
                        query = "SELECT t FROM Transaction t WHERE t.creditor=:userId or t.debtor=:userId"
                )
        })

public class Transaction {

    @Id
    private String id;

    @Column(name = "creditor", nullable = false)
    private String creditor;

    @Column(name = "debtor", nullable = false)
    private String debtor;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "status", nullable = false)
    private String status;

    public Transaction() {
    }

    public Transaction(String id, String creditor, String debtor, int amount, String status) {
        this.id = id;
        this.creditor = creditor;
        this.debtor = debtor;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCreditor() {
        return creditor;
    }

    public String getDebtor() {
        return debtor;
    }

    public int getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }

        final Transaction that = (Transaction) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.creditor, that.creditor) &&
                Objects.equals(this.debtor, that.debtor) &&
                Objects.equals(this.amount, that.amount) &&
                Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creditor, debtor, amount, status);
    }

}
