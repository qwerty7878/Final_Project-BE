package com.highpass.runspot.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // API 기본 설정
        Info info = new Info()
                .title("Run-Spot API Document")
                .version("1.0")
                .description(
                        "[Run-Spot].\n"
                );

        // Server 설정
        Server server = new Server();
        server.setUrl("https://api-ide.sjm00.link");
        server.setDescription("Production Server");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Server");

        // Security 설정

        return new OpenAPI()
                .info(info)
                .servers(List.of(server, localServer))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(new Components().addSecuritySchemes(
                        "cookieAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                ));
    }
}