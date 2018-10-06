import requests

from accountService.factory import create_app
import signal
import sys
def signal_handler(signal, frame):
    if app.config['MAKE_OUTBOUND_REQUESTS']:
        print(" * Unsubscribe from kafka...", file=sys.stdout)
        requests.delete(app.config['UNSUBSCRIBE_LOCATION'])
    sys.exit(0)


signal.signal(signal.SIGTERM, signal_handler)

app = create_app()
