var Http = require( 'http' ),
    Router = require( 'router' ),
    server,
    router;
var cassandra = require('cassandra-driver');
var client = new cassandra.Client({contactPoints: ['172.18.161.100', '172.18.161.101']});

router = new Router();
 
server = Http.createServer( function( request, response ) {
  router( request, response, function( error ) {
    if ( !error ) {
      response.writeHead( 404 );
    } else {
      // Handle errors
      console.log( error.message, error.stack );
      response.writeHead( 400 );
    }
    response.end( 'RESTful API Server is running!' );
  });
});
 
server.listen( 3000, function() {
  console.log( 'Listening on port 3000' );
});

var counter = 0,
    todoList = {};

function getSchoolIds(request, response ) {
  var query="SELECT sid FROM schoolsinfo.schools";
  client.execute(query, function(err, result) {
    response.writeHead( 201, {
       'Content-Type' : 'text/plain'
    });
    response.end(JSON.stringify(result.rows));
  })
}

function getUuids(request, response) {
  var sid = request.params.sid;
  var query="SELECT uuid FROM schoolsinfo.sidsuuids where sid=?";
  client.execute(query, [sid], function(err, result) {
  response.writeHead( 201, {
       'Content-Type' : 'text/plain'
    });
    response.end(JSON.stringify(result.rows));
  }) 
}

function getContext(request, response) {
  var sid = request.params.sid;
  var uuid = request.params.uuid;
  var query = "SELECT * FROM " + sid + ".context_date WHERE uuid=?";
  client.execute(query, [uuid], function(err, result) {
  response.writeHead( 201, {
       'Content-Type' : 'text/plain'
    });
    response.end(JSON.stringify(request.params));
  })
}

router.get('/getschoolids', getSchoolIds)
router.get('/getuuids/:sid', getUuids);
router.get('/getcontext/:sid', getContext);
