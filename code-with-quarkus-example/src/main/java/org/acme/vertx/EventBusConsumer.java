package org.acme.vertx;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventBusConsumer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicUserFetch.class);

    @Override
    public Uni<Void> asyncStart() {
        vertx.eventBus().consumer(PeriodicUserFetch.PUBLISH_ADDRESS,
                message -> {
                    logger.info("Message from EventBus: {}",message.body());
                });
        return Uni.createFrom().voidItem();
    }
}
