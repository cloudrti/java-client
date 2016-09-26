package com.cloudrti.client.vertx;

import com.cloudrti.client.vertx.handlers.FlightControlHandlers;
import com.cloudrti.client.vertx.handlers.ProbeHandlers;
import com.cloudrti.client.vertx.handlers.SnapshotHandlers;
import com.cloudrti.client.vertx.handlers.VersionHandler;
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
import rx.schedulers.Schedulers;

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

        FlightControlHandlers flightControlHandlers = new FlightControlHandlers(vertx);
        router.route("/flightcontrols").handler(flightControlHandlers::handleListFlightControls);
        router.route("/flightcontrol/:name").handler(flightControlHandlers::handleExecute);


        VersionHandler versionHandler = new VersionHandler(vertx);
        router.route("/version").handler(versionHandler::handleVersion);
        versionHandler.getVersionInfo().subscribeOn(Schedulers.io()).subscribe(versionInfo -> {
            String msg = "Cloud RTI client lib version: " + versionInfo.getString("version");
            logger.info(msg);

            String hint = versionInfo.getString("hint");
            if(hint != null) {
                if(hint.contains("You are on the latest")) {
                    logger.info(versionInfo.getString("hint"));
                } else {
                    logger.warn(versionInfo.getString("hint"));
                }
            }
        });

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(9999, r -> {
                    System.out.println("Cloud RTI listening on port 9999");
                    startFuture.complete();
                });


    }

    private String getProperty(String name) {
        String value = System.getProperty(name);
        if(value == null) {
            value = System.getenv(name);
        }

        return value;
    }
}
