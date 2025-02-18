package com.example.blog_board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlogBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogBoardApplication.class, args);
	}

}
