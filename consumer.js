vertx.eventBus().consumer("metrics", function(msg) {
  var metrics = msg.body();
  console.log(JSON.stringify(metrics));
});
