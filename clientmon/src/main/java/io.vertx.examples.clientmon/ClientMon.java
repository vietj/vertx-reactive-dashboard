package io.vertx.examples.clientmon;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ClientMon {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle("maven:io.vertx:collector:1.0-SNAPSHOT");
    JsonObject metrics = new JsonObject();
    vertx.eventBus().<JsonObject>consumer("metrics", msg -> {
      metrics.mergeIn(msg.body());
    });
    HttpServer server = vertx.createHttpServer();

    server.requestHandler(req -> {
      req.response().sendFile("clientmon/index.html");
    });

    server.websocketHandler(ws -> {
      long id = vertx.setPeriodic(1000, v -> {
        ws.writeFinalTextFrame(metrics.encode());
      });
      ws.closeHandler(v -> {
        vertx.cancelTimer(id);
      });

    });
    server.listen(8080, "localhost", ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
      }
    });
  }
}
