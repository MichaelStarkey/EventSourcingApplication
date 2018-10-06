package cssproject.transactions.api.events;

import cssproject.transactions.db.TransactionDAO;

public class ConcreteEvent extends AbstractEvent {

    public ConcreteEvent(){

    }

    public TransactionEvent getEvent(TransactionDAO transactionDAO){
        EventType type = AbstractEvent.EventType.valueOf(getType());
        switch (type){
            case TRANSACTION_CREATED:
                return new TransactionCreatedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case DEPOSIT_CREATED:
                return new DepositCreatedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case WITHDRAWAL_CREATED:
                return new WithdrawalCreatedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_CREDITED:
                return new AccountCreditedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_DEBITED:
                return new AccountDebitedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_DEPOSITED:
                return new AccountDepositedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_WITHDRAWN:
                return new AccountWithdrawnEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_CREDIT_FAILED:
                return new AccountCreditFailedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_DEBIT_FAILED:
                return new AccountDebitFailedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_DEPOSIT_FAILED:
                return new AccountDepositFailedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case ACCOUNT_WITHDRAWAL_FAILED:
                return new AccountWithdrawalFailedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case DEBIT_RECORDED:
                return new TransactionDebitRecordedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case COMPLETED:
                return new TransactionCompletedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
            case FAILED:
                return new TransactionFailedEvent(type, getPayload(), getAggregate(), getCommand_id(), transactionDAO);
        }
        return null;
    }


}
