var DBsql = require('./sqlDB');
var md5 = require('md5');
const express = require('express');
const app = express();
var bodyParser = require("body-parser");
const mongo = require('mongodb').MongoClient
var ObjectID = require('mongodb').ObjectID
const mongo_url = 'mongodb://localhost:27017'

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

mongo.connect(mongo_url, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  }, (err, client) => {
    if (err) {
      console.error(err)
      return
    }

    var db = client.db('mqtt')
    var app_coll = db.collection('app_users')
    var bracelet_coll = db.collection('bracelet')

    //User login from APP
    app.post('/api/login',function(request,response){
        var query = {password: md5(request.body.psw), email: request.body.email};
        app_coll.find(query).toArray((err, items) => {
            if(items.length>0)
                response.write(`{"res":"ok", "id":"${items[0]._id}"}`);
            else
                response.write(`{"res":"err"}`);
            response.end();
        })
    });   
    
    
    //User add bracelet
    app.post('/api/add_bracelet',function(request,response){
      if(request.body.uid.length==24 && request.body.bid.length==24){
        var uid = new ObjectID(request.body.uid);
        var bid = new ObjectID(request.body.bid);
        var query = {password: md5(request.body.psw), _id: bid};
        bracelet_coll.find(query).toArray((err, items) => {
            if(!err && items.length>0){
              var query = { _id: uid };
              var newvalue = { $push: {bracelets: bid} };
              app_coll.updateOne(query, newvalue, function(err, res) {
                if(!err){
                  response.write(`{"res":"ok"}`);
                  response.end();
                }else{
                  response.write(`{"res":"err1"}`);
                  response.end();
                }
              });
            }
            else{
              response.write(`{"res":"err2"}`);
              response.end();
            }
        })
      }else{
        response.write(`{"res":"err3"}`);
        response.end();
      }
      
  }); 

  //Get the bracelets paird wit a user
  app.post('/api/get_bracelets',function(request,response){
    var uid = new ObjectID(request.body.uid);
    var query = {_id: uid};
    app_coll.find(query).toArray((err, items) => {
        if(items.length>0){
          query =  { _id : { $in : items[0].bracelets } }
          bracelet_coll.find(query,{projection:{_id:1, name:1, last_activity:1}}).toArray((err, items) => {
            if(!err)
              response.write(`{"res":"ok", "list": ${JSON.stringify(items)}}`);
            else
              response.write(`{"res":"err"}`);
            response.end();
          });
        }
        else{
          response.write(`{"res":"err"}`);
          response.end();
        }       
    })
  });  

})


app.listen(3000, () => console.log('Gator app listening on port 3000!'));
