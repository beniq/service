var List = React.createClass({
    getInitialState() {
        return {content:{list:[], total:0}};
    },
    componentDidMount() {
        this.refresh();
    },
    page(i) {
        let env = this.props.env;
        env.from = i * env.number;
        if (env.from < 0)
            env.from = 0;
        else if (env.from >= env.total)
            env.from = env.from - env.number;
        this.refresh();
    },
    buildPageComponent() {
        let page = [];
        let env = this.props.env;
        env.total = this.state.content.total;
        for (var i=0;i<env.total/10;i++) {
            page.push(<button type="button" className="btn btn-primary" onClick={this.page.bind(this, i)}>{i+1}</button>);
        }
        return (
            <div className="bottom">
                <button type="button" className="btn btn-primary" onClick={this.page.bind(this, env.from / env.number - 1)}>&lt;&lt;</button>
                {page}
                <button type="button" className="btn btn-primary" onClick={this.page.bind(this, env.from / env.number + 1)}>&gt;&gt;</button>
            </div>
        );
    },
    render() {
        return (
            <div className="list">
                <br/>
                <div className="container-fluid">
                    { this.buildConsole() }
                </div>
                <br/>
                <table className="bordered">
                    <thead>{ this.buildTableTitle() }</thead>
                    <tbody>{ this.state.content.list.map(v => this.buildTableLine(v)) }</tbody>
                </table>
                <br/>
                { this.buildPageComponent() }
                <CreateWin ref="win" parent={this}/>
            </div>
        );
    }
});

class TemplateList extends List {
    create() {
    }
    test(templateId) {
        document.location.href = "test.html?templateId=" + templateId;
    }
    edit(templateId) {
        document.location.href = "edit.html?templateId=" + templateId;
    }
    refresh() {
        common.req("list.json", {}, r => {
            this.setState({content:r});
        });
    }
    buildConsole() {
        return (
            <div className="container-fluid">
                <div className="collapse navbar-collapse">
                    <div className="nav navbar-nav navbar-right">
                        <a className="btn btn-primary" data-toggle="modal" data-target="#createWin" onClick={this.create}>＋ 新的模板</a>
                    </div>
                </div>
            </div>
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
                <td>
                    <button type="button" className="btn btn-primary" onClick={this.edit.bind(this, v.id)}>编辑</button>
                    &nbsp;&nbsp;
                    <button type="button" className="btn btn-primary" onClick={this.test.bind(this, v.id)}>测试</button>
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

$(document).ready( function() {
    ReactDOM.render(
        <TemplateList env={env}/>, document.getElementById("content")
    );
});
