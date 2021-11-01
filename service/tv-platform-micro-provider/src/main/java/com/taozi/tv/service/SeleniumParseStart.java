package com.taozi.tv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Splitter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.WebsiteParseConfDao;
import com.taozi.tv.dao.WebsiteParseConfMetabaseDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.dao.bean.WebsiteParseConf;
import com.taozi.tv.dao.bean.WebsiteParseConfMetabase;
import com.taozi.tv.pojo.ffmpeg.FFmpegMetabase;
import com.taozi.tv.pojo.task.VideoResourceDownloadEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdateEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdatePushEvent;
import com.taozi.tv.pojo.task.WebsiteParseConfAddEvent;
import com.taozi.tv.service.cache.CacheService;
import com.taozi.tv.service.ffmpeg.FFMpegService;
import com.taozi.tv.service.parse.ChromeWebDriverService;
import com.taozi.tv.service.task.EventBusCenter;
import com.taozi.tv.utils.CacheKey;
import com.taozi.tv.utils.CommonUtils;
import com.taozi.tv.utils.SeleniumUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/22
 */
@Slf4j
@Service
public class SeleniumParseStart {
	@Autowired
	private ChromeWebDriverService chromeWebDriverService;

	@Autowired
	private EventBusCenter eventBusCenter;

	@Autowired
	private WebsiteParseConfDao websiteParseConfDao;

	@Autowired
	private WebsiteParseConfMetabaseDao websiteParseConfMetabaseDao;

	@Autowired
	private FFMpegService fFMpegService;

	@Autowired
	private VideoResourceDao videoResourceDao;
	@Autowired
	@Qualifier("guavaCacheServiceImpl")
	private CacheService cacheService;
	private int queueSize = 50000;

	private LinkedBlockingQueue<VideoResource> videoResourceUpdateQueue = new LinkedBlockingQueue<>(queueSize);

	public static void main(String[] args) throws UnsupportedEncodingException {
		String encode = URLEncoder.encode(
				"https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/hls/B3EXmtEiid5oSo-wAtJicgYYCjqHl9MxhLMRvfNan0lIykvH7iy3mlG7TNOVR7PW.m3u8?pkey=ABBLazF7kYtT2KJ06weeSxxj7FOy4GLFWMpRQgaWjUBxu8kBWRKkkFBQK0V76SGCv8vvVkEsngFZRSgBZRkE3JIhg4dZqyn3pjEJV9NUa3MikhiO4o_jXplQ494X2He1p0z2qfxD-M8aC-ZssYE8eUvNEcqaX_XJ70s2nzgfpGSkENfkAEXri2kLz3u4R93E_hfL7yEvbVklpWMRCDVwG1fN4t5r1QkiynA2GBsydWLyTCllOgpS6N68Ofaz8J04bXA&safety_id=AAJOR4_yhqJ_HLG6AYxXav7E",
				"UTF-8");
		System.out.println(encode);
	}

	@PostConstruct
	public void init() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	public void startWebSite() {
		log.info("startWebSite start is begin");
		List<WebsiteParseConf> datas = websiteParseConfDao
				.query(WebsiteParseConf.builder().activeStatus(CommonUtils.ACTIVE_ON).build());
		if (ObjectUtils.isEmpty(datas)) {
			return;
		}
		WebsiteParseConfMetabase confMetabase = WebsiteParseConfMetabase.builder().build();
		for (WebsiteParseConf element : datas) {
			confMetabase.setParseConfId(element.getId());
			WebsiteParseConfMetabase metabase = websiteParseConfMetabaseDao.getOne(confMetabase);
			if (!ObjectUtils.isEmpty(metabase)) {
				element.setMetabase(metabase);
				WebDriver webDriver = chromeWebDriverService.getChromeDriver();
				eventBusCenter.post(WebsiteParseConfAddEvent.builder().webDriver(webDriver).parseConf(element).build());
			}
		}
		log.info("startWebSite start is finished");
	}

	public void startPlay(String urls) {

		if (ObjectUtils.isEmpty(urls)) {
			log.info("startPlay urls is empty");
			return;
		}
		List<String> urlArray = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(urls);
		for (int i = 0; i < 100; i++) {
			for (String url : urlArray) {
				WebDriver webDriver = null;
				log.info("startPlay is begin cycle i:{},url:{}", i, url);
				try {
					webDriver = chromeWebDriverService.getChromeDriver();
					webDriver.get(url);
					WebElement video = SeleniumUtils.waitElementByTagName(webDriver, "video", 16);
					SeleniumUtils.executeScript(webDriver, "return arguments[0].play();", video);
					Thread.sleep(1000L * 1);
				} catch (Exception e) {
				} finally {
					log.info("startPlay is finished cycle i:{},url:{}", i, url);
					// chromeWebDriverService.putChromeDriver(webDriver);
					chromeWebDriverService.destoryWebDriver(webDriver);
					chromeWebDriverService.createWebDriver();
				}

			}
		}

	}

	public void startUpdateNode() {
		log.info("startNode start is begin");
		List<VideoResource> datas = getVideoResources(null);
		if (ObjectUtils.isEmpty(datas)) {
			return;
		}
		for (VideoResource element : datas) {
			isNeedPutChromeDriverTask(element);
		}
		log.info("startNode start is finished");
	}

	public void addSpecialVideoResource(String url) {
		log.info("addSpecialVideoResource start is begin url:{}", url);
		VideoResource element = VideoResource.builder().pageUrl(url).parseConfId(0L).build();
		VideoResource bean = videoResourceDao.getOne(element);
		if (!ObjectUtils.isEmpty(bean)) {
			return;
		}
		isNeedPutChromeDriverTask(element);
		log.info("addSpecialVideoResource start is finished");
	}

	@PostConstruct
	public void takeChromeDriverTask() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						log.info("videoResourceUpdateQueue size:{}", videoResourceUpdateQueue.size());
						VideoResource event = videoResourceUpdateQueue.take();
						WebDriver webDriver = chromeWebDriverService.getChromeDriver();
						VideoResourceUpdateEvent build = VideoResourceUpdateEvent.builder().videoResource(event)
								.webDriver(webDriver).build();
						eventBusCenter.post(build);
						String id = Objects.toString(event.getId());
						String key = CacheKey.KEY_TV_UPDATE_VIDEO.replace(CacheKey.VIDEOID, id);
						cacheService.del(key);
					} catch (Exception e) {
						log.info("takeChromeDriverTask is error", e);
					}
				}
			}
		}).start();
	}

	public void updateVideoResourceChromeDriverTask(VideoResource event) {
		try {
			int size = videoResourceUpdateQueue.size();
			if (size >= queueSize) {
				log.info("putChromeDriverTask queue size is too large while throw task size:{},maxSize:{},event:{}",
						size, queueSize, event);
				return;
			}
			videoResourceUpdateQueue.put(event);
		} catch (Exception e) {
			log.error("putChromeDriverTask is error,event:{}", event);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void updatePushVideoResource(VideoResourceUpdatePushEvent event) {
		String id = Objects.toString(event.getVideoResource().getId());
		String key = CacheKey.KEY_TV_UPDATE_VIDEO.replace(CacheKey.VIDEOID, id);
		Boolean exist = cacheService.exist(key);
		if (exist) {
			log.info("updatePushVideoResource cache  exist videoId:{}", id);
			return;
		}
		cacheService.set(key, id, 1L);
		if (event.isVideoUrlIsExpired()) {
			updateVideoResourceChromeDriverTask(event.getVideoResource());
		} else {
			isNeedPutChromeDriverTask(event.getVideoResource());
		}
	}

	private List<VideoResource> getVideoResources(Integer highQualityStatus) {
		List<VideoResource> datas = videoResourceDao.query(VideoResource.builder().activeStatus(CommonUtils.ACTIVE_ON)
				.videoIsExpired(CommonUtils.ACTIVE_ON).highQualityLevel(highQualityStatus).build());
		return datas;
	}

	private boolean isNeedPutChromeDriverTask(VideoResource element) {
		String videoUrl = element.getVideoUrl();
		FFmpegMetabase videoMetabase = fFMpegService.getVideoMetabase(videoUrl);
		if (ObjectUtils.isEmpty(videoMetabase) || ObjectUtils.isEmpty(videoMetabase.getDuration())) {
			updateVideoResourceChromeDriverTask(element);
			return true;
		}
		cacheService.del(CacheKey.KEY_TV_UPDATE_VIDEO.replace(CacheKey.VIDEOID, element.getId() + ""));
		log.info("isNeedPutChromeDriverTask not need update videoId:{}", element.getId());
		return false;
	}

	public void startDownload(Integer size, Long id, Long parseConfId) {
		log.info("startDownload start is begin size:{}", size);

		List<VideoResource> datas = videoResourceDao
				.queryByPage(VideoResource.builder().activeStatus(CommonUtils.ACTIVE_ON)
						.videoIsExpired(CommonUtils.ACTIVE_ON).id(id).parseConfId(parseConfId)
						.highQualityLevel(CommonUtils.ACTIVE_OFF).orderType("RAND()").offset(size).limit(0).build());
		if (ObjectUtils.isEmpty(datas)) {
			return;
		}
		for (VideoResource element : datas) {
			if (!isNeedPutChromeDriverTask(element)) {
				VideoResourceDownloadEvent build = VideoResourceDownloadEvent.builder().videoResource(element).build();
				eventBusCenter.post(build);
			}
		}
		log.info("startDownload start is finished");
	}

}
