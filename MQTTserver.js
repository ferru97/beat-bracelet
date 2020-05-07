var mosca = require('mosca');
var Mongo = require('./MongoDB');
var dateFormat = require('dateformat');


function pubNewIntrval(bid,value){
  server.publish({
    topic: bid+'/new_inter',
    payload: Buffer.from(value),
    qos: 1, // this is important for offline messaging
    retained: true
  }, null, function done() {})
}
function pubNewHBrange(bid,min_hb,max_hb){
  server.publish({
    topic: bid+'/new_hb_range',
    payload: Buffer.from(min_hb+"-"+max_hb),
    qos: 1, // this is important for offline messaging
    retained: true
  }, null, function done() {})
}

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
  if( client.username[0]!='*' && topic.split('/')[1]=="new_inter"){
    var authorized = client.id == topic.split('/')[0]
    if (!authorized) console.log("1.Unauthorized Subscribe for "+topic);
    else{
      Mongo.APP_getBrcInfo(client.id).then((result)=>{
        if (result!=null) pubNewIntrval(client.id,result.interval.toString());
      })
      callback(null, authorized);
    } 
  }
  if( client.username[0]!='*' && topic.split('/')[1]=="new_hb_range"){
    var authorized = client.id == topic.split('/')[0]
    if (!authorized) console.log("1.Unauthorized Subscribe for "+topic);
    else{
      Mongo.APP_getBrcInfo(client.id).then((result)=>{
        if (result!=null) pubNewHBrange(client.id,result.min_hb.toString(),result.max_hb.toString());
      })
      callback(null, authorized);
    } 
  }

  if( client.username[0]!='*' && topic.split('/')[1]=="new_measure"){
    var authorized = client.id == topic.split('/')[0]
    if (!authorized) console.log("1.Unauthorized Subscribe for "+topic);
    callback(null, authorized);
  }

  if( client.username[0]=='*' && (topic.split('/')[1]=="alert" || topic.split('/')[1]=="alert_hb")){
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
    else{
      var now = new Date();
      dateFormat(now, "dddd, mmmm dS, yyyy, h:MM:ss TT");
      var msg = "Alert type: BUTTON    Date: "+now.toString();
      Mongo.BRC_addAlert(client.id, msg);
    } 
    callback(null, authorized);
  }

  if(client.username[0]!='*' && topic.split('/')[1]=="alert_hb"){
    console.log("ALERT "+payload)
    var authorized = client.id == topic.split('/')[0]
    if(!authorized) console.log("1.Unauthorized Publish! "+topic);
    else{
      var now = new Date();
      dateFormat(now, "dddd, mmmm dS, yyyy, h:MM:ss TT");
      var msg = "Alert type: BPM    Date: "+now.toString();
      Mongo.BRC_addAlert(client.id, msg);
    } 
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