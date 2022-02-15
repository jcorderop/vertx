package org.acme;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ReactiveUserResource {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUserResource.class);

    @GET
    public Uni<List<User>> getUser() {
        logger.info("Get all the users...");
        return User.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{id}")
    public Uni<User> getById(Long id) {
        logger.info("Get by id: {}", id);
        return User.findById(id);
    }

    @POST
    public Uni<Response> create(User user) {
        logger.info("Request to create a new user: {}", user);
        return Panache.<User>withTransaction(user::persist)
                .onItem().transform(userInserted -> Response.created(URI.create("/users/" + userInserted.id)).build()
                );
    }
}