package com.cloudrti.client.vertx.handlers;

import com.cloudrti.client.api.flightcontrols.FlightControl;
import com.cloudrti.client.api.flightcontrols.FlightControlDescription;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FlightControlHandlers {
    private final Set<FlightControl> flightControls = new HashSet<>();
    private final Vertx vertx;
    private final static Logger logger = LoggerFactory.getLogger(FlightControlHandlers.class);

    public FlightControlHandlers(Vertx vertx) {
        this.vertx = vertx;

        ServiceLoader<FlightControl> services = ServiceLoader.load(FlightControl.class);
        services.forEach(flightControls::add);
    }

    public void handleListFlightControls(RoutingContext r) {
        List<FlightControlDescription> descriptions = flightControls.stream().map(FlightControlDescription::fromFlightControl).collect(Collectors.toList());

        r.response().end(Json.encode(descriptions));
    }


    public void handleExecute(RoutingContext routingContext) {
        ServerWebSocket ws = routingContext.request().upgrade();

        ws.handler(buff -> {
            String json = buff.toString();

            executeFlightControl(json, routingContext.pathParam("name")).subscribeOn(Schedulers.computation()).subscribe(s -> {
                        ws.writeFinalTextFrame(s);
                    }, error -> {
                        ws.writeFinalTextFrame("Error: " + error.getMessage());
                    },
                    () -> ws.end());
        });
    }

    private Observable<String> executeFlightControl(String json, String controlName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String,String> arguments = mapper.readValue(json, new TypeReference<Map<String,String>>() {});

            Optional<FlightControl> control = flightControls.stream().filter(c -> c.getName().equals(controlName)).findAny();
            if(!control.isPresent()) {
                return Observable.error(new IllegalArgumentException("Flightcontrol " + controlName + " not found"));
            }

            return control.get().execute(arguments);
        } catch (IOException e) {
            return Observable.error(e);
        }

    }
}
