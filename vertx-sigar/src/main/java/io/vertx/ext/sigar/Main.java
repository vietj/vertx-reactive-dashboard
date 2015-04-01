package io.vertx.ext.sigar;

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
        new SigarServiceVerticle(),
        new DeploymentOptions().setConfig(new JsonObject().put("address", "vertx.sigar")),
        deployment -> {
      if (deployment.succeeded()) {
        vertx.eventBus().<JsonObject>consumer("vertx.sigar", msg -> {
          System.out.println(msg.body());
        });
      } else {
        deployment.cause().printStackTrace();
      }
    });
  }

}
