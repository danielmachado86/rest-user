package io.dmcapps.dshopping.user;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@SecurityScheme(securitySchemeName = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
@Tags(value = @Tag(name = "addresses", description = "All the addresses methods"))
@Path("/api/users")
@Produces(APPLICATION_JSON)
public class AddressResource {

    private static final Logger LOGGER = Logger.getLogger(AddressResource.class);

    @Inject
    AddressService service;

    @Inject
    JsonWebToken jwt;

    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns all all addreses from the database for user_id")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No addresses"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))),
    })
    @GET
    @Path("/{user_id}/addresses")
    public Response getAllUserAddresses(
        @Parameter(description = "user identifier", required = true)
        @PathParam("user_id") String user_id) {
        List<Address> addresses = service.findAllUserAddresses(user_id);
        LOGGER.debug("Total number of addresses " + addresses);
        return Response.ok(addresses).build();
    }

    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns all all addreses from the database for the authenticated user")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No addresses"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class, type = SchemaType.ARRAY))),
    })
    @GET
    @Path("/me/addresses")
    public Response getAllAddressesForAuthUser() {
        String user_id = jwt.getSubject();
        List<Address> addresses = service.findAllUserAddresses(user_id);
        LOGGER.debug("Total number of addresses " + addresses);
        return Response.ok(addresses).build();
    }
    
    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns a address for a given identifier")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class, type = SchemaType.ARRAY))),
    })
    @GET
    @Path("/me/addresses/{name}")
    public Response getAddress(
        @Parameter(description = "Address name", required = true)
        @PathParam("name") String name) {
        String user_id = jwt.getSubject();
        Address address = service.findAddressForUser(user_id, name);
        if (address != null) {
            LOGGER.debug("Found address " + address);
            return Response.ok(address).build();
        } else {
            LOGGER.debug("No address found with name " + name);
            return Response.noContent().build();
        }
    }
    
    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Returns a address for a given identifier")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class, type = SchemaType.ARRAY))),
    })
    @GET
    @Path("/{user_id}/addresses/{name}")
    public Response getUserAddressByName(
        @Parameter(description = "User id", required = true)
        @PathParam("user_id") String user_id,
        @Parameter(description = "Address name", required = true)
        @PathParam("name") String name) {
        Address address = service.findUserAddressByName(user_id, name);
        if (address != null) {
            LOGGER.debug("Found address " + address);
            return Response.ok(address).build();
        } else {
            LOGGER.debug("No address found with name " + name);
            return Response.noContent().build();
        }
    }

    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Creates a valid address")
    @APIResponse(responseCode = "201", description = "The URI of the created address", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    @Path("/addresses")
    public Response createAddress(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class)))
        @Valid Address address, @Context UriInfo uriInfo) {
        address = service.persistAddress(address);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().replacePath("/api/users").path(address.user).path("/addresses").path(address.name);
        LOGGER.debug("New address created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Creates a valid address for authenticated user")
    @APIResponse(responseCode = "201", description = "The URI of the created address", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    @Path("me/addresses")
    public Response createUserAddress(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class)))
        @Valid Address address, @Context UriInfo uriInfo) {
        String user_id = jwt.getSubject();
        address.user = user_id;
        address = service.persistAddress(address);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().replacePath("/api/users").path(address.user).path("/addresses").path(address.name);
        LOGGER.debug("New address created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Updates address")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class, type = SchemaType.ARRAY))),
    })
    @PUT
    @Path("/me/addresses")
    public Response updateUserAddress(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class)))
        @Valid Address address) {
        address = service.updateAddress(address);
        LOGGER.debug("Address updated with new value " + address);
        return Response.ok(address).build();
    }

    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Updates authorized user address")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class, type = SchemaType.ARRAY))),
    })
    @PUT
    @Path("/addresses")
    public Response updateAddress(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Address.class)))
        @Valid Address address) {
            address = service.updateAddress(address);
            LOGGER.debug("Address updated with new value " + address);
            return Response.ok(address).build();
        }
        
    @RolesAllowed("admin")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Deletes an exiting address")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200"),
    })
    @DELETE
    @Path("{user_id}/addresses/{name}")
    public Response deleteAddress(
        @Parameter(description = "Address identifier", required = true)
        @PathParam("user_id") String user_id,
        @Parameter(description = "Address name", required = true)
        @PathParam("name") String name){
        service.deleteAddress(user_id, name);
        LOGGER.debug("Address deleted with " + name);
        return Response.noContent().build();
    }
        
    @RolesAllowed("user")
    @SecurityRequirement(name = "jwt", scopes = {})
    @Operation(summary = "Deletes an exiting address")
    @APIResponses(value = {
        @APIResponse(responseCode = "401", description = "Unauthorized Error"),
        @APIResponse(responseCode = "204", description = "No address"),
        @APIResponse(responseCode = "200"),
    })
    @DELETE
    @Path("me/addresses/{name}")
    public Response deleteUserAddress(
        @Parameter(description = "Address name", required = true)
        @PathParam("name") String name){
        String user_id = jwt.getSubject();
        service.deleteAddress(user_id, name);
        LOGGER.debug("Address deleted with " + name);
        return Response.noContent().build();
    }

}