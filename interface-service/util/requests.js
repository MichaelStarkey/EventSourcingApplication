var request = require('request');

queryService = function(uri) {
  return new Promise(resolve => {request.get(
    {
      url: uri,
      json: true,
      headers: {'User-Agent': 'request'}
    }, (err, r, data) => {
      if (err) {
        console.log('Request error: ', err)
        resolve([err, null, r])
      } else {
        console.log('Data: ', data)
        resolve([null, data, r])
      }
    }
  )})
}

postCommand = function(uri, command) {
  var options = {
    uri: uri,
    method: 'POST',
    json: command
  }
  request(options, function(error, response, body) {
    if (!error && response.statusCode == 202) {
      console.log("Command accepted")
    } else { console.log(error) }
  })
}

// putCommand = function(uri) {
//   var options = {
//     uri: uri,
//     method: 'PUT'
//   }
//   request(options, function(error, response, body) {
//     if (!error && response.statusCode == 202) {
//       console.log("Data accepted")
//     } else { console.log(error) }
//   })
// }

putCommand = function(uri) {
  var options = {
    uri: uri,
    method: 'PUT'
  }
  return new Promise(resolve => {request(options, (err, r, data) => {
      if (err) {
        console.log('Request error: ', err)
        resolve(r)
      } else {
        console.log('Data: ', data)
        resolve(r)
      }
    }
  )})
}

module.exports.queryService = queryService
module.exports.postCommand = postCommand
module.exports.putCommand = putCommand
