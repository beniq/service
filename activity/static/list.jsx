var Content = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
    },
    edit() {
        document.location.href = "draw.html?actId=" + this.props.act.actId;
    },
    render() {
        let v = this.props.act;
        return v == null ? null : (
            <div className="form-row m-0 p-0" style={{height:"100%"}}>
                {/*<div className="col-6 m-0 p-0" style={{backgroundImage:"url(./" + v.img + ")", backgroundSize:"100%"}} onClick={this.edit}></div>*/}
                <div className="col-6 m-0 p-0">
                    <div className="input-group p-2">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>CODE</div>
                        </div>
                        <input type="text" className="form-control" readOnly="true" value={v.code}/>
                    </div>
                    <div className="input-group p-2">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>名称</div>
                        </div>
                        <input type="text" className="form-control" readOnly="true" value={v.name}/>
                    </div>
                    <div className="input-group p-2">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>生产链接</div>
                        </div>
                        <input type="text" className="form-control" readOnly="true" value={"https://sact.iyunbao.com/prd/" + v.actId + "/main.html"}/>
                    </div>
                    <div className="input-group p-2">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>预发链接</div>
                        </div>
                        <input type="text" className="form-control" readOnly="true" value={"https://sact.iyunbao.com/uat/" + v.actId + "/main.html"}/>
                    </div>
                    <div className="input-group p-2">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>测试链接</div>
                        </div>
                        <input type="text" className="form-control" readOnly="true" value={"https://sact.iyunbao.com/test/" + v.actId + "/main.html"}/>
                    </div>
                    <div className="input-group p-2">
                        <button className="form-control btn btn-danger" onClick={this.edit}>编辑</button>
                    </div>
                </div>
                <div className="col-6 m-0 p-0"><iframe style={{width:"100%", height:"100%", border:"0"}} src={"https://sact.iyunbao.com/act/" + v.actId + "/test.html?accountId=1"}></iframe></div>
            </div>
        )
    },
});

var Main = React.createClass({
    getInitialState() {
        return {list: [], now: null};
    },
    componentDidMount() {
        common.req("list.json", {}, r => this.setState({list: r, now: r.length > 0 ? r[0] : null}));
    },
    select(v) {
        this.setState({now: v});
    },
    create() {
        document.location.href = "draw.html";
    },
    render() {
        let list = this.state.list.map(v =>
            <div className={"input-group p-2" + (this.state.now == v ? " bg-success" : "")} onClick={this.select.bind(this, v)}>
                <div className="input-group-prepend">
                    <div className={"btn" + (this.state.now == v ? " btn-light" : " btn-success")} style={{width:"120px"}}>{v.code}</div>
                </div>
                <div type="text" className={"form-control btn text-left" + (this.state.now == v ? " btn-outline-light" : " btn-outline-dark")}>{v.name}</div>
            </div>
        );

        //let list = this.state.list.map(v => <div className={"p-3" + (this.state.now == v ? " bg-success" : "")} onClick={this.select.bind(this, v)}>{v.code} / {v.name}</div>);
        return <div className="form-horizontal m-0 p-0">
            <div className="form-row m-0 p-0">
                <div className="col-4 m-0 p-2" style={{overflowY:"scroll"}}>
                    {list}
                    <div className="input-group p-2">
                        <button className="form-control btn btn-danger" onClick={this.create}>新的活动</button>
                    </div>
                </div>
                <div className="col-8 m-0 p-2" style={{overflowY:"scroll"}}>
                    <Content act={this.state.now}/>
                </div>
            </div>
        </div>;
    },
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});