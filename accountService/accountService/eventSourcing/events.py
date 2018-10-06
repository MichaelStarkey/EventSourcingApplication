from uuid import uuid4

import sys
import requests
from flask import current_app

from accountService.eventSourcing.aggregates import Account, db, Constants, take_snapshot


class Event:
    def __init__(self, event_type, command_id, payload):
        self.event_type = event_type
        self.payload = payload
        self.command_id = command_id
        self.version = -1

    def update_accounts_version(self):
        version = Constants.query.filter_by(key='accounts_version').first()
        version.value = version.value + 1
        db.session.commit()
        self.version = version.value
        snapshot_window = current_app.config['SNAPSHOT_WINDOW']
        if version.value % snapshot_window == 0:
            take_snapshot(version.value)

    def to_dict(self):
        return dict([
            ('aggregate', 'accounts'),
            ('type', self.event_type),
            ('version', self.version),
            ('command_id', self.command_id),
            ('payload', self.payload)
        ])

    def yield_event(self):
        if current_app.config['MAKE_OUTBOUND_REQUESTS'] and current_app.config['TESTING'] is False:
            json = self.to_dict()
            endpoint = current_app.config['PUBLISH_ENDPOINT']
            r = requests.post(endpoint, json=json, headers={'content-type': 'application/json'})
            print("Posted event: {}".format(r.status_code), file=sys.stdout)


class AccountCreatedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_CREATED", command_id, payload)

    def apply(self):
        account_id = str(uuid4())
        if 'account_id' in self.payload:
            account_id = self.payload['account_id']
        new_account = Account(id=account_id, balance=self.payload['balance'], user_id=self.payload['user_id'])
        db.session.add(new_account)
        self.payload['account_id'] = new_account.id
        self.update_accounts_version()


class AccountCreditedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_CREDITED", command_id, payload)

    def apply(self):
        to_acc = Account.query.filter_by(id=self.payload['creditor_id']).first()
        to_acc.balance = to_acc.balance + self.payload['amount']
        self.update_accounts_version()


class AccountDebitedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_DEBITED", command_id, payload)

    def apply(self):
        from_acc = Account.query.filter_by(id=self.payload['debtor_id']).first()
        from_acc.balance = from_acc.balance - self.payload['amount']
        self.update_accounts_version()


class AccountDeletedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_DELETED", command_id, payload)

    def apply(self):
        Account.query.filter_by(id=self.payload['account_id']).delete()
        self.update_accounts_version()


class AccountCreditFailedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_CREDIT_FAILED", command_id, payload)

    def apply(self):
        from_acc = Account.query.filter_by(id=self.payload['creditor_id']).first()
        from_acc.balance = from_acc.balance + self.payload['amount']
        self.update_accounts_version()


class AccountDebitFailedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_DEBIT_FAILED", command_id, payload)

    def apply(self):
        self.update_accounts_version()


class AccountWithdrawnEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_WITHDRAWN", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['account_id']).first()
        account.balance = account.balance - self.payload['amount']
        self.update_accounts_version()


class AccountWithdrawalFailedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_WITHDRAWAL_FAILED", command_id, payload)

    def apply(self):
        self.update_accounts_version()


class AccountDepositedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_DEPOSITED", command_id, payload)

    def apply(self):
        account = Account.query.filter_by(id=self.payload['account_id']).first()
        account.balance = account.balance + self.payload['amount']
        self.update_accounts_version()


class AccountDepositFailedEvent(Event):
    def __init__(self, command_id, payload):
        super().__init__("ACCOUNT_DEPOSITED_FAILED", command_id, payload)

    def apply(self):
        self.update_accounts_version()
