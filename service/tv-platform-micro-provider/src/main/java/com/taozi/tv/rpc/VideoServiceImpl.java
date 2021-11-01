package com.taozi.tv.rpc;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.GetRcommandRequest;
import com.taozi.tv.bean.GetVideoRequest;
import com.taozi.tv.bean.IndexCardRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.pojo.ffmpeg.FFmpegMetabase;
import com.taozi.tv.pojo.task.VideoResourceUpdatePushEvent;
import com.taozi.tv.service.VideoService;
import com.taozi.tv.service.cache.IndexCacheService;
import com.taozi.tv.service.cache.VideoCacheService;
import com.taozi.tv.service.ffmpeg.FFMpegService;
import com.taozi.tv.service.task.EventBusCenter;
import com.taozi.tv.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideoResourceDao videoResourceDao;
	@Autowired
	private VideoCacheService videoCacheService;
	@Autowired
	private EventBusCenter eventBusCenter;
	@Autowired
	private FFMpegService fFMpegService;
	@Autowired
	private IndexCacheService indexCacheService;

	public CallResult<List<VideoResourceProto>> recommand(GetRcommandRequest request) {
		log.info("recommand is begin request:{}", request);
		VideoResource e = VideoResource.builder().orderType("RAND()").activeStatus(CommonUtils.ACTIVE_ON)
				.limit(request.getBeginNum()).offset(request.getEndNum()).build();
		List<VideoResource> data = videoResourceDao.queryByPage(e);
		if (ObjectUtils.isEmpty(data)) {
			log.info("recommand is failed not record");
			return CallResult.failure(CommonUtils.RESPONSE_FAILED, "recommand is failed not record");
		}
		List<VideoResourceProto> result = convert2VideoResourceProto(data);
		return CallResult.success(result);
	}

	private List<VideoResourceProto> convert2VideoResourceProto(List<VideoResource> beans) {
		List<VideoResourceProto> result = new ArrayList<>(beans.size());
		for (VideoResource bean : beans) {
			VideoResourceProto temp = convert2VideoResourceProto(bean);
			result.add(temp);
		}
		return result;
	}

	private VideoResourceProto convert2VideoResourceProto(VideoResource bean) {
		VideoResourceProto result = VideoResourceProto.builder().build();
		BeanUtils.copyProperties(bean, result);
		result.setVideoUrl(
				result.getVideoUrl().replace("https://tx-safety-video.acfun.cn", "http://videostest.com:10001")
						.replace("https://ali-safety-video.acfun.cn", "http://videostest.com:10001"));
		if (bean.getType().contains("m3u8")) {
			result.setType("application/x-mpegURL");
		}
		eventBusCenter
				.post(VideoResourceUpdatePushEvent.builder().videoResource(bean).videoUrlIsExpired(false).build());
		return result;
	}

	@Override
	public CallResult<VideoResourceProto> getVideo(GetVideoRequest request) {
		log.info("getVideoUrl is begin request:{}", request);
		VideoResource bean = videoCacheService.getIfPresentVideoCache(request.getId());
		if (ObjectUtils.isEmpty(bean)) {
			return CallResult.failure(CommonUtils.RESPONSE_FAILED, "not get bean");
		}
		String videoUrl = bean.getVideoUrl();
		FFmpegMetabase videoMetabase = fFMpegService.getVideoMetabase(videoUrl);
		if (ObjectUtils.isEmpty(videoMetabase) || ObjectUtils.isEmpty(videoMetabase.getDuration())) {
			bean.setVideoUrl("");
		}
		log.info("getVideoUrl is finished request:{}", request);
		return CallResult.success(convert2VideoResourceProto(bean));
	}

	@Override
	public CallResult<List<VideoResourceProto>> getRecommandVideo(GetVideoRequest request) {
		VideoResource video = videoCacheService.getIfPresentVideoCache(request.getId());
		if (ObjectUtils.isEmpty(video)) {
			return CallResult.failure(CommonUtils.RESPONSE_FAILED, "not get bean");
		}
		Long tagId = video.getTagId();
		List<VideoResourceProto> videosOfTag = indexCacheService
				.getVideosOfTag(IndexCardRequest.builder().tagId(tagId).leftTotalOffset(request.getOffset()).build());
		return CallResult.success(videosOfTag);
	}

}
