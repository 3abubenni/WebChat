package com.chat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(title = "Chat application", description = "Application for github as portfolio", version = "v1"),
        security = {
                @SecurityRequirement(name = "Authorization")
        }
)
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}

