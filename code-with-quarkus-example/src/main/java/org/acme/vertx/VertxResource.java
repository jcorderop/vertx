package org.acme.vertx;


import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/vertx")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class VertxResource {

    private static final Logger logger = LoggerFactory.getLogger(VertxResource.class);

    private final Vertx vertx;
    private final WebClient clientWeb;

    @Inject
    public VertxResource(Vertx vertx) {
        this.vertx = vertx;
        this.clientWeb = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080));
    }

    @GET
    public Uni<JsonArray> get() {
        logger.info("Get using vertx...");
        final var items = new JsonArray();
        items.add(new JsonObject().put("id", 1));
        items.add(new JsonObject().put("id", 2));
        items.add(new JsonObject().put("id", 3));
        return Uni.createFrom().item(items);
    }

    @GET
    @Path("/users")
    public Uni<JsonArray> getFromUsers() {
        logger.info("Get using vertx from the endpoint /users ...");
        return clientWeb.get("/users").send()
                .onItem().transform(HttpResponse::bodyAsJsonArray);
    }

}
