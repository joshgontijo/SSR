import React  from 'react';

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
                </tr>
                {this.props.instances.map(function (instance, i) {
                    return <tr key={instance.id} className={instance.available ? '' : 'disabledRow'}>
                        <td>{instance.id}</td>
                        <td><a href={instance.address}>{instance.address}</a></td>
                        <td>{instance.since}</td>
                        <td>{instance.downSince}</td>
                    </tr>
                })}

                </tbody>
            </table>
        );
    }
};