package com.web.test.dao;

import java.util.Date;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
@MapperScan(basePackages = { "com.taozi.tv.dao" })
public class VideoResourceDaoTest {
	static VideoResourceDao service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(VideoResourceDao.class);
		create();
	}

	private static void create() {
		VideoResource e = VideoResource.builder().id(10L).name("test2").createTime(new Date()).build();
		// service.query(e);

		service.create(e);
		VideoResource one = service.getOne(e);
		one.setCreateTime(new Date());
		service.update(e);
		// service.delete(e);
		System.out.println("finished");
		// service.update(e);
	}

}
