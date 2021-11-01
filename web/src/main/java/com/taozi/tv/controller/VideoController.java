package com.taozi.tv.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.GetVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.service.VideoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/video")
public class VideoController {
	@Reference(timeout = 10000)
	private VideoService videoService;

	@GetMapping("/getVideo")
	public CallResult<VideoResourceProto> getVideo(@NotNull Long id) {
		log.info("getVideo is begin id:{}", id);
		CallResult<VideoResourceProto> video = videoService.getVideo(GetVideoRequest.builder().id(id).build());
		log.info("getVideo is finished id:{}", id);
		return video;
	}

	@GetMapping("/getRecommand")
	public CallResult<List<VideoResourceProto>> getRecommand(@NotNull Long id, @NotNull Integer offset) {
		log.info("getRecommand is begin id:{}", id);
		CallResult<List<VideoResourceProto>> video = videoService
				.getRecommandVideo(GetVideoRequest.builder().id(id).offset(offset).build());
		log.info("getRecommand is finished id:{}", id);
		return video;
	}

}
