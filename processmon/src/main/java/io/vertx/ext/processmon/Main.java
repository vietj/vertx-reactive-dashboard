package io.vertx.ext.processmon;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(
        new ProcessmonVerticle(),
        new DeploymentOptions().setConfig(new JsonObject().put("address", "processmon")),
        deployment -> {
      if (deployment.succeeded()) {
        vertx.eventBus().<JsonObject>consumer("processmon", msg -> {
          System.out.println(msg.body());
        });
      } else {
        deployment.cause().printStackTrace();
      }
    });
  }

}
