package com.web.test.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.dao.WebsiteParseConfMetabaseDao;
import com.taozi.tv.dao.bean.WebsiteParseConfMetabase;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
@MapperScan(basePackages = { "com.taozi.tv.dao" })
public class WebsiteParseConfMetabaseDaoTest {
	static WebsiteParseConfMetabaseDao service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(WebsiteParseConfMetabaseDao.class);
		create();
	}

	private static void create() {
		WebsiteParseConfMetabase e = WebsiteParseConfMetabase.builder().build();
		// service.create(e);
		System.out.println(service.count(e));
		System.out.println("finished");
	}

}
