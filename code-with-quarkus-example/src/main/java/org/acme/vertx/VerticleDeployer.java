package org.acme.vertx;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class VerticleDeployer {

    private static final Logger logger = LoggerFactory.getLogger(VerticleDeployer.class);

    public void init(@Observes StartupEvent startupEvent, Vertx vertx, Instance<AbstractVerticle> verticles) {
        logger.info("Getting ready to deploy Verticles");
        verticles.forEach(v -> {
            vertx.deployVerticle(v).await().indefinitely();
        });
    }
}
