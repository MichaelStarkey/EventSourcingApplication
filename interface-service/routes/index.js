var express = require('express')
var router = express.Router()
var requests = require('../util/requests')

// Define routes
router.get('/',
  ensureAuthenticated,
  async function(req, res) {
    var query = await requests.queryService(uris['accountService'] + 'accounts/users/' + req.user.id + '/')
    var err = query[0]
    var data = query[1]
    var r = query[2]
    if (err || !data || !r) {
      res.render(
        'userError',
        {
          title: "Error",
          user: req.user,
          message: {title: "Error", message: "Connection Error."}
        }
      )
    } else if (r.statusCode == 404) {
      res.render(
        'userError',
        {
          title: "Error",
          user: req.user,
          message: {title: "Error", message: "No account found."}
        }
      )
    } else {
      res.render('index', {title: 'Index', user: req.user, accounts: data})
    }
  }
)

// router.get('/delete/:id', ,
// ensureAuthenticated,
//  {
//   //TODO: add functionality for delete
//   res.redirect('/')
// })

router.get('/delete/:id',
  ensureAuthenticated,
  function(req, res) {
    var url = uris['accountService'] + 'accounts/commands/'
    var command = {
      "type": "DELETE",
      "payload": {
        "account_id": req.params.id
      }
    }
    requests.postCommand(url, command)
    var backURL=req.header('Referer') || '/'
    res.redirect(backURL)
  }
)

router.get('/account/:id',
  ensureAuthenticated,
  async function(req, res) {
    var acc_query = await requests.queryService(uris['accountService'] + 'accounts/' + req.params.id + '/')
    var acc_err = acc_query[0]
    var acc_data = acc_query[1]

    var tra_query = await requests.queryService(uris['transactionService'] + 'transactions/users/' + req.params.id + '/')
    var tra_err = tra_query[0]
    var tra_data = tra_query[1]

    var valid_acc = !(acc_err || !acc_data)
    var valid_tra = !(tra_err || !tra_data)
    if (valid_acc && valid_tra) {
      res.render('account', {title: 'Account', user: req.user, account: acc_data, transactions: tra_data})
    } else {
      res.render(
        'userError',
        {
          title: "Error",
          user: req.user,
          message: {title: "Error", message: "Account information not found."}
        }
      )
    }
})

router.post('/createBranch',
  ensureAuthenticated,
  function(req, res, next) {
    var branch_name = req.body.branch
    var time = req.body.time

    req.checkBody('branch', 'Branch name is required').notEmpty()
    req.checkBody('time', 'Split time is required').notEmpty()

    var errors = req.validationErrors()
    console.log('form erros: ', errors)

    if (errors){
      res.redirect('/admin')
    } else {
      time = Date.parse(time)
      var url = uris['kafkaInterface'] + 'branch/' + branch_name + '/' + time
      requests.putCommand(url)
      res.redirect('/admin')
    }
  }
)

router.post('/checkoutBranch',
  ensureAuthenticated,
  async function(req, res, next) {
    var branch_name = req.body.branch

    req.checkBody('branch', 'Branch name is required').notEmpty()

    var errors = req.validationErrors()
    console.log('form erros: ', errors)

    if (errors){
      res.redirect('/admin')
    } else {
      var url = uris['kafkaInterface'] + 'checkout/' + branch_name
      var resp = await requests.putCommand(url)
      if (resp.statusCode == 202) {
        var accountsUrl = uris['accountService'] + 'accounts/commands/'
        var accountsCommand = {
          "type": "REFRESH_DATA_STORE_COMMAND",
          "payload": {
            "reason": "checkout"
          }
        }
        var transactionsUrl = uris['transactionService'] + 'transactions/commands/'
        var transactionsCommand = {
          "type": "REFRESH_DATA_STORE_COMMAND",
          "payload": {
            "reason": "checkout"
          }
        }
        requests.postCommand(accountsUrl, accountsCommand)
        requests.postCommand(transactionsUrl, transactionsCommand)
      }
      res.redirect('/admin')
    }
  }
)

router.post('/createTransaction',
  ensureAuthenticated,
  function(req, res, next) {
    var debtor = req.body.debtor
    var creditor = req.body.creditor
    var amount = req.body.amount

    req.checkBody('debtor', 'Debtor is required').notEmpty()
    req.checkBody('creditor', 'Creditor is required').notEmpty()
    req.checkBody('amount', 'Amount is required').notEmpty()

    var errors = req.validationErrors()
    console.log('form errors: ', errors)

    if (errors){
      res.redirect('/transaction')
    } else {
      var url = uris['transactionService'] + 'transactions/commands/'
      var command = {
        "type": "CREATE",
        "payload": {
          "amount": parseInt(amount),
          "creditor_id": creditor,
          "debtor_id": debtor
        }
      }
      requests.postCommand(url, command)
      res.redirect('/')
    }
  }
)

router.post('/createWithdrawal',
  ensureAuthenticated,
  function(req, res, next) {
    var account = req.body.account
    var amount = req.body.amount

    req.checkBody('account', 'Account ID is required').notEmpty()
    req.checkBody('amount', 'Amount is required').notEmpty()

    var errors = req.validationErrors()
    console.log('form errors: ', errors)

    if (errors){
      res.redirect('/withdrawal')
    } else {
      var url = uris['transactionService'] + 'transactions/commands/'
      var command = {
        "type": "CREATE_WITHDRAWAL",
        "payload": {
          "amount": parseInt(amount),
          "account_id": account
        }
      }
      requests.postCommand(url, command)
      res.redirect('/')
    }
  }
)

router.post('/createDeposit',
  ensureAuthenticated,
  function(req, res, next) {
    var account = req.body.account
    var amount = req.body.amount

    req.checkBody('account', 'Account ID is required').notEmpty()
    req.checkBody('amount', 'Amount is required').notEmpty()

    var errors = req.validationErrors()
    console.log('form errors: ', errors)

    if (errors){
      res.redirect('/deposit')
    } else {
      var url = uris['transactionService'] + 'transactions/commands/'
      var command = {
        "type": "CREATE_DEPOSIT",
        "payload": {
          "amount": parseInt(amount),
          "account_id": account
        }
      }
      requests.postCommand(url, command)
      res.redirect('/')
    }
  }
)

router.get('/createAccount',
  ensureAuthenticated,
  function(req, res) {
    var url = uris['accountService'] + 'accounts/commands/'
    var command = {
      "type": "CREATE",
      "payload": {
        "balance": 0,
        "user_id": req.user.id
      }
    }
    requests.postCommand(url, command)
    var backURL=req.header('Referer') || '/'
    res.redirect(backURL)
  }
)

router.get('/transaction', ensureAuthenticated, async function(req, res) {
  var query = await requests.queryService(uris['accountService'] + 'accounts/users/' + req.user.id + '/')
  var err = query[0]
  var data = query[1]
  if (err || !data) {
    res.render(
      'userError',
      {
        title: "Error",
        user: req.user,
        message: {title: "Error", message: "Account information not found."}
      }
    )
  } else {
    res.render('transaction', { title: "Transaction", accounts: data, user: req.user})
  }
})

router.get('/withdrawal', ensureAuthenticated, async function(req, res) {
  var query = await requests.queryService(uris['accountService'] + 'accounts/users/' + req.user.id + '/')
  var err = query[0]
  var data = query[1]
  if (err || !data) {
    res.render(
      'userError',
      {
        title: "Error",
        user: req.user,
        message: {title: "Error", message: "Account information not found."}
      }
    )
  } else {
    res.render('withdrawal', { title: "Withdrawal", accounts: data, user: req.user})
  }
})

router.get('/deposit', ensureAuthenticated, function(req, res) {
  res.render('deposit', {title: "Deposit", user: req.user})
})

router.get('/admin', adminOnly, async function(req, res) {
  var query = await requests.queryService(uris['kafkaInterface'] + 'queries/branches')
  var err = query[0]
  var data = query[1]
  if (err || !data) {
    res.render(
      'userError',
      {
        title: "Error",
        user: req.user,
        message: {title: "Branch query Error", message: "Branch information not found."}
      }
    )
  } else {
    res.render('admin', { title: "Admin Interface", branches: data, user: req.user})
  }
})

function ensureAuthenticated(req,res,next) {
  if (req.isAuthenticated()) {
    return next()
  } else {
    res.redirect('/users/login')
  }
}

function adminOnly(req,res,next) {
  if (req.isAuthenticated() && req.user.admin == 1) {
    return next()
  } else {
    var err = new Error('Not Found')
    err.status = 404
    next(err)
  }
}

module.exports = router;
