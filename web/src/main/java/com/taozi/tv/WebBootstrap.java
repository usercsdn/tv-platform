package com.taozi.tv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
public class WebBootstrap extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebBootstrap.class);
	}

	public static void main(String[] args) {
		log.info("Bootstrap is begin");
		SpringApplication.run(WebBootstrap.class, args);
	}
}
