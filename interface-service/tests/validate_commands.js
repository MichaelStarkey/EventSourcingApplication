var ms = require('mockserver-client')
var msc = ms.mockServerClient

msc("localhost", 1080).retrieveRecordedRequests({
    "path": "/accounts/commands.*",
    "method": "POST"
}).then(
    function (recordedRequests) {
        console.log(JSON.stringify(recordedRequests))
    },
    function (error) {
        console.log(error)
    }
)
