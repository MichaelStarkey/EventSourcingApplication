var sqlite3 = require('sqlite3').verbose()
var crypto = require('crypto')

hashPassword = function(password, salt) {
  var hash = crypto.createHash('sha256');
  hash.update(password);
  hash.update(salt);
  return hash.digest('hex');
}

makeSalt = function(length) {
  var text = ""
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

  for (var i = 0; i < length; i++)
    text += possible.charAt(Math.floor(Math.random() * possible.length))

  return text
}

module.exports.createUser = (newUser, callback) => {
  var db = new sqlite3.Database('sqlite3.db')
  var salt = makeSalt(5)
  var hash = hashPassword(newUser.password, salt)
  db.run(
    `INSERT INTO users(email, first, last, password, salt) VALUES(?,?,?,?,?)`,
    [newUser.email, newUser.firstName, newUser.lastName, hash, salt],
    (err) => {
      callback(err, "")
    }
  )
  db.close()
}

module.exports.hashPassword = hashPassword
module.exports.makeSalt = makeSalt
