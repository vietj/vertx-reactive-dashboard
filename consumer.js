vertx.eventBus().consumer("processmon", function(msg) {
  var body = msg.body();
  console.log(JSON.stringify(body));
});
