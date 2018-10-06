from accountService.eventSourcing.commands import CreateAccountCommand, DeleteAccountCommand, RefreshDataStoreCommand
from accountService.eventSourcing.events import AccountCreatedEvent, AccountDeletedEvent, AccountCreditedEvent, \
    AccountDebitedEvent, AccountDepositedEvent, AccountWithdrawnEvent, AccountCreditFailedEvent, AccountDebitFailedEvent, \
    AccountDepositFailedEvent, AccountWithdrawalFailedEvent
from accountService.eventSourcing.handlers import DebitRecordedEventHandler, TransactionCreatedEventHandler, \
    DepositCreatedEventHandler, WithdrawalCreatedEventHandler


def handle_event(event_type, command_id, payload):
    handlers = {
        "DEBIT_RECORDED": DebitRecordedEventHandler,
        "TRANSACTION_CREATED": TransactionCreatedEventHandler,
        "DEPOSIT_CREATED": DepositCreatedEventHandler,
        "WITHDRAWAL_CREATED": WithdrawalCreatedEventHandler
    }
    if event_type not in handlers:
        return False, "NO HANDLER FOR EVENT"

    handler = handlers[event_type](command_id, payload)
    handler.apply()
    return True, handler.to_dict()


def handle_command(command_type, payload):
    commands = {
        "CREATE": CreateAccountCommand,
        "DELETE": DeleteAccountCommand,
        "REFRESH_DATA_STORE_COMMAND": RefreshDataStoreCommand
    }
    if command_type not in commands:
        return False, "NO HANDLER FOR COMMAND"

    command = commands[command_type](payload)
    command.apply()
    return True, command.to_dict()


def handle_replay(event_type, command_id, payload):
    events = {
        "ACCOUNT_CREATED": AccountCreatedEvent,
        "ACCOUNT_DELETED": AccountDeletedEvent,
        "ACCOUNT_CREDITED": AccountCreditedEvent,
        "ACCOUNT_DEBITED": AccountDebitedEvent,
        "ACCOUNT_DEPOSITED": AccountDepositedEvent,
        "ACCOUNT_WITHDRAWN": AccountWithdrawnEvent,
        "ACCOUNT_WITHDRAWAL_FAILED": AccountWithdrawalFailedEvent,
        "ACCOUNT_DEPOSIT_FAILED": AccountDepositFailedEvent,
        "ACCOUNT_CREDIT_FAILED": AccountCreditFailedEvent,
        "ACCOUNT_DEBIT_FAILED": AccountDebitFailedEvent
    }
    if event_type not in events:
        return False, "NO REPLAY FOR EVENT"

    event = events[event_type](command_id, payload)
    event.apply()
    return True, event
