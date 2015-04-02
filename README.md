# Demo

Metrics aggregation to client

# How to run it

## Install the vertx-sigar Verticle

This verticle uses the sigar lib to publish metrics event on the bus

## Run one or several monitors

```
export VERTX_OPTS="-Dvertx.metrics.options.enabled=true"
vertx run monitor.js -cluster
```

or if you have multicast issues (because of VPN like me)

```
export VERTX_OPTS="-Dvertx.metrics.options.enabled=true"
vertx run monitor.js -cluster -cluster-host 192.168.0.106
```

This script publish an aggregation of the Sigar metrics + Vert.x metrics on the event bus to the `metrics` address

## Run the dashboard server

```
vertx run dashboard.groovy -cluster
```

or...

```
vertx run dashboard.groovy -cluster -cluster-host 192.168.0.106
```

It subscribes to the `metrics` address and aggregates the metrics in a dashboard sent to the client via
the event bus bridge. The client gets the metrics with a nice dynamic dashboard.