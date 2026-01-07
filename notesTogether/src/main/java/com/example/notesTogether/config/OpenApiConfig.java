package com.example.notesTogether.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Notes-Together API",
                version = "1.0.0",
                description = "Backend API for the Notes-Together platform"
        )
)
public class OpenApiConfig {}
