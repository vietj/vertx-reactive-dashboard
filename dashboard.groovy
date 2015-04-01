import io.vertx.groovy.ext.apex.Router;
import io.vertx.groovy.ext.apex.handler.sockjs.SockJSHandler;
import io.vertx.groovy.ext.apex.handler.StaticHandler;


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
  dashboard[metrics.id] = metrics;
};

vertx.setPeriodic(1000) {
  vertx.eventBus().publish("dashboard", dashboard.values() as List)
}