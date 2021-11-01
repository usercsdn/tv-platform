package com.web.test.rpc;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.CallResult;
import com.taozi.tv.bean.CardPojo;
import com.taozi.tv.bean.IndexCardRequest;
import com.taozi.tv.service.IndexService;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
public class IndexServiceTest {
	static IndexService service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(IndexService.class);
		start();
	}

	private static void start() {
		for (int i = 0; i < 100; i++) {
			CallResult<List<CardPojo>> video = service.getIndex(IndexCardRequest.builder().leftRecommandOffset(1)
					.leftTotalOffset(13).rightRecommandOffset(3).rightTotalOffset(10).build());
			// System.out.println(video);
		}
		System.out.println();
		for (int i = 0; i < 100; i++) {
			CallResult<CardPojo> video = service.getCard(IndexCardRequest.builder().tagId(1L).leftRecommandOffset(1)
					.leftTotalOffset(13).rightRecommandOffset(3).rightTotalOffset(10).build());
			// System.out.println(video);
		}
	}

}
