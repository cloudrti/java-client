package com.cloudrti.client.vertx.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class VersionHandler {

    private final String VERSION = "1.1.0";

    private final Vertx vertx;
    private final static Logger logger = LoggerFactory.getLogger(VersionHandler.class);

    public VersionHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void handleVersion(RoutingContext r) {
        getVersionInfo().subscribeOn(Schedulers.io()).subscribe(versionInfo -> {
            r.response().end(versionInfo.encode());
        });
    }

    public Observable<JsonObject> getVersionInfo() {
        JsonObject versionInfo = new JsonObject();
        versionInfo.put("version", VERSION);

        return Observable.create(observer -> {
            HttpClient client = vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true));
            client.getNow(443, "api.bintray.com", "/packages/cloud-rti/maven/cloud-rti-client/versions/_latest", r -> {
                if(r.statusCode() == 200) {
                    r.bodyHandler(body -> {
                        JsonObject jsonObject = body.toJsonObject();
                        String versionName = jsonObject.getString("name");
                        Version version = new Version(versionName);
                        versionInfo.put("availableVersion", version.toString());

                        if(version.compareTo(new Version(VERSION)) > 0) {
                            String msg = "New Cloud RTI client library availabel: " + version.toString() + ". Download the new bundle here: https://bintray.com/cloud-rti/maven/cloud-rti-client";
                            logger.warn(msg);
                            System.err.println(msg);

                            versionInfo.put("hint", msg);
                            versionInfo.put("latest", false);
                        } else {
                            versionInfo.put("hint", "You are on the latest Cloud RTI client lib version");
                            versionInfo.put("latest", true);
                        }

                        observer.onNext(versionInfo);
                        observer.onCompleted();
                    });
                } else {
                    versionInfo.put("hint", "Could not retrieve latest available version");
                    observer.onNext(versionInfo);
                    observer.onCompleted();
                }

            });
        });
    }
}
