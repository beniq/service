var Content = React.createClass({
    getInitialState() {
        return {mode: 0};
    },
    point(e) {
        if (this.state.mode == 0) {
            window.open("/poster.jpg?param=" + encodeURI(JSON.stringify(this.props.value)))
            return;
        }
        let c = $("#poster");
        let s = c.offset();
        let x = (e.pageX - s.left) * 100 / c.width();
        let y = (e.pageY - s.top) * 100 / c.height();
        x = x.toFixed(2);
        y = y.toFixed(2);
        if (this.state.mode == 1) {
            this.props.value.qrx = x;
            this.props.value.qry = y;
            if (!this.props.value.qrw)
                this.props.value.qrw = 20;
        } else if (this.state.mode == 2) {
            this.props.value.namex = x;
            this.props.value.namey = y;
        }
        this.setState({mode: 0});
    },
    setMode(mode) {
        this.setState({mode: mode});
    },
    save() {
        if(this.props.value.code == null || this.props.value.code == ""){
            alert("海报编码不能为空，且保证唯一");
            return false;
        }
        common.req("save_poster.json", this.props.value, r => {});
    },
    render() {
        let v = this.props.value;
        if (v == null) v = {};
        v.cust = "李测试";
        return (
            <div className="form-row m-0 p-0" style={{height:"100%"}}>
                <div className="col-6 m-0 p-0">
                    { v.imgUrl ?
                        <img id="poster" src={"/poster.jpg?param=" + encodeURI(JSON.stringify(v))} style={{width:"100%"}} onClick={this.point}/> :
                        <div style={{width:"100%", minHeight:"400px", border:"#000 1px solid", textAlign:"center"}}>拖拽海报图片至此</div>
                    }
                </div>
                <div className="col-6 m-0 p-0">
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>海报名称</div>
                        </div>
                        <input type="text" className="form-control" value={v.name} onChange={e => { v.name = e.target.value; }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>海报编码</div>
                        </div>
                        <input type="text" className="form-control" value={v.code} onChange={e => { v.code = e.target.value; }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>二维码URL</div>
                        </div>
                        <input type="text" className="form-control" value={v.qrUrl} onChange={e => { v.qrUrl = e.target.value; }}/>
                        <div className="input-group-append">
                            <select className="form-control" value={v.qrUrlUserId} onChange={e => { v.qrUrlUserId = e.target.value; }}>
                                <option value="Y">追加用户id</option>
                                <option value="N">不追加用户id</option>
                            </select>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>二维码XYS</div>
                        </div>
                        <input type="text" className="form-control" value={v.qrw} onChange={e => { v.qrw = e.target.value; }}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" style={{width:"80px"}}>{v.qrx}</div>
                            <div className="btn btn-outline-primary" style={{width:"80px"}}>{v.qry}</div>
                            <div className="btn btn-outline-primary" style={{width:"80px"}} onClick={this.setMode.bind(this, 1)}>选择</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>姓名XYS</div>
                        </div>
                        <select className="form-control" value={v.nameFontSize} onChange={e => { v.nameFontSize = e.target.value; }}>
                            <option value="0">无</option>
                            <option value="7">7px</option>
                            <option value="8">8px</option>
                            <option value="9">9px</option>
                            <option value="10">10px</option>
                            <option value="11">11px</option>
                            <option value="12">12px</option>
                            <option value="14">14px</option>
                            <option value="16">16px</option>
                            <option value="18">18px</option>
                            <option value="20">20px</option>
                            <option value="22">22px</option>
                            <option value="24">24px</option>
                            <option value="28">28px</option>
                            <option value="32">32px</option>
                            <option value="48">48px</option>
                            <option value="64">64px</option>
                            <option value="72">72px</option>
                        </select>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" style={{width:"80px"}}>{v.namex}</div>
                            <div className="btn btn-outline-primary" style={{width:"80px"}}>{v.namey}</div>
                            <div className="btn btn-outline-primary" style={{width:"80px"}} onClick={this.setMode.bind(this, 2)}>选择</div>
                        </div>
                    </div>
                    <div className="p-2">
                        <button className="form-control btn btn-danger" onClick={this.save}>保存</button>
                    </div>
                </div>
            </div>
        )
    }
});

var Main = React.createClass({
    getInitialState() {
        return {list: [], now: null};
    },
    componentDidMount() {
        common.req("list_poster.json", {}, r => this.setState({list: r, now: r.length > 0 ? r[0] : null}));

        let drop = e => {
            e.preventDefault();
            var fileList = e.dataTransfer.files;
            if (fileList.length > 0) {
                var xhr = new XMLHttpRequest();
                xhr.open("post", common.url("poster.do"), true);
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.onreadystatechange = r => {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        let v = JSON.parse(xhr.responseText).content;
                        for (var x in v) {
                            this.state.now[x] = v[x];
                        }
                        this.forceUpdate(() => {
                            this.refs.poster.save();
                        });
                    }
                };
                var fd = new FormData();
                fd.append("type", "poster");
                if (fd != null) {
                    for (var i = 0; i < fileList.length; i++)
                        fd.append("file", fileList[i]);
                    xhr.send(fd);
                }
            }
        };

        document.getElementById('content').addEventListener("drop", drop);
    },
    select(v) {
        this.setState({now: v});
    },
    create() {
        let np = { name:"新海报", qrUrl:"https://", qrUrlUserId: "Y"};
        this.state.list.push(np);
        this.setState({list: this.state.list, now: np});
    },
    render() {
        let list = this.state.list.map(v =>
            <div className={"input-group p-2" + (this.state.now == v ? " bg-success" : "")} onClick={this.select.bind(this, v)}>
                <div type="text" className={"form-control btn text-left" + (this.state.now == v ? " btn-outline-light" : " btn-outline-dark")}>{v.name}</div>
            </div>
        );
        return <div className="form-horizontal m-0 p-0">
            <div className="form-row m-0 p-0">
                <div className="col-4 m-0 p-2">
                    {list}
                    <div className="input-group p-2">
                        <button className="form-control btn btn-danger" onClick={this.create}>新的海报</button>
                    </div>
                </div>
                <div className="col-8 m-0">
                    <Content ref="poster" value={this.state.now}/>
                </div>
            </div>
        </div>;
    },
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));

    $(document).on({
        keydown: this.onKeyDown,
        dragleave:function(e){    //拖离
            e.preventDefault();
        },
        drop:function(e){  //拖后放
            e.preventDefault();
        },
        dragenter:function(e){    //拖进
            e.preventDefault();
        },
        dragover:function(e){    //拖来拖去
            e.preventDefault();
        }
    });
});