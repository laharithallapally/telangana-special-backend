package com.telanaganaspecial.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste your JWT token here"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI telanganaSpecialOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telangana Special API")
                        .description("Backend API for Madhapur Street Food online ordering")
                        .version("v1.0"));
    }
}