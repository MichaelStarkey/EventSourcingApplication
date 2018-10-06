from flask import Flask
import sys

def create_app(config=None):
    import os
    dir_path = os.path.dirname(os.path.realpath(__file__))
    dbpath = dir_path + '/read.db'

    app = Flask('accountService')
    print("dbpath: ", dbpath, file=sys.stdout)
    app.config.update(dict(
        SQLALCHEMY_DATABASE_URI='sqlite:////' + dbpath,
        SQLALCHEMY_TRACK_MODIFICATIONS=False,
    ))
    app.config.update(get_configs())
    app.config.update(config or {})
    app.config.update({"UNSUBSCRIBE_LOCATION": "asdsadasd"})
    from accountService.eventSourcing.aggregates import db
    db.init_app(app)

    from accountService.blueprints.accountService import bp
    app.register_blueprint(bp)

    app.app_context().push()
    init_consumer(app)
    init_db(app, dbpath)
    init_events(app)
    return app


def get_configs():
    from configparser import ConfigParser
    config = ConfigParser()
    config.read("config.ini")

    new_configs = {}
    for section in config.sections():
        for option in config.options(section):
            new_configs[option.upper()] = config.get(section, option)
            try:
                new_configs[option.upper()] = int(config.get(section, option))
            except ValueError:
                new_configs[option.upper()] = config.get(section, option)
            if new_configs[option.upper()] == "True" or new_configs[option.upper()] == "False":
                new_configs[option.upper()] = config.getboolean(section, option)
    return new_configs


def init_consumer(app):
    if app.config['MAKE_OUTBOUND_REQUESTS'] is True and app.config['TESTING'] is False:
        import requests
        print(" * Creating a kafka consumer...", file=sys.stdout)
        subscribe_endpoint = app.config['SUBSCRIBE_ENDPOINT']
        r = requests.put(subscribe_endpoint, params={'topic': "transactions", "endpoint": "http://127.0.0.1:5000/accounts/events/"})
        location = r.headers['location']
        app.config.update({"UNSUBSCRIBE_LOCATION": location})


def init_db(app, dbpath):
    from pathlib import Path
    read_db = Path(dbpath)
    print(" * Checking for read.db: ", read_db.is_file(), file=sys.stdout)
    if not read_db.is_file() and app.config['TESTING'] is False:
        print(" * Read store not found, creating one...")
        from accountService.eventSourcing.aggregates import db, Constants
        db.create_all()
        version = Constants(key="accounts_version", value=0)
        db.session.add(version)
        db.session.commit()


def init_events(app):
    if app.config['TESTING'] is False:
        from accountService.eventSourcing.aggregates import Constants
        version = Constants.query.filter_by(key='accounts_version').first().value
        sync_events(app, version)


def replay_events(events):
    from accountService.eventSourcing.util import handle_replay

    for event in events:
        event_type, command_id, event_payload = event['type'], event['command_id'], event['payload']
        handle_replay(event_type, command_id, event_payload)


def apply_snapshot(snapshot_response):
    snapshot = snapshot_response['snapshot']
    version = snapshot_response['version']

    from accountService.eventSourcing.aggregates import Account, Constants, db
    for account in snapshot:
        new_account = Account(id=account['id'], balance=account['balance'], user_id=account['user_id'])
        db.session.add(new_account)
    service_version = Constants.query.filter_by(key="accounts_version").first()
    service_version.value = version
    db.session.commit()


def sync_events(app, version):
    if app.config['MAKE_OUTBOUND_REQUESTS']:
        import requests
        if app.config['USE_MONGO_HARD_CODE']:
            events_endpoint = 'http://127.0.0.1:8080/queries/test'
        else:
            events_endpoint = app.config['EVENT_STORE_ENDPOINT'] + app.config['EVENT_STORE_REPLAY_ENDPOINT']
        snapshot_endpoint = app.config['EVENT_STORE_ENDPOINT'] + app.config['EVENT_STORE_SNAPSHOT_ENDPOINT']
        print(events_endpoint, "    ", snapshot_endpoint, file=sys.stdout)
        print(" * Fetching most recent snapshot...", file=sys.stdout)
        r = requests.get(snapshot_endpoint)
        if r.status_code == 202:
            if r.content:
                snapshot_response = r.json()
                service_version = Constants.query.filter_by(key="accounts_version").first().value
                if snapshot_response['version'] > service_version:
                    apply_snapshot(snapshot_response)

        print(" * Fetching events from event store...", file=sys.stdout)
        r = requests.get(events_endpoint, params={'from': version})
        if r.status_code == 202:
            replay_events(r.json())
    else:
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
