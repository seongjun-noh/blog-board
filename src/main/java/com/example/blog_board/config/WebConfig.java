package com.example.blog_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.blog_board.security.properties.SecurityProperties;

@Configuration
public class WebConfig {

	@Bean
	public WebMvcConfigurer webMvcConfig(SecurityProperties securityProperties) {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// 모든 경로(또는 /api/** 등)에서 CORS 적용
				registry.addMapping("/**")
					.allowedOrigins(securityProperties.getCors().getAllowedOrigins().toArray(new String[0]))
					.allowedMethods(securityProperties.getCors().getAllowedMethods().toArray(new String[0]))
					.allowedHeaders(securityProperties.getCors().getAllowedHeaders().toArray(new String[0]))
					.allowCredentials(securityProperties.getCors().isAllowCredentials());
			}
		};
	}
}
