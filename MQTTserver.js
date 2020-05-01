var mosca = require('mosca');
var Mongo = require('./MongoDB');
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

var server = new mosca.Server(settings);


var authenticate = function(client, username, password, callback) {
  client.username = username;
  if(username[0]!='*'){//If starts with * is an user app client
    Mongo.AUTH_bracelet(client.id,password).then((result)=>{
      var authorized = result!=null
      if (!authorized) console.log("Client autentication faild");
      callback(null, authorized);
    });
  }else{
    Mongo.APP_auth(client.id,password.toString()).then((result)=>{
      var authorized = result!=null
      if (!authorized) console.log("Client autentication faild");
      callback(null, authorized);
    });
  }
}

var authorizeSubscribe = function(client, topic, callback) {
  if( client.username[0]!='*' && topic.split('/')[1]=="new_measure"){
    var authorized = client.id == topic.split('/')[0]
    if (!authorized) console.log("1.Unauthorized Subscribe for "+topic);
    callback(null, authorized);
  }

  if( client.username[0]=='*' && topic.split('/')[1]=="alert"){
    Mongo.APP_checkIsMyBrc(client.id,topic.split('/')[0]).then((result)=>{
      var authorized = result!=null
      if (!authorized) console.log("2.Unauthorized Subscribe for "+topic);
      callback(null, authorized);
    });
  }
}

var authorizePublish = function(client, topic, payload, callback) {
  if(client.username[0]!='*' && topic.split('/')[1]=="new_measure"){
    var authorized = client.id == topic.split('/')[0]
    if(authorized) 
      Mongo.BRC_addMeasurement(client.id,payload.toString())
    else 
      console.log("1.Unauthorized Publish! "+topic);
    callback(null, authorized);
  } 

  if(client.username[0]!='*' && topic.split('/')[1]=="alert"){
    console.log("ALERT "+payload)
    var authorized = client.id == topic.split('/')[0]
    if(!authorized) console.log("1.Unauthorized Publish! "+topic);
    callback(null, authorized);
  } 
}

server.on('clientConnected', function(client) {
    console.log('client connected', client.id);
});

// fired when a message is received
server.on('published', function(packet, client) {
  console.log('Published', packet.topic);
});


server.on('ready', setup);

// fired when the mqtt server is ready
function setup() {
  server.authenticate = authenticate;
  server.authorizePublish = authorizePublish;
  server.authorizeSubscribe = authorizeSubscribe;
  console.log('Mosca server is up and running');
}