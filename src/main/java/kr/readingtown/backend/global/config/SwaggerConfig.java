package kr.readingtown.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server}")
    private String serverUri;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("ReadingTown API")
                    .description("동네 기반 도서 교환 API 명세서")
                    .version("v1.0.0"))
                .servers(List.of(
                    new Server()
                            .url(serverUri)
                            .description("배포 서버"),
                    new Server()
                            .url("http://localhost:8080")
                            .description("로컬 개발 서버")
            ));
    }
}