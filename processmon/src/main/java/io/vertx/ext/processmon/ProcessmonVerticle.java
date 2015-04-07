package io.vertx.ext.processmon;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.lang.management.ManagementFactory;
import java.util.UUID;

public class ProcessmonVerticle extends AbstractVerticle {

  long timer;

  @Override
  public void start() throws Exception {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    int index = name.indexOf('@');
    String pid = index > -1 ? name.substring(0, index) : UUID.randomUUID().toString();
    OperatingSystemMXBean systemMBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    String publishAddress = context.config().getString("address", "processmon");
    timer = vertx.setPeriodic(1000, id -> {
      JsonObject metrics = new JsonObject();
      metrics.put("pid", pid);
      metrics.put("cpu", systemMBean.getProcessCpuLoad());
      metrics.put("mem", systemMBean.getTotalPhysicalMemorySize() - systemMBean.getFreePhysicalMemorySize());
      vertx.eventBus().publish(publishAddress, metrics);
    });
  }

  @Override
  public void stop() throws Exception {
    vertx.cancelTimer(timer);
  }
}
