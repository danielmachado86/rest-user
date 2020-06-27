package io.dmcapps.dshopping.user;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;

import io.dmcapps.dshopping.user.client.AuthService;
import io.dmcapps.dshopping.user.client.AuthToken;
import io.dmcapps.dshopping.user.client.AuthUser;

@Path("/api/users")
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(
        UserResource.class);

    @Inject
    @RestClient
    AuthService authService;
    
    @Inject
    UserService service;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "auth-api.grant_type")
    String grantType;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String secret;

    @GET
    @Path("/me")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public Response getUser() {
        LOGGER.info("trying to create user");
        String id = jwt.getSubject();
        LOGGER.info(id);
        User user = service.findUserById(id);
        if (user != null) {
            LOGGER.debug("Found user " + user);
            return Response.ok(user).build();
        } else {
            LOGGER.debug("No user found with id " + id);
            return Response.noContent().build();
        }
    }

    @POST
    public Response createUser(
        User user, @Context UriInfo uriInfo) {
        String authorizationHeader = createBasicAuthHeaderValue(clientId, secret);
        AuthToken token = authService.getToken(authorizationHeader, grantType);
        LOGGER.info(token.access_token);
        AuthUser authUser = new AuthUser(user);
        Response response = authService.createUser("Bearer " + token.access_token, authUser);
        user.id = response.getLocation().toString().split("/")[8];
        user = service.persistUser(user);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(user.id.toString());
        LOGGER.debug("New user created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    private String createBasicAuthHeaderValue(String clientId, String secret) {
        String headerValue = null;
        try {
			headerValue = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
        }
        return "Basic " + headerValue;
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
}