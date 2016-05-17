package com.cloudrti.client.vertx.handlers;

import com.cloudrti.client.api.snapshots.Snapshot;
import com.cloudrti.client.api.snapshots.SnapshotDescription;
import com.cloudrti.client.vertx.snapshots.ThreadDumpSnapshot;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by paulb on 10/05/16.
 */
public class SnapshotHandlers {
    private final Set<Snapshot> snapshots = new HashSet<>();
    private final List<SnapshotDescription> snapshotDescriptions;
    private final Vertx vertx;

    public SnapshotHandlers(Vertx vertx) {
        this.vertx = vertx;
        snapshots.add(new ThreadDumpSnapshot());

        ServiceLoader<Snapshot> services = ServiceLoader.load(Snapshot.class);
        services.forEach(snapshots::add);

        snapshotDescriptions = snapshots.stream()
                .map(s -> new SnapshotDescription(s.getName(), s.getDescription(), s.getDataType()))
                .collect(Collectors.toList());
    }

    public void handleList(RoutingContext r) {
        r.response().end(Json.encode(snapshotDescriptions));
    }

    public void handleSnapshot(RoutingContext r) {
        String snapshotname = r.request().getParam("snapshotname");

        vertx.executeBlocking(future -> {
            try {
                Optional<Snapshot> snapshot = snapshots.stream()
                        .filter(s -> s.getName().equals(snapshotname)).findAny();

                if(snapshot.isPresent()) {
                    Map<String, Object> data = snapshot.get().getData();
                    future.complete(data);
                } else {
                    future.fail("Snapshot " + snapshotname + " not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                future.fail(e);
            }
        }, res -> {
            if(res.failed()) {
                r.response().setStatusCode(500).setStatusMessage(res.cause().getMessage()).end();
            } else {
                r.response().end(Json.encode(res.result()));
            }
        });
    }
}
