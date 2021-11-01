package com.taozi.tv.service.cache;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.utils.CacheKey;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/06/15
 */
@Slf4j
@Service
public class VideoCacheService {
	@Autowired
	@Qualifier("guavaCacheServiceImpl")
	private CacheService cacheService;

	@Autowired
	private VideoResourceDao videoResourceDao;

	public VideoResource getIfPresentVideoCache(Long id) {
		VideoResource bean = cacheService.get(CacheKey.KEY_TV_VIDEOINFO_VIDEO.replace(CacheKey.VIDEOID, id + ""),
				VideoResource.class);
		if (!ObjectUtils.isEmpty(bean)) {
			return bean;
		}
		bean = videoResourceDao.getOne(VideoResource.builder().id(id).build());
		putVideoCache(bean);
		return bean;
	}

	public VideoResourceProto getIfPresentVideoProtoCache(Long id) {
		VideoResource bean = cacheService.get(CacheKey.KEY_TV_VIDEOINFO_VIDEO.replace(CacheKey.VIDEOID, id + ""),
				VideoResource.class);
		if (ObjectUtils.isEmpty(bean)) {
			bean = videoResourceDao.getOne(VideoResource.builder().id(id).build());
			putVideoCache(bean);
		}
		if (ObjectUtils.isEmpty(bean)) {
			return null;
		}
		VideoResourceProto target = VideoResourceProto.builder().build();
		BeanUtils.copyProperties(bean, target);
		return target;
	}

	public void putVideoCache(VideoResource video) {
		if (ObjectUtils.isEmpty(video)) {
			log.info("putVideoCache video is Empty");
			return;
		}
		String videoImage = video.getVideoImage();
		if (videoImage.contains(".gif")) {
			video.setVideoImage(video.getVideoImage().replaceAll("imageslim",
					"imageMogr2/auto-orient/format/webp/quality/45!/ignore-error/1"));
		} else {
			video.setVideoImage(video.getVideoImage().replaceAll("imageslim", "imageView2/1/w/320/h/180"));
		}
		cacheService.set(CacheKey.KEY_TV_VIDEOINFO_VIDEO.replace(CacheKey.VIDEOID, video.getId() + ""), video, 5L);
	}

	public void updateVideoCache(VideoResource video) {
		if (ObjectUtils.isEmpty(video)) {
			log.info("updateVideoCache video is Empty");
			return;
		}
		String key = CacheKey.KEY_TV_VIDEOINFO_VIDEO.replace(CacheKey.VIDEOID, video.getId() + "");
		if (!cacheService.exist(key)) {
			log.info("updateVideoCache video cache is not exist id:{}", video.getId());
			return;
		}
		String videoImage = video.getVideoImage();
		if (videoImage.contains(".gif")) {
			video.setVideoImage(video.getVideoImage().replaceAll("imageslim",
					"imageMogr2/auto-orient/format/webp/quality/45!/ignore-error/1"));
		} else {
			video.setVideoImage(video.getVideoImage().replaceAll("imageslim", "imageView2/1/w/320/h/180"));
		}
		cacheService.set(key, video, 5L);
	}
}
