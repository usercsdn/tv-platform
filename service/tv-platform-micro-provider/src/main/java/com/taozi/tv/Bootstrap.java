package com.taozi.tv;

import java.util.UUID;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@org.apache.dubbo.config.spring.context.annotation.EnableDubbo
@MapperScan(basePackages = { "com.taozi.tv.dao" })
public class Bootstrap extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Bootstrap.class);
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
		log.info("Bootstrap is begin");
		SpringApplication.run(Bootstrap.class, args);
	}
}
