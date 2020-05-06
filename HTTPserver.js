const express = require('express');
const app = express();
var bodyParser = require("body-parser");
var Mongo = require('./MongoDB');

function toTimestamp(strDate){
  var datum = Date.parse(strDate);
  return datum/1000;
 }

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

app.post('/api/brc_info',function(request,response){
  Mongo.APP_getBrcInfo(request.body.bid).then((result)=>{
    if (result!=null){
      var alerts,monitors;
      if(request.body.filter==""){
        alerts = result.alerts
        monitors = result.monitors
      }else{
        var min = toTimestamp(request.body.filter)*1000;
        var max = min + (60000*60*24); //+24H
        console.log(min+"/"+max+"/"+result.monitors[0].timestamp)
        monitors = [];
        alerts = [];
        
        for(var k=0; k<result.monitors.length; k++){
          if(result.monitors[k].timestamp>=min && result.monitors[k].timestamp<=max ){
            monitors.push(result.monitors[k]);
            console.log("Ok")
          }
            
        }
        for(var k=0; k<result.monitors.alerts; k++){
          if(result.alerts[k].timestamp>=min && result.alerts[k].timestamp<=max )
            alerts.push(result.alerts[k]);
        }
      }

      response.write(`{"res":"ok", "name":"${result.name}", "n_monitor":"${result.monitors.length}","n_alerts":"${result.alerts.length}", "monitors":${JSON.stringify(monitors)}, "alerts":${JSON.stringify(alerts)}, "intrval":"${result.interval}"}`);
    }
    else
        response.write(`{"res":"err"}`);
    response.end()
  })
});

app.post('/api/set_brc_info',function(request,response){
  var callback = (result)=>{
    if(result=="ok")
      response.write(`{"res":"ok"}`);
    else
      response.write(`{"res":"err"}`);
    response.end()
  }
  Mongo.APP_setBrcInfo(request.body.bid,request.body.name,request.body.interval,callback);
});



app.listen(3000, () => console.log('Gator app listening on port 3000!'));
