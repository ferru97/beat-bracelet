const express = require('express');
const app = express();
var bodyParser = require("body-parser");
var Mongo = require('./MongoDB');

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());


//User login from APP
app.post('/api/login',function(request,response){
    Mongo.APP_login(request.body.email, request.body.psw).then((result)=>{
      if (result!=null)
        response.write(`{"res":"ok", "id":"${result._id}"}`);
      else
          response.write(`{"res":"err"}`);
      response.end()
    })
});   

//User add bracelet
app.post('/api/add_bracelet',function(request,response){
  var callback = (result)=>{
    if(result=="ok")
      response.write(`{"res":"ok"}`);
    else
      response.write(`{"res":"err"}`);
    response.end()
  }
  Mongo.APP_addBracelet(request.body.uid,request.body.bid, request.body.psw, callback )
}); 

//Get the bracelets paird wit a user
app.post('/api/get_bracelets',function(request,response){
  var callback = (result)=>{
    response.write(result);
    response.end()
  }
  Mongo.APP_getBracelets(request.body.uid,callback);
});  



app.listen(3000, () => console.log('Gator app listening on port 3000!'));
