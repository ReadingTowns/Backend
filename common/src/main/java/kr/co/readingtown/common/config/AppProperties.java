package kr.co.readingtown.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${app.profile.default-image-url}")
    private String defaultProfileImageUrl;

    public String getDefaultProfileImageUrl() {
        return defaultProfileImageUrl;
    }
}
