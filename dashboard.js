var Router = require("vertx-apex-js/router");
var SockJSHandler = require("vertx-apex-js/sock_js_handler");
var StaticHandler = require("vertx-apex-js/static_handler");

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

vertx.deployVerticle("maven:io.vertx:vertx-sigar:1.0-SNAPSHOT::io.vertx.vertx-sigar");

var id = java.util.UUID.randomUUID().toString();

vertx.eventBus().localConsumer("vertx.sigar", function(msg) {
  var metrics = msg.body();
  var dashboard = {};
  dashboard[id] = {
    CPU: metrics.process.cpu.percent,
    Mem: metrics.process.mem.size
  };
  vertx.eventBus().publish("dashboard", dashboard);
});

console.log("Started " + id);


