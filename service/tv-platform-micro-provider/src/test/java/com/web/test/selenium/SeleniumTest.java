package com.web.test.selenium;

import java.util.Collections;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTest {
	public static void main(String[] args) {
		// System.getProperties().setProperty("webdriver.chrome.driver",
		// "E:\\爬虫测试\\chromedriver.exe");
		System.getProperties().setProperty("webdriver.chrome.driver", "D:\\work\\learn\\tv-platform\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("headless");
		options.addArguments("--disable-gpu");
		options.addArguments("disable-infobars");
		options.addArguments("--disable-extensions");
		options.addArguments("window-size=1200x600");
		options.addArguments("--no-sandbox");
		options.addArguments("--incognito");
		options.addArguments(
				"user-agent=Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
		options.addArguments("blink-settings=imagesEnabled=false");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

		ChromeDriver webDriver = new ChromeDriver(options);
		webDriver.get("https://www.weibo.com/tv/hot");
		String script = "Object.defineProperty(navigator,\"webdriver\",{get:() => false,});";
		// webDriver.close();
		// System.exit(0);
		WebDriverWait wait = new WebDriverWait(webDriver, 15);
		// 查找id为“kw"的元素是否加载出来了（已经在页面DOM中存在）
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("Frame_main1_1_6JQ")));
		String attribute = webDriver.getPageSource();
		System.out.println(attribute);
	}
}
