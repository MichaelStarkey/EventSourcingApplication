var User = require('./user')
const sqlite3 = require('sqlite3').verbose()

function addAdmin(){
  var db = new sqlite3.Database('sqlite3.db')
  var salt = User.makeSalt(5)
  var hash = User.hashPassword("admin", salt)
  db.run(
    `REPLACE INTO users(email, first, last, password, salt, admin) VALUES(?,?,?,?,?,?)`,
    ["admin@admin.com", "mr", "admin", hash, salt, 1],
    (err) => {
      if (err) {
        console.log(err)
      }
    }
  )
  db.close()
}

addAdmin()
