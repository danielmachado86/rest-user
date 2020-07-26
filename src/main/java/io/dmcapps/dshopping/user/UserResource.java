package io.dmcapps.dshopping.user;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dmcapps.dshopping.user.client.AuthService;
import io.dmcapps.dshopping.user.client.AuthToken;
import io.dmcapps.dshopping.user.client.AuthUser;

@SecurityScheme(securitySchemeName = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
@Tags(value = @Tag(name = "users", description = "All the user methods"))
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
    
    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns all users from the database")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No users"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))),
    })
    @GET
    public Response getAllUsers() {
        List<User> users = service.findAllUsers();
        LOGGER.debug("Total number of users " + users);
        return Response.ok(users).build();
    }
    
    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns a user for a given identifier")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "The user is not found for a given identifier"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))),
    })
    @GET
    @Path("/{id}")
    public Response getUser(
        @Parameter(description = "User identifier", required = true)
        @PathParam("id") Long id) {
        User user = service.findUserById(id);
        if (user != null) {
            LOGGER.debug("Found user " + user);
            return Response.ok(user).build();
        } else {
            LOGGER.debug("No user found with id " + id);
            return Response.noContent().build();
        }
    }

    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns data for the authorized user")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))),
    })

    @GET
    @Path("/me")
    public Response getUser() {
        String id = jwt.getSubject();
        User user = service.findUserById(id);
        if (user != null) {
            LOGGER.debug("Found user " + user);
            return Response.ok(user).build();
        } else {
            LOGGER.debug("No user found with id " + id);
            return Response.noContent().build();
        }
    }

    @Operation(summary = "Creates a valid user")
    @APIResponse(responseCode = "201", description = "The URI of the created user", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    public Response createUser(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class)))
        @Valid User user, @Context UriInfo uriInfo) {
        LOGGER.info("Trying to add new user " + user.credential.type);
        user.credential.value = user.password;
        Response response = createAuthUser(user);
        user.id = getURI(response); // assign user id from auth user
        user = service.persistUser(user);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(user.id.toString());
        LOGGER.debug("New user created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    private String getURI(Response response) {
        String[] splittedUrl = response.getLocation().toString().split("/");
        String uri = splittedUrl[splittedUrl.length - 1];
        return uri;
    }

    private Response createAuthUser(User user) {
        String authorizationHeader = createBasicAuthHeaderValue(clientId, secret);
        AuthToken token = authService.getToken(authorizationHeader, grantType);
        LOGGER.info(token.access_token);
        AuthUser authUser = new AuthUser(user);
        Response response = authService.createUser("Bearer " + token.access_token, authUser);
        return response;
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

    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Updates an exiting  user")
    @APIResponse(responseCode = "200", description = "The updated user", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @PUT
    @Path("/me")
    public Response updateUser(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class)))
        @Valid User user) {
            user = service.updateUser(user);
            LOGGER.debug("User updated with new valued " + user);
            return Response.ok(user).build();
        }
        
    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Deletes an exiting user")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204")
    })
    @DELETE
    @Path("/{id}")
    public Response deleteUser(
        @Parameter(description = "User identifier", required = true)
        @PathParam("id") Long id) {
        service.deleteUser(id);
        LOGGER.debug("User deleted with " + id);
        return Response.noContent().build();
    }
    
}