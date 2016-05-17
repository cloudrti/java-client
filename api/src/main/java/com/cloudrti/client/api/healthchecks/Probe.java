package com.cloudrti.client.api.healthchecks;

public interface Probe {
    /**
     * @return The health check data itself. Should provide context information about the error.
     */
    ProbeData check();

    /**
     * @return Name of the health check. Should be unique in the system, but this is not enforced.
     */
    String getName();

    /**
     * Optional method to recover from an unhealthy state. This may be implemented if a known solution is
     * available for a given error state.
     */
    default void recover() {}
}