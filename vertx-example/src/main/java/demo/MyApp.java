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

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new WebVerticle());
        vertx.deployVerticle(new CloudRtiVerticle());
        vertx.deployVerticle(new VmStatsVerticle());
    }

}