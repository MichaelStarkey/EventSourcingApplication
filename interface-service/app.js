const express = require('express')
const path = require('path')
const favicon = require('serve-favicon')
const logger = require('morgan')
const cookieParser = require('cookie-parser')
const bodyParser = require('body-parser')
const expressValidator = require('express-validator')
const session = require('express-session')
const flash = require('express-flash')
const passport = require('passport')
const LocalStrategy = require('passport-local').Strategy
const sqlite3 = require('sqlite3').verbose()

var User = require('./models/user')
var index = require("./routes/index")
var users = require("./routes/users")

process.argv.forEach(function (val, index, array) {
  console.log(index + ': ' + val);
});

uris = {
  'accountService': 'http://127.0.0.1:5000/',
  'transactionService': 'http://127.0.0.1:3030/',
  'kafkaInterface': 'http://127.0.0.1:8080/'
}

if (process.argv[2] === 'test') {
  uris = {
    'accountService': 'http://127.0.0.1:1080/',
    'transactionService': 'http://127.0.0.1:1080/',
    'kafkaInterface': 'http://127.0.0.1:1080/'
  }
}

function initDB(){
  var db = new sqlite3.Database('sqlite3.db')
  var createTable = "CREATE TABLE IF NOT EXISTS users (" +
    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
    "email TEXT NOT NULL, " +
    "first VARCHAR NOT NULL, " +
    "last VARCHAR NOT NULL, " +
    "password TEXT NOT NULL, " +
    "salt TEXT NOT NULL," +
    "admin INTEGER DEFAULT 0)"
  db.run(createTable)
  db.close()
}
// Init App
initDB()
var app = express()

// view engine setup
app.set('views', path.join(__dirname, 'views'))
app.set('view engine', 'pug')

// favicon + logger middleware
app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')))
app.use(logger('dev'))

// Parser Middleware
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))
app.use(cookieParser())

// Set Static Folder
app.use(express.static(path.join(__dirname, 'public')))


// Express Session
app.use(session({
  secret: 'keyboard-cat',
  resave: false,
  saveUninitialized: true
}))

// Passport init
app.use(passport.initialize())
app.use(passport.session())


// Validator
app.use(expressValidator({
  errorFormatter: function(param, msg, value) {
      var namespace = param.split('.')
      , root = namespace.shift()
      , formParam = root

    while(namespace.length) {
      formParam += '[' + namespace.shift() + ']';
    }
    return {
      param : formParam,
      msg   : msg,
      value : value
    }
  }
}))

// Express Flash
app.use(flash())

app.use('/', index)
app.use('/users', users)

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found')
  err.status = 404
  next(err)
})

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message
  res.locals.error = req.app.get('env') === 'development' ? err : {}

  // render the error page
  res.status(err.status || 500)
  res.render('error')
})

module.exports = app
