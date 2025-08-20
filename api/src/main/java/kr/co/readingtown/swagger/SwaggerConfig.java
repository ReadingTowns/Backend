package kr.co.readingtown.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.base-uri}")
    private String serverUri;

    // TODO : 앞으로 외부 API 문서화가 필요한 api의 패키지를 packagesToScan에 추가해주세요!!
    @Bean
    public GroupedOpenApi externalApiGroup() {
        return GroupedOpenApi.builder()
                .group("external-api")
                .packagesToScan(
                        "kr.co.readingtown.member.externalapi",
                        "kr.co.readingtown.review.externalapi",
                        "kr.co.readingtown.book.externalapi",
                        "kr.co.readingtown.bookhouse.externalapi",
                        "kr.co.readingtown.chat.externalapi"
                )
                .build();
    }

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