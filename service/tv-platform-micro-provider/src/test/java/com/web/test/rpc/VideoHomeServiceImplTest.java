package com.web.test.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.CallResult;
import com.taozi.tv.bean.GetVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.rpc.VideoServiceImpl;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
public class VideoHomeServiceImplTest {
	static VideoServiceImpl service;

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		service = run.getBean(VideoServiceImpl.class);
		start();
	}

	private static void start() {
		CallResult<VideoResourceProto> video = service.getVideo(GetVideoRequest.builder().id(946L).build());
		System.out.println(video);
	}

}
