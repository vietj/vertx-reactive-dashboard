# Reactive monitoring dashboard

This demo shows how Vert.x can be used to create a monitoring Dashboard using Vert.x and reactive concepts.

# Metrics collection

The `collector` folder contains a simple Verticle that measures a couple of simple metrics of the JVM process and
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
vertx run maven:io.vertx:collector:1.0-SNAPSHOT -cluster
````

The _-cluster_ option will publish the events in a Vert.x cluster, the following JavaScript subscribes to the events
and print them on the console:

````
vertx.eventBus().consumer("metrics", function(msg) {
  var metrics = msg.body();
  console.log(metrics.pid + " CPU:" + metrics.cpu + " MEM:" + metrics.mem);
});
````

execute with `vertx run consumer.js -cluster`

It can also be deployed in the same script locally:

````
$vertx.deploy_verticle 'maven:io.vertx:collector:1.0-SNAPSHOT'
$vertx.event_bus.local_consumer 'metrics' do |msg|
  metrics = msg.body
  puts "#{metrics}"
end
````

In this use case, the _-cluster_ option is not needed.

# Dashboard

The dashboard is a client webapp getting dashboard events via the eventbus bridge with the JSON structure:

```
{
  $id: {
    $metric_name : $metric_value
  }
}
````

Start first the `dashboard.js`, it deploys the collector verticle that setup the event bus bridge to forward
the metrics events to the webapp.

Then, the script _dashboard.groovy_ is the same webapp that aggregates the cluster metrics events, those events
are published by the collector verticle, collectors must be run separately, the dashboard collects all metrics
via the distributed event bus:

Start one or several collectors:

````
vertx run maven:io.vertx:collector:1.0-SNAPSHOT -cluster
````

Then start the dashboard:

````
vertx run dashboard.groovy -cluster
````

# RxGroovy api

The _dashboard.groovy_ has a commented part that shows the usage of the RxGroovy API for aggregating dashboard.

# HA

The dashboard monitoring can be executed HA to show failover of the dashboard:

````
vertx run dashboard.groovy -cluster -ha
````

and

````
vertx -ha
````

then kill first node

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

# Shell

The ProcessmonVerticle can be extended with a custom shell command.

1/ in `pom.xml` add the dependency:

````
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-shell</artifactId>
  <version>3.1.0</version>
</dependency>
````

2/ in `start()` method add:

````
Command cmd = CommandBuilder.command(CLI.create("collector").addArgument(new Argument().setIndex(0).setArgName("period"))).
    processHandler(process -> {
      period = Integer.parseInt(process.commandLine().getArgumentValue(0));
      run();
      process.end();
    }).
    build();
CommandRegistry.get(vertx).registerCommand(cmd);
````

Note the command will be process on the Verticle event loop.

3/ finally start the ShellService, for instance in the `Main` class add:

````
ShellService service = ShellService.create(vertx, new ShellServiceOptions().setTelnetOptions(new TelnetOptions().setPort(5000)));
service.start(ar -> {
  if (ar.succeeded()) {
    System.out.println("Shell started");
  } else {
    ar.cause().printStackTrace();
  }
});
````

this is convenient because the service is already on the class path and the `ShellService` is available.


# Authentication

1/ put `user.admin = password` in `auth.properties` and configure the auth handler:

````
def authProvider = io.vertx.groovy.ext.auth.shiro.ShiroAuth.create(vertx, io.vertx.ext.auth.shiro.ShiroAuthRealmType.PROPERTIES, [properties_path:'auth.properties'])
def basicAuthHandler = io.vertx.groovy.ext.web.handler.BasicAuthHandler.create(authProvider)
router.route("/*").handler(basicAuthHandler);
````