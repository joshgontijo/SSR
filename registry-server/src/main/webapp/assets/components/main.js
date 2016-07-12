var React = require('react');
var ReactDOM = require('react-dom');
var TabPanel = require('./TabPanel');
var InstancesDiagram = require('./InstancesDiagram');



ReactDOM.render(
    <TabPanel />,
    document.getElementById('mainComponent')
);
ReactDOM.render(
    <InstancesDiagram />,
    document.getElementById('diagram')
);

