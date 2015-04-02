import io.vertx.groovy.ext.apex.Router;
import io.vertx.groovy.ext.apex.handler.sockjs.SockJSHandler;
import io.vertx.groovy.ext.apex.handler.StaticHandler;
import java.util.concurrent.TimeUnit;

def router = Router.router(vertx)

router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge([
outboundPermitteds: [[address: "dashboard"]]]))

// Serve the static resources
router.route().handler(StaticHandler.create())

def httpServer = vertx.createHttpServer()
httpServer.requestHandler(router.&accept).listen(8080)

def dashboard = [:]
    
vertx.eventBus().consumer("metrics") { msg ->
  def metrics = msg.body();
  dashboard[metrics.id] = [
    CPU: metrics.jvm.process.cpu.percent,
    mem: metrics.jvm.process.mem.size,
    published: metrics.vertx["vertx.eventbus.messages.published"].throughput
  ];
};

vertx.setPeriodic(1000) {
  vertx.eventBus().publish("dashboard", dashboard)
}

/*
// RxGroovy version
def observable = vertx.eventBus().consumer("metrics").bodyStream().toObservable();
observable.
    buffer(1, TimeUnit.SECONDS).
    map({ dashboard -> dashboard.collectEntries { metrics ->  [metrics.id, [
        CPU: metrics.jvm.process.cpu.percent,
        mem: metrics.jvm.process.mem.size,
        published: metrics.vertx["vertx.eventbus.messages.published"].throughput
    ]] }
    }).
    subscribe() { dashboard ->
      vertx.eventBus().publish("dashboard", dashboard)
    }
*/
