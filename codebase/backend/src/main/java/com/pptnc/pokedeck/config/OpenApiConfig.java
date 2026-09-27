package com.pptnc.pokedeck.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pokedeckOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PPTNC Poke Deck API")
                .description("Backend (BFF) da aplicacao PPTNC Poke Deck. Atende o frontend e atua como proxy para a PokeAPI.")
                .version("0.1.0")
                .contact(new Contact().name("PPT Nao Compila").url("https://github.com/wellingtoncruz/pptnc-pokedeck"))
                .license(new License().name("MIT")))
            .components(new Components().addSecuritySchemes(
                "bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtido via POST /api/v1/auth/login")
            ));
    }
}
