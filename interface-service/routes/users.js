var express = require('express');
var router = express.Router();
var User = require('../models/user')
var sqlite3 = require('sqlite3').verbose()
const passport = require('passport')
const LocalStrategy = require('passport-local').Strategy

// GET Register page
router.get('/register', function(req, res, next) {
  res.render('register', { title: 'Register'});
});

// POST: Register a user
router.post('/register',function(req, res, next) {

  var email = req.body.email
  var firstName = req.body.fName
  var lastName = req.body.lName
  var password = req.body.password
  var password2 = req.body.password2

  // Validation
  req.checkBody('email', 'Email is required').isEmail()
  req.checkBody('fName', 'First Name is required').notEmpty()
  req.checkBody('lName', 'Last Name is required').notEmpty()
  req.checkBody('password', 'Password is required').notEmpty()
  req.checkBody('password2', 'Passwords must match').equals(req.body.password)

  var errors = req.validationErrors()

  if (errors){
    res.render('register',{
      title: 'Register',
      errors: errors
    })
  } else {
    var newUser = {
      'email': email,
      'password': password,
      'firstName': firstName,
      'lastName': lastName
    }

    User.createUser(newUser, (err, user) => {
      if (err) throw err
    })

    res.redirect('/users/login')
  }
});

// GET Login Page
router.get('/login', function(req, res, next) {
  res.render('login', { title : 'Login'})
})

passport.use(new LocalStrategy({
  usernameField: 'email',
  passwordField: 'password'
},
function(email, password, done) {
  var db = new sqlite3.Database('sqlite3.db')
  db.get('SELECT * FROM users WHERE email = ?', email, function(err, user) {
    if (!user) return done(null, false, {message: 'Incorrect email.'})
    var hash = User.hashPassword(password, user.salt)
    if (hash != user.password) return done(null, false)
    return done(null, user)
  })
  db.close()
}))

passport.serializeUser(function(user, done) {
done(null, user.id);
});

passport.deserializeUser(function(id, done) {
  var db = new sqlite3.Database('sqlite3.db')
  db.get('SELECT id, email, admin FROM users WHERE id = ?', id, function(err, row) {
    if (!row) return done(null, false)
    return done(null, row);
  })
});

// POST: Attempt to Login
router.post('/login',
  passport.authenticate("local", {
    failureRedirect:'/users/login',
    successRedirect: '/',
    failureFlash: true
  }));

router.get('/logout', (req,res) => {
  req.logout()

  res.redirect('/users/login')
})

module.exports = router;
