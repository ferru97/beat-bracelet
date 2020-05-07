const MongoClient = require('mongodb').MongoClient
Server = require('mongodb').Server
var ObjectID = require('mongodb').ObjectID
var md5 = require('md5');

mongoclient = new MongoClient(new Server("localhost", 27017), {native_parser: true});
mongoclient.connect();

var db =  mongoclient.db('mqtt')
var app_coll =  db.collection('app_users')
var bracelet_coll =  db.collection('bracelet')

async function AUTH_bracelet(id,psw){

    psw = md5(psw)
    var bid = new ObjectID(id);
    var query = {_id: bid, password: psw};
    var res = await bracelet_coll.findOne(query);
    
    return res;
    
}

function BRC_addMeasurement(bid,value){
    var bid = new ObjectID(bid);
    var measure = {timestamp: Date.now(),value: value}
    var query = { _id: bid };
    var newvalue = { $push: {monitors: measure} };
    bracelet_coll.updateOne(query, newvalue, function(err, res) {
        if(!err) console.log("New measurement added for"+bid)
    });
}

function BRC_addAlert(bid,value){
    var bid = new ObjectID(bid);
    var query = { _id: bid };
    var newvalue = { $push: {alerts: value} };
    bracelet_coll.updateOne(query, newvalue, function(err, res) {
        if(!err) console.log("New measurement added for"+bid)
    });
}


async function APP_login(email ,password){
    var query = {password: md5(password), email: email};
    var res = await app_coll.findOne(query);
    return res;
}

async function APP_addBracelet(uid ,bid, psw, callback){
    if(uid.length==24 && bid.length==24){
        var uid = new ObjectID(uid);
        var bid = new ObjectID(bid);
        var query = {password: md5(psw), _id: bid};
        var brc = bracelet_coll.findOne(query);
        if(brc!=null){
            var query = { _id: uid };
            var newvalue = { $push: {bracelets: bid} };
            app_coll.updateOne(query, newvalue, function(err, res) {
                if(!err) callback("ok")
                else callback("err")
            });
        }else callback("err")
    }else callback("err")
}

async function APP_getBracelets(uid, callback){
    var uid = new ObjectID(uid);
    var query = {_id: uid};
    app_coll.find(query).toArray((err, items) => {
        if(!err && items.length>0){
          query =  { _id : { $in : items[0].bracelets } }
          bracelet_coll.find(query,{projection:{_id:1, name:1, last_activity:1}}).toArray((err, items) => {
            if(!err) callback(`{"res":"ok", "list": ${JSON.stringify(items)}}`);
            else callback(`{"res":"err"}`);
          });
        }
        else callback(`{"res":"err"}`);     
    })
}


async function APP_auth(uid,psw){
    psw = md5(psw)
    var uid = new ObjectID(uid);
    var query = {_id: uid, password: psw};
    var res = await app_coll.findOne(query);
    
    return res;
}


async function APP_checkIsMyBrc(uid,bid){
    var uid = new ObjectID(uid);
    var bid = new ObjectID(bid);
    var query = {_id: uid, bracelets: bid};
    var res = await app_coll.findOne(query);
    return res;
}


async function APP_getBrcInfo(bid){
    var bid = new ObjectID(bid);
    var query = {_id: bid};
    var res = await bracelet_coll.findOne(query);
    
    return res;
    
}

async function APP_setBrcInfo(bid,Bname,Binterval,callback,min_HB,max_HB){
    var bid = new ObjectID(bid);
    var query = { _id: bid };
    var newvalue = {$set:{name: Bname, interval: parseInt(Binterval),min_hb: parseInt(min_HB),max_hB: parseInt(max_HB)}};
    bracelet_coll.updateOne(query, newvalue, function(err, res) {
        if(!err) callback("ok")
        else callback("err")
    });
}


module.exports = {
    AUTH_bracelet: AUTH_bracelet,
    APP_login: APP_login,
    APP_addBracelet: APP_addBracelet,
    APP_getBracelets: APP_getBracelets,
    BRC_addMeasurement: BRC_addMeasurement,
    APP_auth: APP_auth,
    APP_checkIsMyBrc: APP_checkIsMyBrc,
    APP_getBrcInfo:APP_getBrcInfo,
    APP_setBrcInfo: APP_setBrcInfo,
    BRC_addAlert: BRC_addAlert

}