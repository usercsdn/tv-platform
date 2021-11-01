package com.web.test.dao;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.dao.WebsiteParseConfDao;
import com.taozi.tv.dao.bean.WebsiteParseConf;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
@MapperScan(basePackages = { "com.taozi.tv.dao" })
public class WebsiteParseConfDaoTest {
	static WebsiteParseConfDao service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(WebsiteParseConfDao.class);

		create();
	}

	private static void create() {
		List<WebsiteParseConf> querys = service.query(WebsiteParseConf.builder().id(51L).name("").build());
		System.out.println(querys.size());
		// WebsiteParseConf e = WebsiteParseConf.builder().id(1L).build();
		// service.update(e);
	}

}
