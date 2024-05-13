package com.esmartdie.EsmartCafeteriaApi.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info= @Info(
                title= "EsmartCafeteria - CRUD",
                version = "1.0",
                description = "This is a CRUD for a cafeteria management"
        )
)
public class OpenApiConfig {
}
