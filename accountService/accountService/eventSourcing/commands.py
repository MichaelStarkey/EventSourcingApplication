from uuid import uuid4

from accountService.eventSourcing.events import AccountCreatedEvent, AccountDeletedEvent


class Command:
    def __init__(self, command_type, payload):
        self.type = command_type
        self.payload = payload
        self.command_id = str(uuid4())

    def to_dict(self):
        return dict([
            ('type', self.type),
            ('command_id', self.command_id),
            ('payload', self.payload)
        ])


class CreateAccountCommand(Command):
    def __init__(self, payload):
        super().__init__("CREATE", payload)

    def __str__(self):
        return super().__str__()

    def apply(self):
        event = AccountCreatedEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()


class DeleteAccountCommand(Command):
    def __init__(self, payload):
        super().__init__("DELETE", payload)

    def apply(self):
        event = AccountDeletedEvent(self.command_id, self.payload)
        event.apply()
        event.yield_event()


class RefreshDataStoreCommand(Command):
    def __init__(self, payload):
        super().__init__("REFRESH", payload)

    def apply(self):
        from accountService.eventSourcing.aggregates import db, Constants
        db.drop_all()
        db.create_all()
        version = Constants(key="accounts_version", value=0)
        db.session.add(version)
        db.session.commit()

        from accountService.factory import sync_events
        from flask import current_app
        sync_events(current_app, 0)
