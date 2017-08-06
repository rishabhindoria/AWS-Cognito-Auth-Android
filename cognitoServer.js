var AWS=require('aws-sdk');
AWS.config.loadFromPath('./config.json');
AWS.config.update({
  region: "us-east-1"
});
var express = require('express')
var app = express();
var bodyParser = require('body-parser');
app.use(bodyParser.json()); // support json encoded bodies

app.post('/', function (req, res) {
	if(req.body.username=="abc"&&req.body.password=="cde") //database call to verify details
	{
		console.log("request success");
		var params = {
			IdentityPoolId: 'us-east-1:********-****-****-****-**********8d', 
			Logins: { 'login.mycompany': '124'// unique id to identify user which can be either username or email address or phone number},
			IdentityId: null,
			TokenDuration: 600 //expiration time
		};
		var cognitoidentity = new AWS.CognitoIdentity();
		cognitoidentity.getOpenIdTokenForDeveloperIdentity(params, function(err, data) {
			if (err) 
				res.send(err);// an error occurred
			else
			{
				console.log("sent data"+data);
				res.send(data);// successful response
			}			
		});
	}
});

app.listen("5000");
console.log("Listening on 5000");