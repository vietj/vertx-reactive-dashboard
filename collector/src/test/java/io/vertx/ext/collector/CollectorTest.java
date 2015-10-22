package io.vertx.ext.collector;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CollectorTest {

  Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    vertx.deployVerticle(
        new CollectorVerticle()
    );
  }

  @Test
  public void theTest(TestContext context) {
    Async async = context.async();
    MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("metrics");
    consumer.handler(msg -> {
      JsonObject metrics = msg.body();
      context.assertNotNull(metrics.getValue("pid"));
      context.assertNotNull(metrics.getValue("cpu"));
      context.assertNotNull(metrics.getValue("mem"));
      consumer.unregister();
      async.complete();
    });
  }

  @After
  public void tearDown() {
    vertx.close();
  }
}
