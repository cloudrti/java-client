package com.cloudrti.client.vertx;

import com.cloudrti.client.vertx.handlers.ProbeHandlers;
import com.cloudrti.client.vertx.handlers.SnapshotHandlers;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.SharedMetricRegistries;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import metrics_influxdb.InfluxdbReporter;
import metrics_influxdb.api.measurements.CategoriesMetricMeasurementTransformer;
import metrics_influxdb.api.protocols.InfluxdbProtocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by paulb on 10/05/16.
 */
public class CloudRtiVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(CloudRtiVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        SnapshotHandlers snapshotHandlers = new SnapshotHandlers(vertx);
        router.route("/snapshots").handler(snapshotHandlers::handleList);
        router.route("/snapshots/:snapshotname").handler(snapshotHandlers::handleSnapshot);

        ProbeHandlers probeHandlers = new ProbeHandlers(vertx);
        router.route("/health").handler(probeHandlers::handleHealthCHeck);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(9999, r -> {
                    System.out.println("Cloud RTI listening on port 9999");
                    startFuture.complete();
                });




        MetricRegistry registry = SharedMetricRegistries.getOrCreate("my-registry");

        String influxdb = getProperty("influxdb");
        if(influxdb != null && influxdb.length() > 0) {
            influxdb = influxdb.replaceAll("-", "_");

            final ScheduledReporter reporter = InfluxdbReporter.forRegistry(registry)
                    .protocol(InfluxdbProtocols.http(influxdb, 8086, getProperty("influxdbuser"), getProperty("influxdbpassword"), getProperty("POD_NAMESPACE")))
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .skipIdleMetrics(false)
                    .transformer(new CategoriesMetricMeasurementTransformer("module", "artifact"))
                    .build();
            reporter.start(10, TimeUnit.SECONDS);
        } else {
            logger.warn("Property `influxdb` not set. Metrics are disabled");
        }

    }

    private String getProperty(String name) {
        String value = System.getProperty(name);
        if(value == null) {
            value = System.getenv(name);
        }

        return value;
    }
}
