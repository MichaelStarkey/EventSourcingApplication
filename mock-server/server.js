var mockserver = require('mockserver-node');
mockserver.start_mockserver({serverPort: 1080});
mockserver.stop_mockserver();
