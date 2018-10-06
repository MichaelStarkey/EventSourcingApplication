## transactionService

### What is transactionService
An event-sourcing module that handles a Transaction aggregate, including event, command and query handlers.

### How to use transactionService

Requirements:\
   maven\
   docker

Command Line:
1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/Transactions-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:3030`

Docker:
1. Run `mvn clean install` to build your application
1. Create a docker container with `docker build -t transactions-service .`
1. Run docker container with `docker run -p <PORT>:<PORT> transactions-service`

### Health Check

To see your applications health enter url `http://localhost:8081/healthcheck`


### Supported Inbound Events:
**Endpoint:** `/transactions/events`

AccountCreditedEvent:
```
{
    "type": "ACCOUNT_CREDITED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "creditor_id": <STRING:id_of_debiting_account>,
        "amount": <INT:amount_to_be_debited>,
    }
}
```

AccountDebitedEvent:
```
{
    "type": "ACCOUNT_DEBITED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "debtor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountDepositedEvent:
```
{
    "type": "ACCOUNT_DEPOSITED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountWithdrawnEvent:
```
{
    "type": "ACCOUNT_WITHDRAWN",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountDebitFailedEvent:
```
{
    "type": "ACCOUNT_DEBIT_FAILED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "debtor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountCreditFailedEvent:
```
{
    "type": "ACCOUNT_CREDIT_FAILED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "creditor_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountWithdrawalFailedEvent:
```
{
    "type": "ACCOUNT_WITHDRAWAL_FAILED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```

AccountDepositFailedEvent:
```
{
    "type": "ACCOUNT_DEPOSIT_FAILED",
    "aggregate": "accounts",
    "version": <INT:aggregate_version>,
    "command_id": <STRING:id_of_initial_command>,
    "payload": {
        "account_id": <STRING:id_of_crediting_account>,
        "amount": <INT:amount_to_be_credited>,
    }
}
```
### Supported Outbound Events:
TransactionCreatedEvent:
```
{
     "type": "TRANSACTION_CREATED",
     "aggregate": "transactions",
     "version": <INT:aggregate_version>,
     "command_id": <STRING:id_of_initial_command>,
     "payload": {
         "debtor_id": <STRING:id_of_crediting_account>,
         "amount": <INT:amount_to_be_credited>,
     }
}
```

DepositCreatedEvent:
```
{
     "type": "WITHDRAWAL_CREATED",
     "aggregate": "transactions",
     "version": <INT:aggregate_version>,
     "command_id": <STRING:id_of_initial_command>,
     "payload": {
         "account_id": <STRING:id_of_crediting_account>,
         "amount": <INT:amount_to_be_credited>,
     }
}
```

WithdrawalCreatedEvent:
```
{
     "type": "DEPOSIT_CREATED",
     "aggregate": "transactions",
     "version": <INT:aggregate_version>,
     "command_id": <STRING:id_of_initial_command>,
     "payload": {
         "account_id": <STRING:id_of_crediting_account>,
         "amount": <INT:amount_to_be_credited>,
     }
}
```

TransactionDebitRecordedEvent:
```
{
     "type": "DEBIT_RECORDED",
     "aggregate": "transactions",
     "version": <INT:aggregate_version>,
     "command_id": <STRING:id_of_initial_command>,
     "payload": {
         "creditor_id": <STRING:id_of_crediting_account>,
         "amount": <INT:amount_to_be_credited>,
     }
}
```


### Supported Commands:
**Endpoint:** `/transactions/commands`  
CreateTransactionCommand:
```
{
    "type": "CREATE",
    "payload": {
        "amount": <INT:initial_balance>,
        "creditor_id": <STRING:owners_user_uuid>,
        "debtor_id": <STRING:owners_user_uuid>
    }
}
```

CreateDepositCommand:
```
{
    "type": "CREATE_DEPOSIT",
    "payload": {
        "amount": <INT:initial_balance>,
        "account_id": <STRING:owners_user_uuid>,
        "debtor_id": <STRING:owners_user_uuid>
    }
}
```

CreateWithdrawalCommand:
```
{
    "type": "CREATE_WITHDRAWAL",
    "payload": {
        "amount": <INT:initial_balance>,
        "account_id": <STRING:owners_user_uuid>,
        "debtor_id": <STRING:owners_user_uuid>
    }
}
```

### Supported Queries
**Endpoint:** `/transactions/{transaction_id}`

Transaction ID query, returns one transaction:

**Endpoint:** `/transactions/users/{user_id}`

User ID query, returns list of transactions with user as creditor or debtor:

**Endpoint:** `/transactions/`

Returns the current service version.
