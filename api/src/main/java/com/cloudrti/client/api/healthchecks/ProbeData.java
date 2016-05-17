package com.cloudrti.client.api.healthchecks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable class representing health state.
 * Create instances by using {@link Builder}
 */
public class ProbeData {

    // healthy Boolean representing the health of a component
    // May be null if no health status is calculated from this probe.
    private Boolean m_healthy;

    // details Untyped map containing optional context information for debugging an unhealthy component
    private Map<String, Object> m_details = new HashMap<>();

    private String m_name;
    private String m_visualisationId;

    public ProbeData() {
    }

    public Boolean isHealthy() {
        return m_healthy;
    }

    public Map<String, Object> getDetails() {
        return Collections.unmodifiableMap(m_details);
    }

    public void setHealthy(boolean healthy) {
        this.m_healthy = healthy;
    }

    public void setDetails(Map<String, Object> details) {
        this.m_details = details;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    @Override
    public String toString() {
        return "HealthCheckData [m_healthy=" + m_healthy + ", m_details="
                + m_details + "]";
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public String getVisualisationId() {
        return m_visualisationId;
    }

    public void setVisualisationId(String visualisationId) {
        m_visualisationId = visualisationId;
    }


    /**
     * Builder to create ProbeData instances
     */
    public static class Builder {

        private ProbeData m_healthCheckData;

        public Builder() {
            this.m_healthCheckData = new ProbeData();
        }

        /**
         * @param healthy Indicates if this health check passed
         * @return
         */
        public Builder healthy(boolean healthy) {
            m_healthCheckData.m_healthy = healthy;
            return this;
        }

        /**
         * @param name Name of the Probe
         * @return
         */
        public Builder name(String name) {
            m_healthCheckData.m_name = name;
            return this;
        }

        /**
         * Extra data to get insights in the status of the system
         * @param details An untyped map of extra data. May contain nested structures.
         * @return
         */
        public Builder details(Map<String, Object> details) {
            m_healthCheckData.m_details = details;
            return this;
        }

        /**
         * Optional visualisation type, if the data belongs to specific dashboard plugins. Not set for most health checks.
         * @param visualisationId
         * @return
         */
        public Builder visualisationId(String visualisationId) {
            m_healthCheckData.m_visualisationId = visualisationId;
            return this;
        }

        public ProbeData build() {
            if(m_healthCheckData.m_details == null) {
                m_healthCheckData.m_details = new HashMap<String, Object>();
            }
            ProbeData result = m_healthCheckData;
            m_healthCheckData = null;
            return result;
        }
    }
}