package demo;

import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by paulb on 06/05/16.
 */
public class Videos  {
    private final Logger logger = LoggerFactory.getLogger(Videos.class);


    public void list(RoutingContext r) {
        logger.info("testing logs");
        logger.warn("testing log warn");

        r.response().end("videos");
    }
}
