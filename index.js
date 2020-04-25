var mosca = require('mosca');
var DBsql = require('./sqlDB');
var md5 = require('md5');

var ascoltatore = {
  //using ascoltatore
  type: 'mongo',
  url: 'mongodb://localhost:27017/mqtt',
  pubsubCollection: 'ascoltatori',
  mongo: {}
};

var settings = {
  port: 1883,
  backend: ascoltatore
};

var authenticate = function(client, username, password, callback) {
  password = md5(password)
  var authorized = (DBsql.authenticate(client.id,password));

  if (authorized) client.user = client.id;
  else console.log("Client autentication faild");
  callback(null, authorized);
}

var authorizeSubscribe = function(client, topic, callback) {
  if(client.id != topic.split('/')[0]) console.log("Unauthorized Subscribe!");
  callback(null, client.id == topic.split('/')[0]);
}

var authorizePublish = function(client, topic, payload, callback) {
  if(client.id != topic.split('/')[0]) console.log("Unauthorized Publish!");
  callback(null, client.id == topic.split('/')[0]);
}

var server = new mosca.Server(settings);

server.on('clientConnected', function(client) {
    console.log('client connected', client.id);
});

// fired when a message is received
server.on('published', function(packet, client) {
  console.log('Published', packet.payload);
});


server.on('ready', setup);

// fired when the mqtt server is ready
function setup() {
 // server.authenticate = authenticate;
  server.authorizePublish = authorizePublish;
  server.authorizeSubscribe = authorizeSubscribe;
  console.log('Mosca server is up and running');
}