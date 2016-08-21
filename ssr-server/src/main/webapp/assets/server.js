var express = require('express');
var cors = require('cors');

var fs = require('fs');
var path = require('path');

var app = express();

app.use(cors());

app.get('/', function (req, res) {
    res.send('Hello World')
});

app.get('/api/services', function (req, res) {
    var obj = JSON.parse(fs.readFileSync('./json/services.json', 'utf8'));
    res.json(obj)
});

app.listen(3000);