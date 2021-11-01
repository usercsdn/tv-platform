package com.taozi.tv.service.task;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.elastic.VideoResourceESDao;
import com.taozi.tv.pojo.ffmpeg.FFmpegMetabase;
import com.taozi.tv.pojo.task.VideoResourceAddEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdateFinishedEvent;
import com.taozi.tv.service.cache.VideoCacheService;
import com.taozi.tv.service.ffmpeg.FFMpegService;
import com.taozi.tv.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/19
 */
@Slf4j
@Service
public class VideoPersisterTaskService {

	@Autowired
	private VideoResourceDao videoResourceDao;
	@Autowired
	private VideoResourceESDao videoResourceESDao;
	@Autowired
	private FFMpegService fFMpegService;
	@Autowired
	private EventBusCenter eventBusCenter;

	@Autowired
	private VideoCacheService videoCacheService;

	@PostConstruct
	private void register() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void addVideoResource(VideoResourceAddEvent event) {
		VideoResource data = event.getVideoResource();
		String pageUrl = data.getPageUrl();

		VideoResource e = VideoResource.builder().pageUrl(pageUrl).build();
		e = videoResourceDao.getOne(e);
		if (!ObjectUtils.isEmpty(e)) {
			log.info("persister is exist data:{}", data);
			return;
		}
		String downloadUrl = data.getVideoUrl();
		String url = downloadUrl.replaceAll("=", "\\=");
		FFmpegMetabase videoMetabase = fFMpegService.getVideoMetabase(url);
		int duration = NumberUtils.toInt(videoMetabase.getDuration(), 0);

		data.setDuration(duration);
		data.setDurationFormat(CommonUtils.formatSeconds(duration));
		data.setLikeCount(RandomUtils.nextLong(0, 100000));
		data.setActiveStatus(CommonUtils.ACTIVE_ON);
		data.setCreateTime(new Date());
		videoResourceDao.create(data);
		videoResourceESDao.save(data);
		log.info("addVideoResource is finished videoName:{}", data.getName());
	}

	@Subscribe
	@AllowConcurrentEvents
	public void updateVideoResource(VideoResourceUpdateFinishedEvent event) {
		VideoResource data = event.getVideoResource();
		String downloadUrl = data.getVideoUrl();
		String url = downloadUrl.replaceAll("=", "\\=");
		FFmpegMetabase videoMetabase = fFMpegService.getVideoMetabase(url);
		data.setDuration(NumberUtils.toInt(videoMetabase.getDuration(), 0));
		data.setActiveStatus(CommonUtils.ACTIVE_ON);
		data.setModifyTime(new Date());
		data.setVideoUrl(downloadUrl);
		videoResourceDao.update(data);
		videoCacheService.updateVideoCache(data);
		log.info("updateVideoResource is finished videoName:{}", data.getName());
	}

}
