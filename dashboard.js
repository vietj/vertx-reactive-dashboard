var Router = require("vertx-web-js/router");
var SockJSHandler = require("vertx-web-js/sock_js_handler");
var StaticHandler = require("vertx-web-js/static_handler");

var router = Router.router(vertx);

var options = {
  "outboundPermitteds" : [
    {
      "address" : "dashboard"
    }
  ]
};
router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options).handle);

router.route().handler(StaticHandler.create().handle);
vertx.createHttpServer().requestHandler(router.accept).listen(8080);

vertx.deployVerticle("maven:io.vertx:collector:1.0-SNAPSHOT");

vertx.eventBus().localConsumer("metrics", function(msg) {
  var metrics = msg.body();
  var dashboard = {};
  dashboard[metrics.pid] = {
    CPU: metrics.cpu,
    Mem: metrics.mem
  };
  vertx.eventBus().publish("dashboard", dashboard);
});


