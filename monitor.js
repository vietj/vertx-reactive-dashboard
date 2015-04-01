var metricsService = require("vertx-dropwizard-js/metrics_service");
vertx.deployVerticle("maven:io.vertx:vertx-sigar:1.0-SNAPSHOT::io.vertx.vertx-sigar");

var service = metricsService.create(vertx);

var nodeId = java.util.UUID.randomUUID().toString();

vertx.eventBus().consumer("vertx.sigar", function(msg) {
  var snapshot = service.getMetricsSnapshot(vertx.eventBus());
  vertx.eventBus().publish("metrics", {
    "node": nodeId,
    "eventBus" : snapshot,
    "processes" : msg.body()
  });
});

console.log("Started " + nodeId);


