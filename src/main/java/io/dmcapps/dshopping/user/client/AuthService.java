package io.dmcapps.dshopping.user.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@RegisterRestClient(configKey="auth-api")
public interface AuthService {

    @POST
    @Path("/realms/dshopping/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthToken getToken(@HeaderParam("Authorization") String authorization, @FormParam("grant_type") String grant_type);
    
    @POST
    @Path("/admin/realms/dshopping/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(@HeaderParam("Authorization") String authorization, AuthUser authUser);
}