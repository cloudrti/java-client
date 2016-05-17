package com.cloudrti.client.vertx.probes;

import com.cloudrti.client.api.healthchecks.Probe;
import com.cloudrti.client.api.healthchecks.ProbeData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;

import java.util.HashMap;
import java.util.Map;

public class StartupProbe implements Probe {

    private final Vertx vertx;
    private boolean started = false;
    private final MetricsService metricsService;


    public StartupProbe(Vertx vertx) {
        this.vertx = vertx;
        metricsService = MetricsService.create(vertx);

        vertx.eventBus().consumer("startup", m -> {
           if(m.body().equals("true")) {
               started = true;
           };
        });
    }

    @Override
    public ProbeData check() {
        JsonObject metricsSnapshot = metricsService.getMetricsSnapshot("vertx.verticles");

        Map<String,Object> details = new HashMap<>();
        metricsSnapshot.fieldNames().forEach(f -> details.put(f, metricsSnapshot.getValue(f)));

        return new ProbeData.Builder().healthy(started).details(details).build();
    }

    @Override
    public String getName() {
        return "startup";
    }
}
