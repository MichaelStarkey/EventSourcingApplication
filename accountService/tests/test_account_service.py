import json

from accountService.eventSourcing.aggregates import Account, db, Constants

# noinspection PyUnresolvedReferences
from accountService.factory import replay_events
from tests.util import client


def test_create_account_command(client):
    user_id = "TEST_ID"
    create_account_command = {
        "type": "CREATE",
        "payload": {
            "balance": 0,
            "user_id": user_id
        }
    }
    r = client.post('/accounts/commands/', data=json.dumps(create_account_command))
    account = Account.query.filter_by(user_id=user_id).first()
    assert account is not None, "New account not added"
    assert r.status == '202 ACCEPTED', "Response was not a 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_delete_account_command(client):
    account_id = "TEST_ID"
    new_account = Account(id=account_id, balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    delete_account_command = {
        "type": "DELETE",
        "payload": {
            "account_id": account_id
        }
    }
    r = client.post('/accounts/commands/', data=json.dumps(delete_account_command))
    accounts = Account.query.all()
    assert len(accounts) == 0, "Account was not deleted"
    assert r.status == '202 ACCEPTED', "Response not 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_debit_recorded_event(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    debit_recorded_event = {
        "type": "DEBIT_RECORDED",
        "command_id": "test",
        "payload": {
            "creditor_id": "account_id",
            "amount": 50,
            "transaction_id": "transaction_id"
        }
    }
    r = client.post('/accounts/events/', data=json.dumps(debit_recorded_event))
    account = Account.query.filter_by(id="account_id").first()
    assert account.balance == 150, "Account balance was not increased"
    assert r.status == '202 ACCEPTED', "Response not 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_transaction_created_event(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    debit_recorded_event = {
        "type": "TRANSACTION_CREATED",
        "command_id": "test",
        "payload": {
            "debtor_id": "account_id",
            "amount": 50,
            "transaction_id": "transaction_id"
        }
    }
    r = client.post('/accounts/events/', data=json.dumps(debit_recorded_event))
    account = Account.query.filter_by(id="account_id").first()
    assert account.balance == 50, "Account balance was not decreased."
    assert r.status == '202 ACCEPTED', "Response not 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_deposit_created_event(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    deposit_created_event = {
        "type": "DEPOSIT_CREATED",
        "command_id": "test",
        "payload": {
            "account_id": "account_id",
            "amount": 50,
            "transaction_id": "transaction_id"
        }
    }
    r = client.post('/accounts/events/', data=json.dumps(deposit_created_event))
    account = Account.query.filter_by(id="account_id").first()
    assert account.balance == 150, "Account balance was not increased."
    assert r.status == '202 ACCEPTED', "Response not 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_withdrawal_created_event(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    withdrawal_created_event = {
        "type": "WITHDRAWAL_CREATED",
        "command_id": "test",
        "payload": {
            "account_id": "account_id",
            "amount": 50,
            "transaction_id": "transaction_id"
        }
    }
    r = client.post('/accounts/events/', data=json.dumps(withdrawal_created_event))
    account = Account.query.filter_by(id="account_id").first()
    assert account.balance == 50, "Account balance was not decreased."
    assert r.status == '202 ACCEPTED', "Response not 202"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 1, "Aggregate version not updated"


def test_query_by_account_id(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    r = client.get('/accounts/account_id/')
    json_response = json.loads(r.get_data(as_text=True))
    assert json_response == {'id': 'account_id', 'user_id': 'Bob', 'balance': 100.0},\
        "Wrong return from account_id query"
    assert r.status == '200 OK', "Response not 200"


def test_query_by_user_id(client):
    new_account = Account(id="account_id", balance=100, user_id="Bob")
    db.session.add(new_account)
    new_account = Account(id="account_id_2", balance=100, user_id="Bob")
    db.session.add(new_account)
    db.session.commit()

    r = client.get('/accounts/users/Bob/')
    json_response = json.loads(r.get_data(as_text=True))
    assert json_response == [
        {'balance': 100.0, 'id': 'account_id', 'user_id': 'Bob'},
        {'balance': 100.0, 'id': 'account_id_2', 'user_id': 'Bob'}
    ], "Wrong return from user_id query"
    assert r.status == '200 OK', "Response not 200"


def test_replay_events(client):
    event1 = dict([
        ("type", "ACCOUNT_CREATED"),
        ("command_id", "test"),
        ("payload", dict([
            ("account_id", "test"),
            ("balance", 0),
            ("user_id", "test")
        ]))
    ])
    event2 = dict([
        ("type", "ACCOUNT_CREDITED"),
        ("command_id", "test"),
        ("payload", dict([
            ("creditor_id", "test"),
            ("amount", 50)
        ]))
    ])
    mock_replay_query = [event1, event2]
    replay_events(mock_replay_query)
    account = Account.query.filter_by(id="test").first()
    assert account is not None, "Account not created properly"
    assert account.balance == 50, "Account balance not credited properly"

    version = Constants.query.filter_by(key='accounts_version').first()
    assert version.value == 2, "Account not versioned correctly"
