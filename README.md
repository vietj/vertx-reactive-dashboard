# Reactive monitoring dashboard

This demo shows how Vert.x can be used to create a monitoring Dashboard using Vert.x and reactive concepts.

# Process monitoring

The _processmon_ folder contains a simple Verticle that measures a couple of simple metrics of the JVM process and
publish a JSON object on the event bus:

````
{
   pid : 1234, // The process id
   cpu: 0.4,   // The measured CPU load
   mem: 15394  // The measured Mem
}
````

The projects has a Main class to see the Verticle in action in the IDE or with the build:

````
mvn exec:java
````

or

````
gradle run
````

The verticle can be installed in a Maven repository with `mvn install` or with a Gradle script `gradle install`. It can
be executed from the command line or deployed by another verticle:

````
vertx run maven:io.vertx:processmon:1.0-SNAPSHOT -cluster
````

The _-cluster_ option will publish the events in a Vert.x cluster, the following JavaScript subscribes to the events
and print them on the console:

```
vertx.eventBus().consumer("processmon", function(msg) {
  var metrics = msg.body();
  console.log(metrics.pid + " CPU:" + metrics.cpu + " MEM:" + metrics.mem);
});
````

execute with `vertx run consumer.js -cluster`

It can also be deployed in the same script locally:

```
vertx.deployVerticle("maven:io.vertx:processmon:1.0-SNAPSHOT::io.vertx.processmon");
vertx.eventBus().consumer("processmon", function(msg) {
  var metrics = msg.body();
  console.log(metrics.pid + " -> CPU:" + metrics.cpu + " MEM:" + metrics.mem);
});
````

In this use case, the _-cluster_ option is not necessary anymore.

# Dashboard

The dashboard is a client webapp getting dashboard events via the eventbus bridge with the JSON structure:

```
{
  $id: {
    $metric_name : $metric_value
  }
}
````

The processmon JSON format is to the dashboard JSON format.

The script dashboard.js creates a simple version of the dashboard for the current process.

````
vertx run dashboard.js
````

The script _dashboard.groovy_ is a dashboard webapp that aggregates the cluster dashboard events, those events
are published by the _monitor.js_ script.

In both case the client side is the same, the difference is the aggregation is the second case.

# RxGroovy api

The _dashboard.groovy_ has a commented part that shows the usage of the RxGroovy API for aggregating dashboard.

# Testing

The _processmon_ verticle has a JUnit test, showing the Vert.x Unit API, the tests can be run either with
Maven or Gradle:

````
mvn test
````

or

````
gradle test
````

The _test.js_ script is a JavaScript unit test still using Vert.x Unit but using the polyglot API, it can be executed
directly from the CLI:

````
vertx test tests.js
````

# Todo

use react.js in the dashboard