var metricsService = require("vertx-dropwizard-js/metrics_service");
vertx.deployVerticle("maven:io.vertx:vertx-sigar:1.0-SNAPSHOT::io.vertx.vertx-sigar");

var service = metricsService.create(vertx);

var id = java.util.UUID.randomUUID().toString();

vertx.eventBus().localConsumer("vertx.sigar", function(msg) {
  var snapshot = service.getMetricsSnapshot(vertx);
  vertx.eventBus().publish("metrics", {
    "id": id,
    "vertx" : snapshot,
    "jvm" : msg.body()
  });
});

console.log("Started " + id);


