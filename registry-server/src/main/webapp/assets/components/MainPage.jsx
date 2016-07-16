import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import _ from 'underscore';
import TabPanel from './TabPanel.jsx';
import InstancesDiagram from './InstancesDiagram.jsx';

export default class MainPage extends React.Component {
    constructor() {
        super();
        this.state = {
            services: []
        };
    }
    fetchServices(){
        $.getJSON(root + "/api/services", function (data) {
            if(!_.isEqual(this.state.services, data)){
                this.setState({
                    services: data
                });
            }
        }.bind(this));
    }
    componentDidMount() {
        this.fetchServices();
        setInterval(() => {
            this.fetchServices();
        }, 10000);
    }
    render() {
        //console.log(' >>>>>>>> ' + JSON.stringify(this.state.services));
        return (
            <div>
                <TabPanel services={this.state.services}/>
                <InstancesDiagram services={this.state.services}/>
            </div>
        );
    }
};