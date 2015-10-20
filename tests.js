
var TestSuite = require('vertx-unit-js/test_suite');
var suite = TestSuite.create("the_suite");

suite.before(function(context) {
  var async = context.async();
  vertx.deployVerticle("maven:io.vertx:processmon:1.0-SNAPSHOT", function(id,err) {
    if (id != null) {
      async.complete();
    } else {
      context.fail(err);
    }
  });
});

suite.test("the_test", function (context) {
  var async = context.async();
  var consumer = vertx.eventBus().localConsumer("processmon");
  consumer.handler(function(msg) {
    var process = msg.body();
    context.assertTrue(process.pid !== undefined && process.pid !== null);
    context.assertTrue(process.cpu !== undefined && process.cpu !== null);
    context.assertTrue(process.mem !== undefined && process.mem !== null);
    async.complete();
    consumer.unregister();
  });
});

suite.run(vertx);
