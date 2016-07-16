import React from 'react';
import Cytoscape from 'cytoscape';

export default class InstancesDiagram extends React.Component {
    componentDidMount() {
        var elements = this.mountElements(this.props);

        var cy = Cytoscape({
            container: document.getElementById('cy'),

            boxSelectionEnabled: false,
            autounselectify: true,

            layout: {
                name: 'circle'
            },
            style: [
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
                        'width': 1,
                        'target-arrow-shape': 'triangle',
                        'line-color': '#9dbaea',
                        'target-arrow-color': '#9dbaea',
                        'curve-style': 'bezier'
                    }
                }
            ],

            elements: elements
        });

        this.setState({cy: cy});

    }
    componentWillReceiveProps(nextProps) { //on parent update
        var elements = this.mountElements(nextProps);
        this.state.cy.json({ elements: elements });
        this.state.cy.center();
        this.state.cy.layout({name: 'circle'});
    }
    mountElements(props){
        var elements = {
            nodes: [],
            edges: []
        };

        props.services.forEach(function (service) {
            elements.nodes.push({data: {id: service.name}});

            service.links.forEach(function (link) {
                elements.edges.push({data: {id: service.name + "_" + link, source: service.name, target: link}});
            });
        });

        return elements;
    }
    render() {
        return (
            <div className="main">
                <div className="box padding cols">
                    <div className="box content">
                        <h3 className="tit">Services diagram</h3>
                        <div className="tabs box">
                            <div id="cy" style={{width: '100%', height: '100%', position: 'relative'}}></div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
};