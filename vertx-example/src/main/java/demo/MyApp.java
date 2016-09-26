package demo;

import com.cloudrti.client.vertx.CloudRtiVerticle;
import com.cloudrti.client.vertx.sensors.VmStatsVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;

/**
 * Created by paulb on 02/05/16.
 */
public class MyApp {

    public static final VertxOptions DROPWIZARD_OPTIONS = new VertxOptions().
            setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true)
                    .setRegistryName("my-registry")
                    .addMonitoredHttpServerUri(new Match().setValue("/.*").setType(MatchType.REGEX)));

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(DROPWIZARD_OPTIONS);

        vertx.deployVerticle(new WebVerticle());
        vertx.deployVerticle(new CloudRtiVerticle());
        vertx.deployVerticle(new VmStatsVerticle());
    }

}