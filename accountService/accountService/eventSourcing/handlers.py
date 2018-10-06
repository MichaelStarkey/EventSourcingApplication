from accountService.eventSourcing.aggregates import Account
from accountService.eventSourcing.events import AccountCreditedEvent, AccountDebitedEvent, AccountDebitFailedEvent, \
    AccountCreditFailedEvent, AccountWithdrawalFailedEvent, AccountWithdrawnEvent, AccountDepositedEvent, \
    AccountDepositFailedEvent


class EventHandler:
    def __init__(self, event_type, command_id, payload):
        self.type = event_type
        self.command_id = command_id
        self.payload = payload

    def to_dict(self):
        return dict([
            ('type', self.type),
            ('payload', self.payload)
        ])


class DebitRecordedEventHandler(EventHandler):
    def __init__(self, command_id, payload):
        super().__init__("DEBIT_RECORDED", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['creditor_id']).first()
        event = AccountCreditFailedEvent(self.command_id, self.payload)
        if account:
            event = AccountCreditedEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()


class TransactionCreatedEventHandler(EventHandler):
    def __init__(self, command_id, payload):
        super().__init__("TRANSACTION_CREATED", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['debtor_id']).first()
        event = AccountDebitFailedEvent(self.command_id, self.payload)
        if account:
            if account.balance >= self.payload['amount']:
            	event = AccountDebitedEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()


class WithdrawalCreatedEventHandler(EventHandler):
    def __init__(self, command_id, payload):
        super().__init__("WITHDRAWAL_CREATED", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['account_id']).first()
        event = AccountWithdrawalFailedEvent(self.command_id, self.payload)
        if account:
            if account.balance >= self.payload['amount']:
                event = AccountWithdrawnEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()


class DepositCreatedEventHandler(EventHandler):
    def __init__(self, command_id, payload):
        super().__init__("DEPOSIT_CREATED", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['account_id']).first()
        event = AccountDepositFailedEvent(self.command_id, self.payload)
        if account:
            event = AccountDepositedEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()
