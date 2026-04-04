package com.apptaxis.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("AppTaxis API")
                .description("API REST para la gestion de conductores y viajes de la flota de taxis.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Pau Balsach")
                    .url("https://github.com/Pau-Balsach/apptaxis-api"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")));
    }
}
