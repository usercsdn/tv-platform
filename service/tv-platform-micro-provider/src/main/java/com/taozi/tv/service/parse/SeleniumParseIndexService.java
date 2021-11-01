package com.taozi.tv.service.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.dao.bean.WebsiteParseConf;
import com.taozi.tv.dao.bean.WebsiteParseConfMetabase;
import com.taozi.tv.pojo.parse.JsoupElementNextPage;
import com.taozi.tv.pojo.parse.JsoupElementNodeRequest;
import com.taozi.tv.pojo.task.VideoResourceAddEvent;
import com.taozi.tv.pojo.task.WebsiteParseConfAddEvent;
import com.taozi.tv.service.task.EventBusCenter;
import com.taozi.tv.utils.SeleniumUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeleniumParseIndexService {
	@Autowired
	private ChromeWebDriverService chromeWebDriverService;

	@Autowired
	private VideoResourceDao videoResourceDao;

	@Autowired
	private JsoupParse jsoupParseList;
	@Autowired
	private SeleniumParseNodeService seleniumParseNodeService;
	@Autowired
	private SeleniumParseNextPageService seleniumParseNextPageService;

	@Autowired
	private EventBusCenter eventBusCenter;
	@Autowired
	private SeleniumPositionService seleniumPositionService;

	@PostConstruct
	private void register() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	private String getClassName(WebsiteParseConfAddEvent request) throws Exception {
		String cssSelector = request.getParseConf().getMetabase().getMetaLoadCss();
		if (!ObjectUtils.isEmpty(cssSelector)) {
			return cssSelector;
		}
		WebDriver webDriver = request.getWebDriver();
		for (int i = 0; i < 10; i++) {
			String html = SeleniumUtils.isPageLoadSuccess(webDriver);
			List<Element> elements = seleniumPositionService.getVideoJsoupElement(html, webDriver);
			// List<Element> elements = jsoupParseList.getElements(html);
			if (!ObjectUtils.isEmpty(elements)) {
				Element element = elements.get(0);
				return element.cssSelector();
			}
			Thread.sleep(500L);
		}
		return "";
	}

	private void closeScreenDiv(WebsiteParseConfAddEvent request) {
		try {
			String cssSelector = request.getParseConf().getMetabase().getMetaScreenCss();
			if (!ObjectUtils.isEmpty(cssSelector)) {
				WebDriver webDriver = request.getWebDriver();
				boolean executeScript = SeleniumUtils.existElementByExecuteScript(webDriver, cssSelector);
				if (executeScript) {
					String script = "return document.querySelector('" + cssSelector + "').click()";
					SeleniumUtils.executeScript(webDriver, script);
				}
			}
		} catch (Exception e) {
			log.error("closeScreenDiv is error ", e);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public String getHtml(WebsiteParseConfAddEvent request) {
		WebsiteParseConf parseConf = request.getParseConf();
		String url = parseConf.getUrl();
		WebDriver webDriver = request.getWebDriver();
		try {
			webDriver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);
			webDriver.get(url);
			String isPageLoadSuccess = SeleniumUtils.isPageLoadSuccess(webDriver);
			if (ObjectUtils.isEmpty(isPageLoadSuccess)) {
				return "";
			}
			String className = getClassName(request);
			boolean waitElementByCssSelector = SeleniumUtils.waitElementByCssSelector(webDriver, className, 15);
			if (!waitElementByCssSelector) {
				log.info("getHtml is failed request:{},waitElementByCssSelector:{}", request, waitElementByCssSelector);
				return "";
			}
			closeScreenDiv(request);
			JsoupElementNextPage nodeRequest = JsoupElementNextPage.builder().webDriver(webDriver).build();
			String indexHtml = "";
			WebsiteParseConfMetabase metabase = parseConf.getMetabase();
			for (int i = 0; i < metabase.getMetaNextSize(); i++) {
				boolean nextPageHtml = seleniumParseNextPageService.getNextPage(nodeRequest, i == 0, className);
				if (nextPageHtml) {
					nodeRequest.setNextPageText(metabase.getMetaNextText());
					String html = SeleniumUtils.isPageLoadSuccess(webDriver);
					getNode(html, className, request);
					if (i != 0 && seleniumPositionService.isSameHtml(indexHtml, html, className)) {
						log.info("getHtml is break index:{}", i);
						break;
					}
					indexHtml = html;
				}
			}
			log.info("getHtml is finished webDriver:{}", webDriver);
			return "";
		} catch (Exception e) {
			log.error("getHtml is error", e);
		} finally {
			chromeWebDriverService.putChromeDriver(webDriver);
		}
		return "";
	}

	private VideoResource nodeCheck(Element ele, WebsiteParseConfAddEvent request) {
		WebDriver webDriver = request.getWebDriver();
		WebsiteParseConf parseConf = request.getParseConf();
		boolean existElementByExecuteScript = SeleniumUtils.existElementByExecuteScript(webDriver, ele.cssSelector());
		String text = ele.text();
		log.info("text:" + ele.text());
		if (!existElementByExecuteScript || ObjectUtils.isEmpty(text)) {
			return null;
		}
		String metaVideoNameFilter = parseConf.getMetabase().getMetaVideoNameFilter();
		String videoName = text;
		if (ObjectUtils.isEmpty(videoName)) {
			return null;
		}
		String image = jsoupParseList.getImage(ele.children());
		if (ObjectUtils.isEmpty(image)) {
			return null;
		}
		String videoSha1 = getVideoSha1(image, parseConf.getId(), metaVideoNameFilter);
		if (ObjectUtils.isEmpty(videoSha1)) {
			return null;
		}
		return VideoResource.builder().name(videoName).nameSha1(videoSha1).videoImage(image).build();
	}

	public void getNode(String html, String cssName, WebsiteParseConfAddEvent request) {
		try {
			if (ObjectUtils.isEmpty(html)) {
				return;
			}
			WebDriver webDriver = request.getWebDriver();
			// SeleniumUtils.screenshotAs(webDriver);
			List<Element> elements = seleniumPositionService.getVideoJsoupElement(html, cssName);
			// List<Element> elements = jsoupParseList.getElements(html);
			String windowHandle = webDriver.getWindowHandle();
			String windowHandleTitle = webDriver.getTitle();
			WebsiteParseConf parseConf = request.getParseConf();
			for (Element ele : elements) {
				try {
					VideoResource nodeCheck = nodeCheck(ele, request);
					if (ObjectUtils.isEmpty(nodeCheck)) {
						continue;
					}
					WebElement findElementByCssSelector = webDriver.findElement(By.cssSelector(ele.cssSelector()));
					Point location = findElementByCssSelector.getLocation();
					String script = "window.scrollTo(" + location.getX() + "," + (location.getY() - 200) + ")";
					SeleniumUtils.executeScript(webDriver, script);
					findElementByCssSelector.click();
					// SeleniumUtils.executeClick(webDriver, ele.cssSelector(),
					// 3);

					boolean isSwitch = switchCurrentWindowHandle(webDriver);
					VideoResource nodeVideo = seleniumParseNodeService
							.getNodeVideo(JsoupElementNodeRequest.builder().webDriver(webDriver).build());
					switchIndexWindowHandle(windowHandle, windowHandleTitle, isSwitch, webDriver);
					destoryNoneIndexWindowHandle(windowHandle, webDriver);
					log.info("nodeVideo:{}", nodeVideo);
					buildResult(nodeCheck, nodeVideo, parseConf);
				} catch (Exception e) {
					SeleniumUtils.screenshotAs(webDriver);
					log.error("get elements is error", e);
				}
			}
			destoryNoneIndexWindowHandle(windowHandle, webDriver);
		} catch (Exception e) {
			log.error("get is error", e);
		}
	}

	private String getVideoSha1(String text, Long parseConfId, String metaVideoNameFilter) {

		// String videoName = CommonUtils.getText(text, metaVideoNameFilter,
		// false);
		String sha1 = DigestUtils.sha1Hex(text + "_" + parseConfId);
		VideoResource e = VideoResource.builder().nameSha1(sha1).build();
		e = videoResourceDao.getOne(e);
		if (!ObjectUtils.isEmpty(e)) {
			log.info("getVideoSha1 is exist in db videoImage:{},sha1:{}", text, sha1);
			return "";
		}
		return sha1;
	}

	private void buildResult(VideoResource nodeCheck, VideoResource nodeVideo, WebsiteParseConf parseConf) {
		if (ObjectUtils.isEmpty(nodeVideo)) {
			return;
		}
		Long webSiteConfId = parseConf.getId();
		nodeVideo.setName(nodeCheck.getName());
		nodeVideo.setNameSha1(nodeCheck.getNameSha1());
		nodeVideo.setVideoImage(nodeCheck.getVideoImage());
		nodeVideo.setParseConfId(webSiteConfId);
		nodeVideo.setHighQualityLevel(parseConf.getHighQualityLevel());
		nodeVideo.setTagId(parseConf.getTagId());
		nodeVideo.setTagName(parseConf.getTagName());

		log.info("buildResult:" + nodeVideo.getVideoUrl());
		VideoResourceAddEvent build = VideoResourceAddEvent.builder().videoResource(nodeVideo).build();
		eventBusCenter.post(build);
		log.info("buildResult is finished");
	}

	private void destoryNoneIndexWindowHandle(String indexWindowHandle, WebDriver chromeDriver) {
		Set<String> windowHandles = chromeDriver.getWindowHandles();
		if (windowHandles.size() > 1) {
			for (String handler : windowHandles) {
				if (!handler.equals(indexWindowHandle)) {
					chromeDriver.switchTo().window(handler);
					chromeDriver.close();
				}
			}
		}
		chromeDriver.switchTo().window(indexWindowHandle);
	}

	private boolean switchCurrentWindowHandle(WebDriver chromeDriver) {
		Set<String> windowHandles = chromeDriver.getWindowHandles();
		if (windowHandles.size() <= 1) {
			return false;
		} else {
			List<String> targetList = new ArrayList<>(windowHandles);
			chromeDriver.switchTo().window(targetList.get(targetList.size() - 1));
			return true;
		}
	}

	private void switchIndexWindowHandle(String windowHandle, String windowHandleTitle, boolean isSwitch,
			WebDriver chromeDriver) {
		Set<String> windowHandles = chromeDriver.getWindowHandles();
		if (windowHandles.size() <= 1) {
			String title = chromeDriver.getTitle();
			if (!title.equals(windowHandleTitle)) {
				Navigation navigate = chromeDriver.navigate();
				navigate.back();
			}
		} else {
			if (isSwitch) {
				String title = chromeDriver.getTitle();
				if (!title.equals(windowHandleTitle)) {
					chromeDriver.close();
					chromeDriver.switchTo().window(windowHandle);
				}
			}
		}

	}
}
