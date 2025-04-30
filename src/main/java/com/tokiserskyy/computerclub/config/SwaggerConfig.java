package com.tokiserskyy.computerclub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenapi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dating App API")
                        .version("0.5.2")
                        .description("API application for Computer CLub"));
    }
}
