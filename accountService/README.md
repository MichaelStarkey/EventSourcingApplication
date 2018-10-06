## accountService

### What is accountService
An event-sourcing module that handles an Account aggregate, including event, command and query handlers.  

### How to use accountService
1. Install requirements: `pip install .`
2. Instruct flask to use accountService as app: `export FLASK_APP="accountService.run_app"`
3. Run service: `flask run`

Can be tested with: `python3 setup.py test`

### Supported Inbound Events:
**Endpoint:** `/accounts/events`  
transactionCreatedEvent:  
Occurs on the creation of a transaction, valid if:  
* `from_acc` has a corresponding account  
* `from_acc` has a balance >= `amount`  

On success, `accountDebitedEvent` is yielded, else an `accountDebitFailedEvent` is.
```
{
    "aggregate": "transactions",
    "type": "TRANSACTION_CREATED",
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "from_acc": <STRING:id_of_debiting_account>,
        "amount": <INT:amount_to_be_debited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

debitRecordedEvent:  
Occurs on the recording of an `accountDebitedEvent`, valid if:  
* `to_acc` has a corresponding account  

On success, `accountCreditedEvent` is yielded, else an `accountCreditFailedEvent` is.
```
{
    "aggregate": "transactions",
    "type": "DEBIT_RECORDED",
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "to_acc": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

withdrawalCreatedEvent:  
Occurs on the creation of a withdrawal, valid if:  
* `account` has a corresponding account  
* `account` has a balance >= `amount`  

On success, `accountWithdrawalEvent` is yielded, else an `accountWithdrawalFailedEvent` is.
```
{
    "aggregate": "transactions",
    "type": "WITHDRAWAL_CREATED",
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_debiting_account>,
        "amount": <INT:amount_to_be_debited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

depositCreatedEvent:  
Occurs on the creation of a deposit, valid if:  
* `account` has a corresponding account   

On success, `accountDepositedEvent` is yielded, else an `accountDepositFailedEvent` is.
```
{
    "aggregate": "transactions",
    "type": "WITHDRAWAL_CREATED",
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_debiting_account>,
        "amount": <INT:amount_to_be_debited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```
### Supported Outbound Events:
accountCreatedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_CREATED",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "user_id": <STRING:owners_user_uuid>,
        "balance": <INT:initial_balance>
    }
}
```

accountCreditedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_CREDITED",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "creditor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountCreditFailedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_CREDIT_FAILED",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "creditor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountDebitedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_DEBITED",
    "command_id": <STRING:id_of_initial_command>,
    "version": <INT:aggregate_version>,
    "payload": {
        "debtor_id": <STRING:id_of_debiting_account>,
        "amount": <INT:amount_that_has_been_debited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountDebitFailedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_DEBIT_FAILED",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "debtor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountDeletedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_DELETED",
    "command_id": <STRING:id_of_initial_command>,
    "version": <INT:aggregate_version>,
    "payload": {
        "account_id": <STRING:id_of_deleted_account>
    }
}
```
accountWithdrawnEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_WITHDRAWN_EVENT",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountWithdrawalFailedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_WITHDRAWAL_FAILED_EVENT",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountDepositedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_DEPOSITED_EVENT",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

accountDepositFailedEvent:
```
{
    "aggregate": "accounts",
    "type": "ACCOUNT_DEPOSIT_FAILED_EVENT",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_that_has_been_credited>,
        "transaction_id": <STRING:transaction_id>
    }
}
```

### Supported Commands:  
**Endpoint:** `/accounts/commands`  
createAccountCommand:
```
{
    "type": "CREATE",
    "payload": {
        "balance": <INT:initial_balance>,
        "user_id": <STRING:owners_user_uuid>
    }
}
```
deleteAccountCommand:  
```
{
    "type": "DELETE",
    "payload": {
      "account_id": <STRING:to_be_deleted_account_id>
    }
}
```

### Supported Queries
**Endpoint:** `/accounts/<STRING:account_id>/`  
Account ID query, returns one account.  
**Endpoint:** `/accounts/users/<STRING:user_id>/`  
User ID query, returns list of accounts.  


### Acceptance Criteria
This service must:  
* Accept and validate the following commands:
  * accountCreateCommand, which in turn yields an accountCreatedEvent.
  * accountDeleteCommand, which in turn yields an accountDeletedEvent.
* Accept and validate the following events:
  * transactionCreatedEvent.
  * debitRecordedEvent.
  * depositCreatedEvent
  * withdrawalCreatedEvent
* Yield events:
  * accountCreatedEvent, which defines the method to create an account.
  * accountDeletedEvent, which defines the method to delete an account.
  * accountCreditedEvent, which defines the method to credit an account (increase its balance).
  * accountDebitedEvent, which defines the method to debit an account (reduce its balance).
  * accountDepositedEvent, which defines the method for depositing money to an account (increase its balance).
  * accountWithdrawnEvent, which defines the method for withdrawing money from an account (reducing its balance).
  * accountDebitFailedEvent, to inform the system on a failed account debit.
  * accountCreditFailedEvent, to inform  the system on a failed account credit, and to reimburse the corresponding debtor.
  * accountDepositFailedEvent, to inform the system on a failed account deposit.
  * accountWithdrawalFailedEvent, to inform the system on a failed account withdrawal.
 * Replay events, by querying an eventStore for all past events.
 * Take regular snapshots, by recording the aggregate version, saving the state periodically.
