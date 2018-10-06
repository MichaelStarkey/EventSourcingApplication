var mockserverc = require('mockserver-client');
var mockServerClient = mockserverc.mockServerClient;

mockServerClient("localhost", 1080).mockAnyResponse({
    "httpRequest": {
        "method": "GET",
        "path": "/accounts/users/.*"
    },
    "httpResponse": {
        "statusCode": 302,
        "body": '[' +
        '{"balance": 100.0, "id": "111111", "user_id": "Bob"}, ' +
        '{"balance": 0.0, "id": "222222", "user_id": "Bob"}' +
        ']'
    },
    "times": {
      "unlimited": true
    }
}).then(
  function () {
    console.log("/accounts/users.* expectation created");
    mockServerClient("localhost", 1080).mockAnyResponse({
        "httpRequest": {
            "method": "GET",
            "path": "/accounts/.*"
        },
        "httpResponse": {
            "statusCode": 302,
            "body": '{"balance": 100.0, "id": "111111", "user_id": "Bob"}'
        },
        "times": {
          "unlimited": true
        }
    }).then(
      function () {
        console.log("/accounts/.* expectation created");
      },
      function (error) {
        console.log(error);
      }
    );
  },
  function (error) {
    console.log(error);
  }
);

mockServerClient("localhost", 1080).mockAnyResponse({
    "httpRequest": {
        "method": "GET",
        "path": "/transactions/users/.*"
    },
    "httpResponse": {
        "statusCode": 302,
        "body": '[' +
        '{"amount": 34, "id": "tratest", "creditor": "111111", "debtor": "333333", "status": "COMPLETE"}, ' +
        '{"amount": 67, "id": "tratest2", "creditor": "222222", "debtor": "111111", "status": "FAILED"},' +
        '{"amount": 90, "id": "tratest2", "creditor": "111111", "debtor": "333333", "status": "ANYTHING"}' +
        ']'
    },
    "times": {
      "unlimited": true
    }
}).then(
  function () {
    console.log("/transactions/accounts/.* expectation created");
  },
  function (error) {
    console.log(error);
  }
);

mockServerClient("localhost", 1080).mockAnyResponse({
    "httpRequest": {
        "method": "GET",
        "path": "/queries/branches"
    },
    "httpResponse": {
        "statusCode": 302,
        "body": '{"current": "master", "branches": ["master", "branch1"]}'
    },
    "times": {
      "unlimited": true
    }
}).then(
  function () {
    console.log("/queries/branches expectation created");
  },
  function (error) {
    console.log(error);
  }
);

mockServerClient("localhost", 1080).mockAnyResponse({
    "httpRequest": {
        "method": "POST",
        "path": "/accounts/commands.*"
    },
    "httpResponse": {
        "statusCode": 202,
        "body": 'Success!'
    },
    "times": {
      "unlimited": true
    }
}).then(
  function () {
    console.log("/accounts/commands.* expectation created");
  },
  function (error) {
    console.log(error);
  }
);

mockServerClient("localhost", 1080).mockAnyResponse({
    "httpRequest": {
        "method": "POST",
        "path": "/transactions/commands.*"
    },
    "httpResponse": {
        "statusCode": 202,
        "body": 'Success!'
    },
    "times": {
      "unlimited": true
    }
}).then(
  function () {
    console.log("/transactions/commands.* expectation created");
  },
  function (error) {
    console.log(error);
  }
);
