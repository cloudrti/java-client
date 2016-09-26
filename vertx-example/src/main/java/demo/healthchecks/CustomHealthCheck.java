package demo.healthchecks;

import com.cloudrti.client.api.healthchecks.Probe;
import com.cloudrti.client.api.healthchecks.ProbeData;

public class CustomHealthCheck implements Probe {
    @Override
    public ProbeData check() {
        return ProbeData.newBuilder().healthy(true).build();
    }

    @Override
    public String getName() {
        return "Demo health check";
    }
}
