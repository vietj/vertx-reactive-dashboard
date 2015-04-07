# Reactboard

This demo shows how Vert.x can be used to create a monitoring Dashboard using Vert.x and reactive concepts.

# Process monitoring

The _processmon_ folder contains a simple Verticle that measures a couple of simple metrics of the JVM process and
publish a JSON object on the event bus:

```
{
   pid : 1234, // The process id
   cpu: 0.4,   // The measured CPU load
   mem: 15394  // The measured Mem
}
````

The projects has a Main class to test the Verticle in the IDE and see the Verticle in action.

The verticle can be installed in a Maven repository with `mvn install` . It can be executed from the command line
or deployed by another verticle:

````vertx run maven:io.vertx:processmon:1.0-SNAPSHOT::io.vertx.processmon -cluster
````

The _-cluster_ option will publish the events in a Vert.x cluster, the following JavaScript subscribes to the events
and print them on the console:

```
vertx.eventBus().consumer("processmon", function(msg) {
  var metrics = msg.body();
  console.log(metrics.pid + " CPU:" + metrics.cpu + " MEM:" + metrics.mem);
});
````

execute with `ertx run consume.js -cluster`

It can also be deployed in the same script locally:

```
vertx.deployVerticle("maven:io.vertx:processmon:1.0-SNAPSHOT::io.vertx.processmon");
vertx.eventBus().consumer("processmon", function(msg) {
  var metrics = msg.body();
  console.log(metrics.pid + " CPU:" + metrics.cpu + " MEM:" + metrics.mem);
});
````

In this use case, the _-cluster_ option is not necessary anymore.

# Monitor

The dashboard is a client webapp getting dashboard events via the eventbus bridge with the JSON structure:

```
{
  $id: {
    $metric_name : $metric_value
  }
}
````

The script dashboard.js creates a simple version of the dashboard for the current process.

````
vertx run dashboard.js
````

#


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

## Fatjaring monitor.js

The _service_ Maven project creates a fatjar wrapping `monitor.js` and its dependencies, run it with:

```
java -Dvertx.metrics.options.enabled=true -jar target/my-service-1.0-SNAPSHOT-fatjar.jar
```

Note: it cannot be run with custom cluster configuration at the moment