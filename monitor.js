var metricsService = require("vertx-dropwizard-js/metrics_service");
vertx.deployVerticle("maven:io.vertx:vertx-sigar:1.0-SNAPSHOT::io.vertx.vertx-sigar");

var service = metricsService.create(vertx);

vertx.eventBus().consumer("vertx.sigar", function(msg) {
  var abc = service.getMetricsSnapshot(vertx.eventBus());
  console.log(abc);
  vertx.eventBus().publish("metrics", {
    "eventBus" : abc,
    "processes" : msg.body()
  });
});