package com.example.blog_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.blog_board.security.jwt.JwtFilter;
import com.example.blog_board.security.jwt.JwtUtil;
import com.example.blog_board.security.properties.SecurityProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final SecurityProperties securityProperties;
	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(AbstractHttpConfigurer::disable)
		.cors(Customizer.withDefaults())

		.authorizeHttpRequests((authorize) -> authorize
			.requestMatchers(securityProperties.getPermitUrls().toArray(new String[0])).permitAll()
			.requestMatchers(securityProperties.getDenyUrls().toArray(new String[0])).hasRole("ADMIN")
			.anyRequest().authenticated()
		)

		.formLogin(AbstractHttpConfigurer::disable)
		.logout(AbstractHttpConfigurer::disable)

		.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtFilter jwtFilter(JwtUtil jwtUtil) {
		return new JwtFilter(jwtUtil);
	}
}
