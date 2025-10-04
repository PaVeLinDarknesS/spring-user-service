package ru.aston.intensive.util.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Pavel"
                ),
                description = "OpenApi documentation for User Service",
                title = "OpenApi specification - Pavel",
                version = "1.0",
                license = @License(
                        name = "Lisense name",
                        url = "https://some-url.com"
                ),
                termsOfService = "Some term of service"
        ),
        servers = {
                @Server(
                        description = "Local Env",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApiConfig {
}
