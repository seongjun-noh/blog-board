package com.example.blog_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(AbstractHttpConfigurer::disable)
		.cors(AbstractHttpConfigurer::disable)

		.authorizeHttpRequests((authorize) -> authorize
			.requestMatchers("/**").permitAll()
			.anyRequest().authenticated()
		)

		.formLogin(formLogin ->
			formLogin
				.loginPage("/login")
				.permitAll()
		)
		.logout(logout ->
			logout
				.logoutUrl("/logout")
				.permitAll()
		);

		return http.build();
	}
}
