var React = require('react');

module.exports = React.createClass({
    render: function () {
        console.log('>>>' + JSON.stringify(this.props.instances));
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