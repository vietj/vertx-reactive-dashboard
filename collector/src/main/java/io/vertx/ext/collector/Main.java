package io.vertx.ext.collector;

import io.vertx.core.Vertx;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

/*
    ShellService service = ShellService.create(vertx,
        new ShellServiceOptions().
            setTelnetOptions(new TelnetTermOptions().setPort(5000)));
    service.start();

    Command cmd = CommandBuilder.command(CLI.create("collector").addArgument(new Argument().setIndex(0).setArgName("period"))).
        processHandler(process -> {

          process.interruptHandler(v -> {
            process.end();
          });

        }).
        completionHandler(completion -> {
          completion.complete("toto", true);
        }).
        build(vertx);
    CommandRegistry.getShared(vertx).registerCommand(cmd);
*/

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
