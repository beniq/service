var Main = React.createClass({
    getInitialState() {
        return {list: []};
    },
    componentDidMount() {
        common.req("list.json", {}, r => this.setState({list: r}));
    },
    render() {
        let list = this.state.list.map(v =>
            <div className="form-horizontal form-row m-0 p-2" style={{height:"200px"}}>
                <div className="col-2" style={{backgroundImage:"url(./" + v.img + ")", backgroundSize:"100%"}}>
                </div>
                <div className="col-10">
                    <div>生产：{"https://sact.iyunbao.com/prd/" + v.actId + "/main.html"}</div>
                    <div>预发：{"https://sact.iyunbao.com/uat/" + v.actId + "/main.html"}</div>
                    <div>测试：{"https://sact.iyunbao.com/test/" + v.actId + "/main.html"}</div>
                </div>
            </div>
        );
        return <div>{list}</div>;
    },
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});
