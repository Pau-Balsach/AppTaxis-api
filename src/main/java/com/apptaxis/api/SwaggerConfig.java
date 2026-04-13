package com.apptaxis.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("AppTaxis API")
                .description("""
                    API REST para la gestión de conductores y viajes de la flota de taxis.
                    
                    **Autenticación**: todos los endpoints requieren el header `X-API-Key` \
                    con una clave válida asignada a tu cuenta. Cada clave solo permite \
                    acceder a los datos de su propio cliente.
                    """)
                .version("1.1.0")
                .contact(new Contact()
                    .name("Pau Balsach")
                    .url("https://github.com/Pau-Balsach/apptaxis-api"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")))
            .components(new Components()
                .addSecuritySchemes("apiKey", new SecurityScheme()
                    .type(Type.APIKEY)
                    .in(In.HEADER)
                    .name("X-API-Key")
                    .description("Clave de acceso asignada a tu cuenta.")));
    }
}