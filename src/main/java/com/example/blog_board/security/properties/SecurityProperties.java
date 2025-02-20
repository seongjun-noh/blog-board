package com.example.blog_board.security.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("security")
public class SecurityProperties {
    // CORS 설정
    private CorsProps cors;

    // 허용 URL
    private List<String> permitUrls;
    // 거부 URL
    private List<String> denyUrls;

    @Getter
    @Setter
    public static class CorsProps {
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
        private List<String> allowedHeaders;
        private boolean allowCredentials;
    }
}