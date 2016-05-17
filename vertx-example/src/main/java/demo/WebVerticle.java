package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by paulb on 06/05/16.
 */
public class WebVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(WebVerticle.class);

    private HttpServer httpServer;

    @Override
    public void start(Future fut) {
        // Create an HTTP server...
        httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.mountSubRouter("/videos", videoRouter());
        logger.info("Startup!");

        httpServer.requestHandler(router::accept).listen(8080, s -> {
            vertx.eventBus().publish("startup", "true");
            fut.complete();
        });
    }

    private Router videoRouter() {
        Router videosRouter = Router.router(vertx);
        Videos videos = new Videos();
        videosRouter.get().handler(videos::list);
        return videosRouter;
    }

    @Override
    public void stop(Future fut) {
        httpServer.close( res -> fut.complete());
    }
}
