var metricsService = require("vertx-dropwizard-js/metrics_service");
vertx.deployVerticle("maven:io.vertx:processmon:1.0-SNAPSHOT::io.vertx.processmon");

var service = metricsService.create(vertx);

vertx.eventBus().localConsumer("processmon", function(msg) {
  var snapshot = service.getMetricsSnapshot(vertx);
  var process = msg.body();
  var metrics = {};
  metrics[process.pid] = {
    CPU: process.cpu,
    Mem: process.mem
  };
  vertx.eventBus().publish("metrics", metrics);
});


