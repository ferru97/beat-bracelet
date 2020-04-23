var DBsql = require('./sqlDB');
var md5 = require('md5');
const express = require('express');
const app = express();
var bodyParser = require("body-parser");
const mongo = require('mongodb').MongoClient
const mongo_url = 'mongodb://localhost:27017'

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

mongo.connect(mongo_url, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  }, (err, client) => {
    var db = client.db('mqtt')
    var app_coll = db.collection('app_users')
    var bracelet_coll = db.collection('bracelet')

    //User login from APP
    app.post('/api/login',function(request,response){
        var query = {password: `${md5(request.body.psw)}`, email: `${request.body.email}`};
        app_coll.find(query).toArray((err, items) => {
            if(items.length>0)
                response.write(`{"res":"ok", "id":"${items[0]._id}"}`);
            else
                response.write(`{"res":"err"}`);
            response.end();
        })
    });    

  if (err) {
    console.error(err)
    return
  }
})

/*res.write(DBsql.loginClient(req.body.email, md5(req.body.psw)));
        res.end();*/

app.listen(3000, () => console.log('Gator app listening on port 3000!'));