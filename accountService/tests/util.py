import pytest

from accountService.factory import create_app


@pytest.fixture
def client():
    config = dict(TESTING=True, SQLALCHEMY_DATABASE_URI='sqlite://')
    app = create_app(config=config)
    with app.app_context():
        from accountService.eventSourcing.aggregates import db, Constants
        db.create_all()
        app.testing = True

        version = Constants(key="accounts_version", value=0)
        db.session.add(version)
        db.session.commit()
        yield app.test_client()
