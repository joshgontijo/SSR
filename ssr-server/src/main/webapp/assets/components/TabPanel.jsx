import React from 'react';
import $ from 'jquery';

import Tab from './Tab.jsx';
import TabContent from './TabContent.jsx';

//var services = require('./services.json');

export default class TabPanel extends React.Component {
    constructor(props) {
        super(props); //http://stackoverflow.com/questions/30571875/whats-the-difference-between-super-and-superprops-in-react-when-using-e
        this.state = {
            services: props.services,
            currentService: props.services[0]
        };
    }

    tabsClickHandler(tab) {
        var current = null;
        this.state.services.forEach(function (service) {
            if (service.name === tab) {
                current = service;
            }
        });
        this.setState({currentService: current});
    }

    componentWillReceiveProps(nextProps) { //on parent update
        this.state = {
            services: nextProps.services,
            currentService: nextProps.services[0]
        };
    }

    render() {
        var that = this;
        var services = this.props.services.map(function (service, i) {
            return <Tab clickHandler={that.tabsClickHandler.bind(that)} key={service.name} name={service.name}
                        current={service === that.state.currentService}/>
        });
        return (
            <div className="main">
                <div className="box padding cols">
                    <div className="box content">
                        <h3 className="tit">Services</h3>
                        <div className="tabs box">
                            <ul className="ui-tabs-nav">
                                {services}
                            </ul>
                        </div>
                        <div >
                            <TabContent
                                instances={this.state.currentService ? this.state.currentService.instances : []}/>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
};