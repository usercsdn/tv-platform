package com.taozi.tv.service.parse;

import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.pojo.parse.JsoupElementNextPage;
import com.taozi.tv.utils.SeleniumUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeleniumParseNextPageService {
	@Autowired
	private JsoupParse jsoupParse;
	@Autowired
	private SeleniumPositionService seleniumPositionService;

	public boolean getNextPage(JsoupElementNextPage nodeRequest, boolean isFrist, String cssName) {
		WebDriver webDriver = nodeRequest.getWebDriver();
		String nextPageText = nodeRequest.getNextPageText();

		try {
			Element element = null;
			String html = SeleniumUtils.isPageLoadSuccess(webDriver);
			if (!ObjectUtils.isEmpty(nextPageText)) {
				element = jsoupParse.searchText(html, nextPageText);
			}
			if (!ObjectUtils.isEmpty(element)) {
				String cssSelector = element.cssSelector();
				WebElement findElement = webDriver.findElement(By.cssSelector(cssSelector));
				findElement.click();
				SeleniumUtils.waitElementByCssSelector(webDriver, cssSelector, 10);
			} else {
				for (int i = 0; i < 20; i++) {
					SeleniumUtils.executeScript(webDriver, "window.scrollBy(0,500);");
				}
			}
			if (!isFrist) {
				for (int i = 0; i < 10; i++) {
					String newHtml = SeleniumUtils.isPageLoadSuccess(webDriver);
					if (seleniumPositionService.isSameHtml(newHtml, html, cssName)) {
						Thread.sleep(1000L);
					} else {
						break;
					}
				}
			}

			return true;
		} catch (Exception e) {
			log.error("getNodeVideo is error", e);
		}
		return false;
	}
}
