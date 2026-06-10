package org.aep.observacao.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ObservAção API")
                        .version("1.0")
                        .description("API REST para gerenciar solicitações do ObservAção")
                        .contact(new Contact().name("ObservAção Team"))
                );
    }
}
