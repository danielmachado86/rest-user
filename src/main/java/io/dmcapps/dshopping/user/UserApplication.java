package io.dmcapps.dshopping.user;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
@OpenAPIDefinition(
    info = @Info(title = "User API",
        description = "This API allows CRUD operations on an authenticated user",
        version = "1.0",
        contact = @Contact(name = "Daniel Machado", url = "https://github.com/danielmachado86")),
    servers = {
        @Server(url = "http://localhost:8083")
    },
    externalDocs = @ExternalDocumentation(url = "https://github.com/danielmachado86/rest-user", description = "User service for distributed orders"),
    tags = {
        @Tag(name = "api", description = "Public that can be used by anybody"),
        @Tag(name = "users", description = "Anybody interested in users")
    }
)
public class UserApplication extends Application {
}