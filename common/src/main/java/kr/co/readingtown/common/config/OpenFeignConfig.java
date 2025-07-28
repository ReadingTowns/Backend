package kr.co.readingtown.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("kr.co.readingtown")
public class OpenFeignConfig {
}
