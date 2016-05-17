package com.cloudrti.client.api.healthchecks;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents system health at a given moment, based on {@link Probe} found in the system.
 */
public class ProbeEvent {
    private Set<ProbeData> data = new HashSet<>();

    public ProbeEvent() {
    }

    public Set<ProbeData> getData() {
        return data;
    }

    public void addData(ProbeData healthCheckData) {
        data.add(healthCheckData);
    }

    public boolean isHealthy() {
        return !data.stream()
                .filter(d -> d.isHealthy() != null)
                .filter(d -> !d.isHealthy()).findAny().isPresent();
    }

    @Override
    public String toString() {
        return "ProbeEvent{" +
                "data=" + data +
                ", m_healthy=" + isHealthy() +
                '}';
    }
}