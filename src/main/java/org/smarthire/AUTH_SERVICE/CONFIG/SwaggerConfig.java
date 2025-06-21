package org.smarthire.AUTH_SERVICE.CONFIG;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "AUTH-API",
                description = "Api for providing Authentication, made with Spring security and JWT",
                contact = @Contact(
                        name = "Subhadeep Pattanayak",
                        email = "spjisu2000@gmail.com"
                ),
                version = "v1"
        ),
        security = @SecurityRequirement(
                name = "auth"
        )
)
@SecurityScheme(
        name = "auth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        description = "Security Description",
        scheme = "bearer"
)
public class SwaggerConfig {


}