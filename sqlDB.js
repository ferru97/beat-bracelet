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

module.exports = {
    authenticate: authenticate,
}