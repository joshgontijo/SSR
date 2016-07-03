var services = [
    {
        "name": "account",
        "instances": [
            {
                "id": "0",
                "name": "account",
                "address": "http://192.168.0.7:8081/account/rest",
                "since": "2016-06-26 02:05:25"
            }
        ]
    }
];

var TabPanel = React.createClass({
    componentDidMount: function () {
        $.getJSON(root +"/api/services", function (data) {
            this.setState({
                services: data,
                currentTab: data[0]
            });
        }.bind(this));
    },
    getInitialState: function () {
        return {
            services: services,
            currentTab: services[0]
        };
    },
    tabsClickHandler: function (tab) {
        var current = null;
        this.state.services.forEach(function (service) {
            if (service.name === tab) {
                current = service;
            }
        });
        this.setState({currentTab: current});
    },
    render: function () {
        var that = this;
        //console.log("render - > " + JSON.stringify(that.state));
        return (
            <div id="main">
                <div id="cols" className="box padding">
                    <div id="content" className="box">
                        <h3 className="tit">Services</h3>
                        <div className="tabs box">
                            <ul className="ui-tabs-nav">

                                {this.state.services.map(function (tab, i) {
                                    return <Tab clickHandler={that.tabsClickHandler} key={tab.name} name={tab.name}
                                                current={tab === that.state.currentTab}/>
                                })}

                            </ul>
                        </div>
                        <div >
                            <TabContent
                                instances={that.state.currentTab != null ? that.state.currentTab.instances : []}/>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

var Tab = React.createClass({
    handleClick: function (e) {
        e.preventDefault();
        this.props.clickHandler(this.props.name);
    },
    render: function () {
        return (
            <li className={this.props.current ? 'ui-tabs-selected' : null}>
                <a onClick={this.handleClick} href="#"><span>{this.props.name}</span></a>
            </li>
        );
    }
});

var TabContent = React.createClass({
    render: function () {
        return (
            <table className="nostyle">
                <tbody>
                <tr>
                    <th>ID</th>
                    <th>URL</th>
                    <th>Since</th>
                </tr>
                {this.props.instances.map(function (instance, i) {
                    return <tr key={instance.id}>
                        <td>{instance.id}</td>
                        <td><a href={instance.address}>{instance.address}</a></td>
                        <td>{instance.since}</td>
                    </tr>
                })}

                </tbody>
            </table>
        );
    }
});

ReactDOM.render(
    <TabPanel />,
    document.getElementById('mainComponent')
);
