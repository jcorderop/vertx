package org.acme.vertx;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@ApplicationScoped
public class PeriodicUserFetch extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicUserFetch.class);

    public static String PUBLISH_ADDRESS = PeriodicUserFetch.class.getName();

    @Override
    public Uni<Void> asyncStart() {
        var clientWeb = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080));

        vertx.periodicStream(Duration.ofSeconds(5).toMillis())
                .toMulti()
                .subscribe()
                .with(aLong -> {
                    logger.info("Fetch all users!!!");
                    clientWeb.get("/users").send()
                            .subscribe().with(result -> {
                                var body = result.bodyAsJsonArray();
                                logger.info("Publishing messages on the EventBus...");
                                vertx.eventBus().publish(PUBLISH_ADDRESS,body);
                            });
                });

        return Uni.createFrom().voidItem();
    }
}
