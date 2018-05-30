
$(document).ready( function() {
    ENV.actId = common.param("actId");
    ReactDOM.render(<Main/>, document.getElementById("content"));

    setInterval(ENV.saveQueue, 10000);
});
