package cssproject.transactions.api.commands;

import cssproject.transactions.db.TransactionDAO;

import java.util.LinkedList;

public class ConcreteCommand extends AbstractCommand {

    ConcreteCommand(){
    }

    public AbstractTransactionCommand getCommand(TransactionDAO transactionDAO){
        switch(getRawType()){
            case CREATE:
                return new TransactionCreateCommand(getRawType(), getPayload(), transactionDAO);
            case CREATE_DEPOSIT:
                return new DepositCreateCommand(getRawType(), getPayload(), transactionDAO);
            case CREATE_WITHDRAWAL:
                return new WithdrawalCreateCommand(getRawType(), getPayload(), transactionDAO);
        }
        return null;
    }
}
