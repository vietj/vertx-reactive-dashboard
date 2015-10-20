vertx.deployVerticle("maven:io.vertx:processmon:1.0-SNAPSHOT");

vertx.eventBus().localConsumer("processmon", function(msg) {
  var process = msg.body();
  var metrics = {};
  metrics[process.pid] = {
    CPU: process.cpu,
    Mem: process.mem
  };
  vertx.eventBus().publish("metrics", metrics);
});


