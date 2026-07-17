package com.itau.desafio.requesterservice.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI requesterServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Requester Service API")
                        .description("API de gerenciamento de solicitantes")
                        .version("v1"));
    }
}
