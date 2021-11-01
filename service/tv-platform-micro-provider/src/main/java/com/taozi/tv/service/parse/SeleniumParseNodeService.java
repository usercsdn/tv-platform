package com.taozi.tv.service.parse;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.pojo.parse.JsoupElementNodeRequest;
import com.taozi.tv.pojo.task.VideoResourceAddEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdateEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdateFinishedEvent;
import com.taozi.tv.service.task.EventBusCenter;
import com.taozi.tv.utils.CommonUtils;
import com.taozi.tv.utils.SeleniumUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeleniumParseNodeService {
	private String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
	private Pattern patten = Pattern.compile(regex);

	@Autowired
	private EventBusCenter eventBusCenter;
	@Autowired
	private ChromeWebDriverService chromeWebDriverService;

	@PostConstruct
	private void register() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void updateWebNodeVideo(VideoResourceUpdateEvent event) {
		WebDriver webDriver = event.getWebDriver();
		try {
			VideoResource data = event.getVideoResource();
			webDriver.get(data.getPageUrl());
			VideoResource nodeVideo = getNodeVideo(JsoupElementNodeRequest.builder().webDriver(webDriver).build());
			if (!ObjectUtils.isEmpty(nodeVideo) && !ObjectUtils.isEmpty(nodeVideo.getVideoUrl())) {
				data.setVideoUrl(nodeVideo.getVideoUrl());
				data.setType(nodeVideo.getType());
				boolean ifAddVideoResource = ifAddVideoResource(data, webDriver);
				if (!ifAddVideoResource) {
					log.info("updateWebNodeVideo is begin videoName:{}", data.getName());
					eventBusCenter.post(VideoResourceUpdateFinishedEvent.builder().videoResource(data).build());
				}
			}
		} catch (Exception e) {
			log.error("updateWebNodeVideo is error", e);
		} finally {
			Navigation navigate = webDriver.navigate();
			navigate.back();
			chromeWebDriverService.putChromeDriver(webDriver);
		}
	}

	private boolean ifAddVideoResource(VideoResource data, WebDriver webDriver) {
		if (ObjectUtils.isEmpty(data.getId())) {
			String videoName = webDriver.getTitle();

			String sha1 = DigestUtils.sha1Hex(videoName + "_" + Objects.toString(data.getParseConfId(), "0"));
			data.setName(webDriver.getTitle());
			data.setNameSha1(sha1);
			data.setHighQualityLevel(CommonUtils.ACTIVE_OFF);
			log.info("ifAddVideoResource is begin videoName:{}", videoName);
			eventBusCenter.post(VideoResourceAddEvent.builder().videoResource(data).build());
			return true;
		}
		return false;
	}

	public VideoResource getNodeVideo(JsoupElementNodeRequest nodeRequest) {
		WebDriver webDriver = nodeRequest.getWebDriver();
		try {
			WebElement video = SeleniumUtils.waitElementByTagName(webDriver, nodeRequest.getLoadBrowserSuccessSign(),
					16);
			if (ObjectUtils.isEmpty(video)) {
				return null;
			}
			String currentUrl = webDriver.getCurrentUrl();
			String url = SeleniumUtils.executeScript(webDriver, "return arguments[0].currentSrc;", video);
			if (ObjectUtils.isEmpty(url) || url.startsWith(nodeRequest.getLoadVideoExcludeSign())) {
				Logs logs = webDriver.manage().logs();
				for (int i = 0; i < 10; i++) {
					LogEntries logEntries = logs.get(LogType.PERFORMANCE);
					for (LogEntry e : logEntries) {
						VideoResource videoUrl = getVideoUrl(e.getMessage(), currentUrl);
						if (!ObjectUtils.isEmpty(videoUrl)) {
							return videoUrl;
						}
					}
					Thread.sleep(1000);
				}
			} else {
				return getVideoUrl(url, currentUrl);
			}
		} catch (Exception e) {
			log.error("getNodeVideo is error", e);
		}
		return null;
	}

	private VideoResource getVideoUrl(String url, String currentUrl) {
		List<String> types = Lists.newArrayList(".mp4", ".m3u8");
		for (String type : types) {
			if (url.contains(type)) {
				Matcher matcher = patten.matcher(url);
				while (matcher.find()) {
					String group = matcher.group();
					if (group.contains(type) && url.contains(currentUrl)) {
						return VideoResource.builder().videoUrl(group).type(type).pageUrl(currentUrl).build();
					}
				}
			}
		}
		return null;
	}
}
