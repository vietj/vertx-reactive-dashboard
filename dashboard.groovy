import io.vertx.groovy.ext.web.Router;
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.groovy.ext.web.handler.StaticHandler;
import java.util.concurrent.TimeUnit;

def router = Router.router(vertx)

router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge([
outboundPermitteds: [[address: "dashboard"]]]))

// Serve the static resources
router.route().handler(StaticHandler.create().setCachingEnabled(false))

def httpServer = vertx.createHttpServer()
httpServer.requestHandler(router.&accept).listen(8080)

def dashboard = [:]
    
vertx.eventBus().consumer("metrics") { msg ->
  def metrics = msg.body();
  dashboard << metrics;
};

vertx.setPeriodic(1000) {
  println dashboard
  vertx.eventBus().publish("dashboard", dashboard)
}

/*
// RxGroovy version
def observable = vertx.eventBus().consumer("metrics").bodyStream().toObservable();
observable.
    buffer(1, TimeUnit.SECONDS).
    map({ dashboard -> dashboard.inject([:]) { result, i -> result << i }
    }).
    subscribe() { dashboard ->
      vertx.eventBus().publish("dashboard", dashboard)
    }
*/
