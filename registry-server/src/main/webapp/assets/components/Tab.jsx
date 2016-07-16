import React  from 'react';

export default class Tab extends React.Component {
    handleClick(e) {
        e.preventDefault();
        this.props.clickHandler(this.props.name);
    };
    render() {
        return (
            <li className={this.props.current ? 'ui-tabs-selected' : null}>
                <a onClick={this.handleClick.bind(this)} href="#"><span>{this.props.name}</span></a>
            </li>
        );
    }
};
