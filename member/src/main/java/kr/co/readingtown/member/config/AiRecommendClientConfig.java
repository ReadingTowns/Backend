package kr.co.readingtown.member.config;

import feign.Request;
import org.springframework.context.annotation.Bean;

public class AiRecommendClientConfig {

    @Bean
    public Request.Options aiRecommendFeignOptions() {
        return new Request.Options(
                3_000,  // 연결 타임아웃 3초
                10_000  // 읽기 타임아웃 10초
        );
    }
}
