package com.web.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.service.SeleniumParseStart;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
public class SeleniumParseStartTest {
	static SeleniumParseStart service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(SeleniumParseStart.class);
		start();
	}

	private static void start() {
		service.startWebSite();
		// service.startUpdateNode();
		// service.startDownload(20, null, 2L);
		// service.addSpecialVideoResource("https://www.acfun.cn/v/ac4784498");
		// service.startPlay("https://b23.tv/GpkbrJ");
	}

}
