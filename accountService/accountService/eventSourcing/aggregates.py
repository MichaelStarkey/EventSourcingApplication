import requests
import sys
from flask import current_app
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()


class Account(db.Model):
    __tablename__ = 'accounts'

    id = db.Column(db.String(36), primary_key=True)
    balance = db.Column(db.Float)
    user_id = db.Column(db.String(36))

    def __repr__(self):
        return str({
            'id': self.id,
            'balance': self.balance,
            'user_id': self.user_id
        })

    def to_dict(self):
        return {
            'id': self.id,
            'balance': self.balance,
            'user_id': self.user_id
        }


class Constants(db.Model):
    __tablename__ = 'constants'

    key = db.Column(db.String(10), primary_key=True)
    value = db.Column(db.Integer)

    def __repr__(self):
        return str({
            'key': self.key,
            'value': self.value
        })

    def __str__(self):
        return 'Value of {} is currently: {}'.format(self.key, self.value)


def take_snapshot(version):
    accounts = []

    all_accounts = Account.query.all()
    for account in all_accounts:
        accounts.append(account.to_dict())

    document = {
        'version': version,
        'aggregate': 'accounts',
        'snapshot': accounts
    }

    endpoint = current_app.config['EVENT_STORE_ENDPOINT'] + current_app.config['EVENT_STORE_SNAPSHOT_ENDPOINT']

    if current_app.config['MAKE_OUTBOUND_REQUESTS']:
        r = requests.post(endpoint, data=document)
        print("Snapshot saved, " + str(r.status_code), file=sys.stdout)
