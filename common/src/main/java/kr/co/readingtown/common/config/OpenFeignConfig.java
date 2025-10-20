package kr.co.readingtown.common.config;

import feign.Request;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("kr.co.readingtown")
public class OpenFeignConfig {

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                10_000, // 연결 타임아웃 10초
                120_000 // 읽기 타임아웃 120초
        );
    }
}
