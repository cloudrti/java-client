# Cloud RTI Client

This repository contains the plain (non-OSGi) client libraries for Cloud RTI.

## Components

* api: API only, this contains interfaces such as `Probe` and `Snapshot`.
* logging: Kafka logger based on Logback. Usually used by the SLF4j API.
* vertx: Vert.x implementation for probes and metrics. This kind of implementation is required to make full use of Cloud RTI monitoring tools. When using a different framework, this implementation can be used as inspiration.

## Usage from Gradle

```
repositories {
    jcenter()
    maven {
        url  "http://dl.bintray.com/cloud-rti/maven"
    }
}

configurations.all {
    resolutionStrategy.failOnVersionConflict()

    resolutionStrategy.force 'org.slf4j:slf4j-api:1.7.21', 'com.fasterxml.jackson.core:jackson-databind:2.7.4', 'com.fasterxml.jackson.core:jackson-core:2.7.4'
}

dependencies {
    compile "io.vertx:vertx-mongo-client:3.2.1"
    compile "io.vertx:vertx-web:3.2.1"
    compile "org.slf4j:slf4j-api:1.7.21"
    compile "com.cloudrti.client:api:1.0.0"
    compile "com.cloudrti.client:vertx:1.0.0"
    runtime "com.cloudrti.client:logging:1.0.0"
}
```

## Logging

The logging component has a Logback configuration included that logs to Kafka. The configuration can be overriden by including a version in your own project.
The configuration assumes two environment properties to be set:

* kafka (e.g. kakfahost:9092)
* POD_NAMESPACE (e.g. mynamespace)

```
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- This is the kafkaAppender -->
    <appender name="kafkaAppender" class="com.github.danielwegener.logback.kafka.KafkaAppender">
        <!-- This is the default encoder that encodes every log message to an utf8-encoded string  -->
        <encoder class="com.github.danielwegener.logback.kafka.encoding.LayoutKafkaMessageEncoder">
            <layout class="com.cloudrti.vertx.logging.JsonFormat">
            </layout>
        </encoder>
        <topic>cloudrti.logging.${POD_NAMESPACE}</topic>
        <keyingStrategy class="com.github.danielwegener.logback.kafka.keying.RoundRobinKeyingStrategy" />
        <deliveryStrategy class="com.github.danielwegener.logback.kafka.delivery.AsynchronousDeliveryStrategy" />

        <!-- each <producerConfig> translates to regular kafka-client config (format: key=value) -->
        <!-- producer configs are documented here: https://kafka.apache.org/documentation.html#newproducerconfigs -->
        <!-- bootstrap.servers is the only mandatory producerConfig -->
        <producerConfig>bootstrap.servers=${kafka}</producerConfig>
        <producerConfig>block.on.buffer.full=false</producerConfig>
    </appender>

    <root level="info">
        <appender-ref ref="kafkaAppender" />
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

## Vertx

For monitoring and metrics to work, an agent is required. This is implemented for Vert.x and can easily be implemented for other frameworks as well.
To enable the agent in Vert.x you need to deploy the `CloudRtiVerticle`.

```
public class MyApp {

    public static final VertxOptions DROPWIZARD_OPTIONS = new VertxOptions().
            setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true)
                    .setRegistryName("my-registry")
                    .addMonitoredHttpServerUri(new Match().setValue("/.*").setType(MatchType.REGEX)));

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(DROPWIZARD_OPTIONS);

        vertx.deployVerticle(new WebVerticle()); // Your own Verticle
        vertx.deployVerticle(new CloudRtiVerticle()); // Cloud RTI Verticle
    }
}
```

Finally, you need to emit a value to the eventbus to indicate that the applicaiton is successfully started.

```
vertx.createHttpServer().requestHandler(router::accept).listen(8080, r -> {
  if(r.succeeded()) {
    startFuture.complete();
    System.out.println("Listening on port 8080");
    vertx.eventBus().publish("startup", "true");
  } else {
    startFuture.fail("Http server could not be started");
  }
});
```
