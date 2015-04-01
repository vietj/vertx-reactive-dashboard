package io.vertx.ext.sigar;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SigarServiceVerticle extends AbstractVerticle {

  private Sigar sigar;
  private long id;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    String publishAddress = context.config().getString("address");
    sigar = new Sigar();
    id = vertx.setPeriodic(1000, id -> {
      JsonObject metrics = new JsonObject();
      try {
        long pid = sigar.getPid();
        ProcCpu cpu = sigar.getProcCpu(pid);
        metrics.put("process", toJson(pid, cpu));
      } catch (SigarException e) {
        e.printStackTrace();
      }
      vertx.eventBus().publish(publishAddress, metrics);
    });
    startFuture.complete();
  }

  @Override
  public void stop() throws Exception {
    vertx.cancelTimer(id);
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
  }

  private JsonObject toJson(long id, ProcCpu cpu) {
    return new JsonObject().
        put("id", id).
        put("total", cpu.getTotal()).
        put("sys", cpu.getSys()).
        put("startTime", cpu.getStartTime()).
        put("lastTime", cpu.getLastTime()).
        put("percent", cpu.getPercent()).
        put("user", cpu.getUser());
  }
}
