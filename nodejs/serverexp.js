var express = require('express');
var stream = require('express-stream');
var cors = require('cors');

var app = express();
app.use(cors());

var cassandra = require('cassandra-driver');
var client = new cassandra.Client({contactPoints: ['172.18.161.100', '172.18.161.101']});

// This responds with "Hello World" on the homepage
app.get('/', function (req, res) {
   console.log("Got a GET request for the homepage");
   res.send('Hello GET');
})

// This responds a POST request for the homepage
app.post('/', function (req, res) {
   console.log("Got a POST request for the homepage");
   res.send('Hello POST');
})

app.get('/schoolids', function (req, res) {
  var query = "SELECT sid FROM schoolsinfo.schools";
  client.execute(query, function(err, result) {
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify(result.rows));
  })
})

app.get('/schooluuids/:sid', function (req, res) {
  var sid = req.params.sid;
  var query = "SELECT uuid FROM schoolsinfo.sidsuuids where sid=?";
  client.execute(query, [sid], function(err, result) {
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify(result.rows));
  })
})

app.get('/contextdata/:sid', function(req, res) {
  var sid = req.params.sid;
  var uuid = req.query.uuid;
  var query = "SELECT * FROM " + sid + ".context_date WHERE uuid=?";
  client.execute(query, [uuid], function(err, result) {
    res.setHeader('Content-Type', 'application/json');
    console.log(result.rows.length);
    /*for (i = 0; i < result.rows.length; i++ ) {
        if (result.rows[i].situation != 3) {
           console.log("remove -" + result.rows[i].situation)
           result.rows.splice(i, 1);
           i--;
        }
    }*/
    console.log(result.rows.length);
    res.send(JSON.stringify(result.rows));
  })
})

app.get('/sleepdata/:sid', function(req, res) {
  var sid = req.params.sid;
  var uuid = req.query.uuid;
  var query = "SELECT * FROM " + sid + ".sleep_date WHERE uuid=?";
  client.execute(query, [uuid], function(err, result) {
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify(result.rows));
  })
})

var server = app.listen(3000, function () {

   var host = server.address().address
   var port = server.address().port

   console.log("Example app listening at http://%s:%s", host, port)
})
