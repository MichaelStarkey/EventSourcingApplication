from flask import request, Blueprint, jsonify
from flask_api import status
from accountService.eventSourcing.aggregates import Account, Constants
from accountService.eventSourcing.util import handle_command, handle_event

bp = Blueprint('accountService', __name__)


@bp.route('/accounts/events/', methods=['POST'])
def event_handler():
    content = request.get_json(force=True)
    event_type, command_id, payload = content['type'], content['command_id'], content['payload']
    success, message = handle_event(event_type, command_id, payload)
    message = jsonify(message)
    if success:
        return message, status.HTTP_202_ACCEPTED
    return message, status.HTTP_400_BAD_REQUEST


@bp.route('/accounts/commands/', methods=['POST'])
def command_handler():
    content = request.get_json(force=True)
    command_type, payload = content['type'], content['payload']

    success, message = handle_command(command_type, payload)
    message = jsonify(message)
    if success:
        return message, status.HTTP_202_ACCEPTED
    return message, status.HTTP_400_BAD_REQUEST


@bp.route('/accounts/users/<user_id>/', methods=['GET'])
def user_query_handler(user_id):
    accounts = Account.query.filter_by(user_id=user_id).all()
    if accounts:
        for i in range(0, len(accounts)):
            accounts[i] = accounts[i].to_dict()
        return jsonify(accounts), status.HTTP_200_OK
    response = {
        "status": 404,
        "reason": "Query not found"
    }
    return jsonify(response), status.HTTP_404_NOT_FOUND


@bp.route('/accounts/<account_id>/', methods=['GET'])
def account_query_handler(account_id):
    account = Account.query.filter_by(id=account_id).first()
    if account:
        return jsonify(account.to_dict()), status.HTTP_200_OK
    response = {
        "status": 404,
        "reason": "Query not found"
    }
    return jsonify(response), status.HTTP_404_NOT_FOUND


@bp.route('/dbTest/', methods=['GET'])
def db_test():
    accounts = Account.query.all()
    return str(accounts)


@bp.route('/', methods=['GET'])
def index():
    version = Constants.query.filter_by(key='accounts_version').first()
    return str(version)
