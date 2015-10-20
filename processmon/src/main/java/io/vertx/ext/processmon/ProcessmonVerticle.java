package io.vertx.ext.processmon;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.lang.management.ManagementFactory;
import java.util.UUID;

public class ProcessmonVerticle extends AbstractVerticle {

  long timerId;

  long period;
  String pid;
  OperatingSystemMXBean systemMBean;
  String publishAddress;

  @Override
  public void start() throws Exception {

    String name = ManagementFactory.getRuntimeMXBean().getName();
    int index = name.indexOf('@');
    pid = index > -1 ? name.substring(0, index) : UUID.randomUUID().toString();
    systemMBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    publishAddress = context.config().getString("address", "processmon");
    period = config().getInteger("period", 1000);

    // Start monitoring
    startMon();
  }

  void startMon() {
    if (timerId > 0) {
      vertx.cancelTimer(timerId);
    }
    timerId = vertx.setPeriodic(period, id -> {
      JsonObject metrics = new JsonObject();
      metrics.put("pid", pid);
      metrics.put("cpu", systemMBean.getProcessCpuLoad());
      metrics.put("mem", systemMBean.getTotalPhysicalMemorySize() - systemMBean.getFreePhysicalMemorySize());
      vertx.eventBus().publish(publishAddress, metrics);
    });
  }

  @Override
  public void stop() throws Exception {
    vertx.cancelTimer(timerId);
  }
}
