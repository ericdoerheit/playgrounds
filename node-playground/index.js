var express = require('express');
var app = express();

var counter = 0;

// Allow CORS
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.get('/', function (req, res) {
    counter += 1;
    if(counter <= 1) {
        res.header('Content-Type', 'application/json');
        res.send(JSON.stringify({ hello: 'world', count: counter}));
    } else if(counter == 2) {
        res.status(403);
        res.end();
    } else {
        res.status(401);
        res.end();
    }


});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});
