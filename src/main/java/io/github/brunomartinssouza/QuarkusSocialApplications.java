package io.github.brunomartinssouza;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
            title = "API Quarkus Social",
            version = "1.0",
            contact = @Contact(
                    name = "Bruno Martins",
                    url = "https://github.com/brunomartinssouza",
                    email = "martins_bruno01@hotmail.com"),
            license = @License(
                    name = "Apache 2.0",
                    url = "https://www.apache.org.licenses/LICENSE-2.0.html")
            )
)
public class QuarkusSocialApplications extends Application {
}
