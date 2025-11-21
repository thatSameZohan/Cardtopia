package org.spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Cardtopia API").version("1.0"));
    }

    @Bean
    public GroupedOpenApi groupedOpenApiPerson() {
        return GroupedOpenApi.builder().group("cardtopia")
                .displayName("Cardtopia Group")
                .pathsToMatch("/v1/api/**")
                .build();
    }
}