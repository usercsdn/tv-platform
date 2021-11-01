package com.taozi.tv.service.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.pojo.parse.JsoupElementNodeRequest;
import com.taozi.tv.utils.SeleniumUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeleniumPositionService {
	@Autowired
	private JsoupParse jsoupParseList;
	@Autowired
	private SeleniumParseNodeService seleniumParseNodeService;

	public List<Element> getVideoJsoupElement(String html, String cssName) {
		List<List<Element>> elements = jsoupParseList.getElementsArray(html);
		for (List<Element> eles : elements) {
			for (Element ele : eles) {
				if (ele.cssSelector().equals(cssName)) {
					return eles;
				}
			}
		}
		return Collections.emptyList();
	}

	public boolean isSameHtml(String left, String right, String cssName) {
		List<Element> lefts = getVideoJsoupElement(left, cssName);
		List<Element> rights = getVideoJsoupElement(right, cssName);
		List<String> elementTextLeft = jsoupParseList.getElementText(lefts);
		List<String> elementTextRight = jsoupParseList.getElementText(rights);
		elementTextLeft.removeAll(elementTextRight);
		return ObjectUtils.isEmpty(elementTextLeft);
	}

	public List<Element> getVideoJsoupElement(String html, WebDriver webDriver) {
		List<Element> result = new ArrayList<>();
		if (ObjectUtils.isEmpty(html)) {
			return result;
		}
		String windowHandle = webDriver.getWindowHandle();
		String windowHandleTitle = webDriver.getTitle();
		List<List<Element>> elements = jsoupParseList.getElementsArray(html);
		for (List<Element> eles : elements) {
			for (Element ele : eles) {
				try {

					WebElement findElementByCssSelector = webDriver.findElement(By.cssSelector(ele.cssSelector()));
					Point location = findElementByCssSelector.getLocation();
					String script = "window.scrollTo(" + location.getX() + "," + (location.getY() - 200) + ")";
					SeleniumUtils.executeScript(webDriver, script);
					findElementByCssSelector.click();
					boolean isSwitch = switchCurrentWindowHandle(webDriver);

					VideoResource nodeVideo = seleniumParseNodeService
							.getNodeVideo(JsoupElementNodeRequest.builder().webDriver(webDriver).build());
					switchIndexWindowHandle(windowHandle, windowHandleTitle, isSwitch, webDriver);
					destoryNoneIndexWindowHandle(windowHandle, webDriver);
					if (!ObjectUtils.isEmpty(nodeVideo)) {
						log.info("getVideoJsoupElement is success nodeVideo is not empty css:{}", ele.cssSelector());
						return eles;
					}
				} catch (Exception e) {
					log.error("getVideoJsoupElement is error css:{}", ele.cssSelector());
					break;
				}
			}
		}
		return result;
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
}
