package io.vertx.ext.processmon;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ProcessmonVerticle extends AbstractVerticle {

  long id;

  @Override
  public void start() throws Exception {
    OperatingSystemMXBean systemMBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    String publishAddress = context.config().getString("address");
    id = vertx.setPeriodic(1000, id -> {
      JsonObject metrics = new JsonObject();
      metrics.put("cpu", systemMBean.getProcessCpuLoad());
      metrics.put("mem", systemMBean.getTotalPhysicalMemorySize() - systemMBean.getFreePhysicalMemorySize());
      vertx.eventBus().publish(publishAddress, metrics);
    });
  }

  @Override
  public void stop() throws Exception {
    vertx.cancelTimer(id);
  }
}
