import io.vertx.groovy.ext.apex.Router;
import io.vertx.groovy.ext.apex.handler.sockjs.SockJSHandler;
import io.vertx.groovy.ext.apex.handler.StaticHandler;


def router = Router.router(vertx);

router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge([
outboundPermitteds: [[address: "metrics"]]]));

// Serve the static resources
router.route().handler(StaticHandler.create());

def httpServer = vertx.createHttpServer();
httpServer.requestHandler(router.&accept).listen(8080);
    
vertx.eventBus().consumer("metrics") { msg -> 
  System.out.println("got metrics from ${msg.body().node}");
};