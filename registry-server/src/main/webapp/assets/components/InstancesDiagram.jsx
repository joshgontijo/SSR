import React from 'react';
import Cytoscape from 'cytoscape';

export default class InstancesDiagram extends React.Component {
    componentDidMount() {
        var mounted = this.mountElements(this.props);

        var cy = Cytoscape({
            container: document.getElementById('cy'),

            boxSelectionEnabled: false,
            autounselectify: true,

            layout: {
                name: 'circle'
            },
            style: [ //order matters
                {
                    selector: 'node',
                    style: {
                        'content': 'data(id)',
                        'text-opacity': 0.5,
                        'text-valign': 'top',
                        'text-halign': 'center',
                        'background-color': '#11479e'
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': 6,
                        'target-arrow-shape': 'triangle',
                        'line-color': '#9dbaea',
                        'target-arrow-color': '#9dbaea',
                        'curve-style': 'bezier'
                    }
                },
                {
                    selector: '.disabled',
                    style: {
                        'background-color': 'red',
                        'line-color': '#ddd',
                        'target-arrow-color': '#ddd',
                    }
                },
            ],

            elements: mounted.elements
        });

        this.setState({cy: cy});

    }

    componentWillReceiveProps(nextProps) { //on parent update
        var mounted = this.mountElements(nextProps);
        this.state.cy.json({elements: mounted.elements});
        this.state.cy.center();
        this.state.cy.layout({name: 'circle'});

        var that = this;
        mounted.disabledNodes.forEach(function(node){
            var n = that.state.cy.$('#'+node.data.id);
            var edges = n.connectedEdges();
            edges.forEach(function(edge){
               edge.addClass('disabled');
            });
        });


    }

    mountElements(props) {

        var disabledNodes = [];

        var elements = {
            nodes: [],
            edges: []
        };

        props.services.forEach(function (service) {
            var available = service.instances.map(function (instance) {
                return instance.available;
            }).reduce(function (current, acum) {
                return current && acum;
            });

            var node = {
                data: {
                    id: service.name
                },
                classes: available ? '' : 'disabled'
            };

            if(!available){
                disabledNodes.push(node);
            }
            elements.nodes.push(node);

            service.links.forEach(function (link) {
                elements.edges.push({
                    data: {
                        id: service.name + "_" + link,
                        source: service.name, target: link
                    },
                    classes: available ? '' : 'disabled'
                });
            });
        });

        return {elements: elements, disabledNodes: disabledNodes};
    }

    render() {
        return (
            <div className="main">
                <div className="box padding cols">
                    <div className="box content">
                        <h3 className="tit">Services diagram</h3>
                        <div className="tabs box">
                            <div id="cy" style={{width: '100%', height: '60%', position: 'relative'}}></div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
};