var React = require('react');
var cytoscape = require('cytoscape');
var cydagre = require('cytoscape-dagre');
var dagre = require('dagre');
var instances = require('./instances.json');

cydagre(cytoscape, dagre);


module.exports = React.createClass({
    getInitialState: function () {
        return {
            id: 1
        };
    },
    componentDidMount: function () {
        var nodes = [];
        var edges = [];
        $.getJSON(root + "/api/services", function (data) {

            data.forEach(function(e){
               nodes.push({data: {id: e.name}});
                e.links.forEach(function(link){
                    edges.push({data: {source: e.name, target: link}})
                })
            });

            this.setState({
                services: data,
            });
        }.bind(this));

        var that = this;
        var cy = cytoscape({
            container: document.getElementById('cy'),

            boxSelectionEnabled: false,
            autounselectify: true,

            layout: {
                name: 'dagre'
            },

            style: [
                {
                    selector: 'node',
                    style: {
                        'content': 'data(id)',
                        'text-opacity': 0.5,
                        'text-valign': 'center',
                        'text-halign': 'center',
                        'background-color': '#11479e'
                    }
                },

                {
                    selector: 'edge',
                    style: {
                        'width': 1,
                        'target-arrow-shape': 'triangle',
                        'line-color': '#9dbaea',
                        'target-arrow-color': '#9dbaea',
                        'curve-style': 'bezier'
                    }
                }
            ],

            elements: {
                nodes: [
                    {data: {id: 'n0'}},
                    {data: {id: 'n1'}},
                    {data: {id: 'n2'}},
                    {data: {id: 'n3'}},
                    {data: {id: 'n4'}},
                    {data: {id: 'n5'}},
                    {data: {id: 'n6'}},
                    {data: {id: 'n7'}},
                    {data: {id: 'n8'}},
                    {data: {id: 'n9'}},
                    {data: {id: 'n10'}},
                    {data: {id: 'n11'}},
                    {data: {id: 'n12'}},
                    {data: {id: 'n13'}},
                    {data: {id: 'n14'}},
                    {data: {id: 'n15'}},
                    {data: {id: 'n16'}}
                ],
                edges: [
                    {data: {source: 'n0', target: 'n1'}},
                    {data: {source: 'n1', target: 'n0'}},
                    {data: {source: 'n1', target: 'n2'}},
                    {data: {source: 'n1', target: 'n3'}},
                    {data: {source: 'n4', target: 'n5'}},
                    {data: {source: 'n4', target: 'n6'}},
                    {data: {source: 'n6', target: 'n7'}},
                    {data: {source: 'n6', target: 'n8'}},
                    {data: {source: 'n8', target: 'n9'}},
                    {data: {source: 'n8', target: 'n10'}},
                    {data: {source: 'n11', target: 'n12'}},
                    {data: {source: 'n12', target: 'n13'}},
                    {data: {source: 'n13', target: 'n14'}},
                    {data: {source: 'n13', target: 'n15'}},
                ]
            }
        });

        this.setState({cy: cy});

    },
    addElement: function (e) {
        e.preventDefault();
        var cy = this.state.cy;
        var id = this.state.id;
        cy.add([
            {group: "nodes", data: {id: id}},
            {group: "edges", data: {id: id + '_edge', source: "n0", target: id}}
        ]);
        this.setState({id: id + 1});
    },
    render: function () {
        return (
            <div className="main">
                <div className="box padding cols">
                    <div  className="box content">
                        <h3 className="tit">Services diagram</h3>
                        <div className="tabs box">
                            <button onClick={this.addElement}>Add Element</button>
                            <div id="cy" style={{width: '100%', height: '100%', position: 'relative'}}></div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
});