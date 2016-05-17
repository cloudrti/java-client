package com.cloudrti.client.vertx.handlers;

import com.cloudrti.client.api.healthchecks.Probe;
import com.cloudrti.client.api.healthchecks.ProbeData;
import com.cloudrti.client.api.healthchecks.ProbeEvent;
import com.cloudrti.client.vertx.probes.StartupProbe;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Created by paulb on 10/05/16.
 */
public class ProbeHandlers {
    private final Set<Probe> probes = new HashSet<>();
    private final Vertx vertx;
    private final static Logger logger = LoggerFactory.getLogger(ProbeHandlers.class);

    public ProbeHandlers(Vertx vertx) {
        this.vertx = vertx;

        probes.add(new StartupProbe(vertx));

        ServiceLoader<Probe> services = ServiceLoader.load(Probe.class);
        services.forEach(probes::add);
    }

    public void handleHealthCHeck(RoutingContext r) {
        vertx.executeBlocking(future -> {
            try {
                future.complete(getProbeInfo());
            } catch (Exception e) {
                future.fail(e);
            }
        }, res -> {
            if(res.failed()) {
                res.cause().printStackTrace();
                r.response().setStatusCode(500).setStatusMessage(res.cause().getMessage()).end();
            } else {
                r.response().end(Json.encode(res.result()));
            }
        });


    }

    private ProbeEvent getProbeInfo() {
        ProbeEvent event = new ProbeEvent();


        probes.parallelStream().forEach(probe -> {
            try {
                System.out.println(probe);
                ProbeData probeData = probe.check();
                if(probeData != null) {
                    probeData.setName(probe.getName());

                    event.addData(probeData);

                    if(probeData.isHealthy() != null && !probeData.isHealthy()) {
                        probe.recover();
                    }
                } else {
                    logger.warn("Null value while requesting for probe data from probe {}", probe.getName());
                }
            } catch (Exception e) {
                logger.error("Exception while requesting for probe data from probe {}: {}", probe.getName(), e.getMessage());
            }
        });

        return event;
    }
}
