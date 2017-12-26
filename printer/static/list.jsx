var env = {
    search: null,
    from: 0,
    number: 20
}

var List = React.createClass({
    getInitialState() {
        return {content:{list:[], total:0}};
    },
    componentDidMount() {
        this.refresh();
    },
    page(i) {
        env.from = i * env.number;
        if (env.from < 0)
            env.from = 0;
        else if (env.from >= env.total)
            env.from = env.from - env.number;
        this.refresh();
    },
    buildPageComponent() {
        let page = [];
        env.total = this.state.content.total;
        for (var i = 0; i < env.total / env.number; i++) {
            page.push(<a key={i} onClick={this.page.bind(this, i)}>&nbsp;{i+1}&nbsp;</a>);
        }
        return (
            <div className="bottom">
                <a onClick={this.page.bind(this, env.from / env.number - 1)}>上一页&nbsp;&nbsp;</a>
                {page}
                <a onClick={this.page.bind(this, env.from / env.number + 1)}>&nbsp;&nbsp;下一页</a>
            </div>
        );
    },
    render() {
        return (
            <div className="list">
                <br/>
                { this.buildConsole() }
                <table>
                    <thead>{ this.buildTableTitle() }</thead>
                    <tbody>{ this.state.content.list.map(v => this.buildTableLine(v)) }</tbody>
                </table>
                <br/>
                { this.buildPageComponent() }
                <CreateWin ref="win" parent={this}/>
                <ExportWin ref="expWin"/>
            </div>
        );
    }
});

class TemplateList extends List {
    create() {
    }
    import() {
    }
    test(templateId) {
        document.location.href = "test.html?templateId=" + templateId;
    }
    edit(templateId) {
        document.location.href = "edit.html?templateId=" + templateId;
    }
    export(templateId) {
        this.refs.expWin.setState({text: ""});
        common.req("export.json", {templateId:templateId}, r => {
            this.refs.expWin.setState({text: JSON.stringify(r)});
        });
    }
    synch(templateId) {
        common.req("export.json", {templateId:templateId}, r => {
            common.post("https://sv-uat.iyb.tm/printer/save.json", r, r1 => {
                alert("success");
            });
        });
    }
    refresh() {
        common.req("list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildConsole() {
        return (
            <nav className="navbar navbar-default">
                <div className="container-fluid">
                    <div className="collapse navbar-collapse">
                        <ul className="nav navbar-nav">
                        </ul>
                        <ul className="nav navbar-nav navbar-right">
                            <li><a data-toggle="modal" data-target="#createWin" onClick={this.create}>新的模板</a></li>
                            <li><a data-toggle="modal" data-target="#createWin" onClick={this.import}>导入模板</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        );
    }
    buildTableTitle() {
        return (
            <tr>
                <th>ID</th>
                <th>CODE</th>
                <th>名称</th>
                <th></th>
            </tr>
        );
    }
    buildTableLine(v) {
        return (
            <tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.code}</td>
                <td>{v.name}</td>
                <td width="15%">
                    <button type="button" className="btn btn-primary" onClick={this.edit.bind(this, v.id)}>编辑</button>
                    &nbsp;&nbsp;
                    <button type="button" className="btn btn-primary" onClick={this.test.bind(this, v.id)}>测试</button>
                    &nbsp;&nbsp;
                    <button type="button" className="btn btn-primary" onClick={this.synch.bind(this, v.id)}>同步</button>
                </td>
            </tr>
        );
    }
}

var CreateWin = React.createClass({
    save() {
        common.req("create.json", {name:this.refs.templateName.value, code:this.refs.templateCode.value}, r => {
            this.props.parent.edit(r);
        });
    },
    render() {
        return (
            <div className="modal fade" id="createWin" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 className="modal-title" id="myModalLabel">
                                新的模板
                            </h4>
                        </div>
                        <div className="modal-body">
                            模板名称：<input ref="templateName" className="form-control"/>
                            <br/>
                            模板代码：<input ref="templateCode" className="form-control"/>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.save}>确定</button>
                            <button type="button" className="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

var ExportWin = React.createClass({
    getInitialState() {
        return {text: ""};
    },
    render() {
        return (
            <div className="modal fade" id="exportWin" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 className="modal-title" id="myModalLabel">
                                JSON
                            </h4>
                        </div>
                        <div className="modal-body">
                            <textarea ref="templateName" className="form-control" style={{height:"400px"}} value={this.state.text}></textarea>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(
        <TemplateList/>, document.getElementById("content")
    );
});
