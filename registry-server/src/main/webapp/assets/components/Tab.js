var React = require('react');

module.exports = React.createClass({
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
