var mysql = require('mysql');

var MySql = require('sync-mysql');

var connection = new MySql({
  host: 'localhost',
  user: 'root',
  port: 3308,
  password: '',
  database: 'beat_bracelet'
});

function authenticate(id,psw){
    const result = connection.queueQuery(`SELECT COUNT(*) AS n FROM bracelet WHERE id='${id}' AND password='${psw}'`);
    return result()[0].n == 1;
}


function loginClient(email,psw){
  const result = connection.queueQuery(`SELECT id FROM user WHERE email='${email}' AND password='${psw}'`);
  if(result().length==1){
    const id = result[0].id;
    const result2 = connection.queueQuery(`SELECT id,id_bracelet, name FROM user_bracelet WHERE id_user='${id}' `);
    return result2();
  }else
    return "{login: faild}";
}

module.exports = {
    authenticate: authenticate,
}