package io.vertx.ext.collector;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(
        new CollectorVerticle(),
        deployment -> {
      if (deployment.succeeded()) {
        vertx.eventBus().<JsonObject>consumer("metrics", msg -> {
          System.out.println(msg.body());
        });
      } else {
        deployment.cause().printStackTrace();
      }
    });
  }

}
