import React  from 'react';
import _ from 'underscore'

export default class TabContent extends React.Component {
    render() {
        return (
            <table className="nostyle">
                <tbody>
                <tr>
                    <th>ID</th>
                    <th>URL</th>
                    <th>Since</th>
                    <th>Down Since</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                {this.props.instances.map(function (instance, i) {
                    return <tr key={instance.id} className={instance.state == 'OUT_OF_SERVICE'
                                                            || instance.state == 'DOWN' ? 'disabledRow' : ''}>
                        <td>{instance.id}</td>
                        <td><a href={instance.address}>{instance.address}</a></td>
                        <td>{instance.since}</td>
                        <td>{instance.downSince}</td>
                        <td>{instance.state}</td>
                        <td>ACTION</td>
                    </tr>
                })}

                </tbody>
            </table>
        );
    }
};