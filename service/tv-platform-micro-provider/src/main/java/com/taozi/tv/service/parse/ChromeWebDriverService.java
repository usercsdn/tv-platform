package com.taozi.tv.service.parse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;
import com.taozi.tv.utils.CommonUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChromeWebDriverService {
	@SneakyThrows
	public static void main(String[] args) {
		ChromeWebDriverService api = new ChromeWebDriverService();
		// api.createAndStartService();
		// WebDriver chromeDriver = api.getChromeDriver();
		//
		// chromeDriver.get("https://market.finance.sina.com.cn/transHis.php?symbol=sh600928&date=2021-09-23&page=5");
		// System.out.println();
		api.test();
	}

	@SneakyThrows
	private void test() {
		createAndStartService();
		WebDriver chromeDriver = getChromeDriver();
		ArrayList<String> codes = Lists.newArrayList("sh603155", "sh600691", "sh603033", "sh600273", "sh600328");

		for (String code : codes) {
			for (int i = 0; i < 60; i++) {
				chromeDriver.get("https://market.finance.sina.com.cn/transHis.php?symbol=" + code
						+ "&date=2021-09-23&page=" + i);
				System.out.println(code + ":" + i);
				Thread.sleep(1000L);
			}

		}
	}

	private static int CHROMEDRIVER_SIZE = 1;
	static {
		String os = System.getProperty("os.name");
		boolean isWin = os.toLowerCase().startsWith("win");
		if (!isWin) {
			CHROMEDRIVER_SIZE = 3;
		}
	}
	private BlockingQueue<WebDriver> webDriverQueue = new LinkedBlockingQueue<>(CHROMEDRIVER_SIZE);
	private BlockingQueue<ChromeDriverService> chromeDriverServices = new LinkedBlockingQueue<>(CHROMEDRIVER_SIZE);

	public void createAndStartServiceRemote() throws IOException {
		ChromeOptions chromeOptions = getChromeOptions();
		for (int i = 0; i < CHROMEDRIVER_SIZE; i++) {
			try {
				WebDriver driver = new RemoteWebDriver(new URL(CommonUtils.CHROMEDRIVER_REMOTE_PATH), chromeOptions);
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				webDriverQueue.put(driver);
				log.info("createAndStartService is finished size:{}", i);
			} catch (Exception e) {
				log.error("createAndStartService is error", e);
			}
		}
	}

	@PostConstruct
	public void createAndStartService() throws IOException {
		ChromeOptions chromeOptions = getChromeOptions();
		for (int i = 0; i < CHROMEDRIVER_SIZE; i++) {
			try {
				ChromeDriverService service = new ChromeDriverService.Builder()
						.usingDriverExecutable(new File(CommonUtils.CHROMEDRIVER_PATH)).usingAnyFreePort().build();
				service.start();
				chromeDriverServices.add(service);
				WebDriver driver = new ChromeDriver(service, chromeOptions);
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				webDriverQueue.put(driver);
				log.info("createAndStartService is finished size:{}", i);
			} catch (Exception e) {
				log.error("createAndStartService is error", e);
			}
		}
	}

	public void destoryWebDriver(WebDriver driver) {
		driver.close();
	}

	public void createWebDriver() {
		ChromeDriverService service = null;
		try {
			ChromeOptions chromeOptions = getChromeOptions();
			service = chromeDriverServices.take();
			WebDriver driver = new ChromeDriver(service, chromeOptions);
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			webDriverQueue.put(driver);
		} catch (Exception e) {
			log.error("createWebDriver is error", e);
		} finally {
			try {
				chromeDriverServices.put(service);
			} catch (Exception e) {
				log.error("createWebDriver is error", e);
			}
		}
	}

	private ChromeOptions getChromeOptions() {
		try {
			log.info("SeleniumParseIndexService getChromeOptions webDriver begin");

			ChromeOptions options = new ChromeOptions();
			String os = System.getProperty("os.name");
			boolean headless = os.toLowerCase().startsWith("win");
			if (headless) {
				// options.addArguments("headless");
				// options.addArguments("--start-maximized");
				options.addArguments("--window-size=4000,1600");
			} else {
				options.addArguments("headless");
				options.addArguments("--window-size=4000,1600");
			}
			options.addArguments("--disable-gpu");
			options.addArguments("--disable-popup-blocking");
			options.addArguments("--disable-infobars");
			options.addArguments("--disable-extensions");
			options.addArguments("--no-sandbox");
			options.addArguments("--incognito");
			options.addArguments("--disable-blink-features");
			options.addArguments("--disable-blink-features=AutomationControlled");

			options.addArguments(
					"user-agent=Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
			options.addArguments("blink-settings=imagesEnabled=false");
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			// options.addArguments("--auto-open-devtools-for-tabs");
			// 加载adblock 插件
			// options.addArguments(
			// "load-extension=C:/Users/jacks/AppData/Local/Google/Chrome/User
			// Data/Default/Extensions/cfhdojbkjhnklbpkdaibdccddilifddb/3.4.2_0");

			DesiredCapabilities caps = DesiredCapabilities.chrome();
			LoggingPreferences loggingPreferences = new LoggingPreferences();
			loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
			loggingPreferences.enable(LogType.BROWSER, Level.ALL);
			caps.setCapability(ChromeOptions.CAPABILITY, options);
			// caps.setCapability(CapabilityType.LOGGING_PREFS,
			// loggingPreferences);
			// 这里将pageLoadStrategy设置为none表示费阻塞模式，加载的最大时间到就会继续执行。
			// caps.setCapability("pageLoadStrategy", "none");
			caps.setCapability("goog:loggingPrefs", loggingPreferences);
			options.merge(caps);

			log.info("SeleniumParseIndexService getChromeOptions webDriver finished");
			return options;
		} catch (Exception e) {
			log.error("getChromeOptions is error", e);
		}
		return null;
	}

	public WebDriver getChromeDriver() {
		try {
			log.info("getChromeDriver is begin queue Size:{}", webDriverQueue.size());
			WebDriver take = webDriverQueue.take();
			log.info("getChromeDriver is finished queue Size:{}", webDriverQueue.size());
			return take;
		} catch (Exception e) {
			log.error("getChromeDriver is error", e);
		}
		return null;
	}

	public WebDriver getChromeDriverAndAdd() {
		try {
			log.info("getChromeDriver is begin queue Size:{}", webDriverQueue.size());
			if (webDriverQueue.size() <= 0) {

			}
			WebDriver take = webDriverQueue.take();
			log.info("getChromeDriver is finished queue Size:{}", webDriverQueue.size());
			return take;
		} catch (Exception e) {
			log.error("getChromeDriver is error", e);
		}
		return null;
	}

	public void putChromeDriver(WebDriver take) {
		try {
			if (ObjectUtils.isEmpty(take)) {
				return;
			}
			log.info("releaseChromeDriver is begin queue Size:{}", webDriverQueue.size());
			webDriverQueue.put(take);
			log.info("releaseChromeDriver is finished queue Size:{}", webDriverQueue.size());
		} catch (Exception e) {
			log.error("getChromeDriver is error", e);
		}
	}
}
