package com.sgl.tenant.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGL Tenant Service API")
                        .description("Microserviço responsável pelo gerenciamento de Tenants (Lava-Jatos) no SaaS SGL.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SGL Team")
                                .email("sgl@example.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://example.com")));
    }

    @Bean
    public GlobalOpenApiCustomizer tenantHeaderCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem ->
                        pathItem.readOperations().forEach(operation ->
                                operation.addParametersItem(
                                        new Parameter()
                                                .in("header")
                                                .name("X-Tenant-ID")
                                                .description("Identificador do tenant")
                                                .required(false)
                                                .schema(new StringSchema())
                                )
                        )
                );
            }
        };
    }
}

