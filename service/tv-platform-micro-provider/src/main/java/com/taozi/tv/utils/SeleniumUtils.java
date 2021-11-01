package com.taozi.tv.utils;

import java.io.File;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/17
 */
@Slf4j
public class SeleniumUtils {
	private SeleniumUtils() {
	}

	public static boolean waitElementByCssSelector(WebDriver webDriver, String cssSelector, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, timeout);
			// 查找id为“cssSelector"的元素是否加载出来了（已经在页面DOM中存在）
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
			return true;
		} catch (Exception e) {
			log.error("waitElementByCssSelector is error,cssSelector:{}", cssSelector, e);
		}
		return false;
	}

	public static String isPageLoadSuccess(WebDriver webDriver) {
		for (int i = 0; i < 10; i++) {
			try {
				return webDriver.getPageSource();
			} catch (Exception e) {
				log.error("getUrl is error,msg:{}", e.getMessage());
				CommonUtils.sleep(1000);
			}
		}
		return "";
	}

	public static WebElement waitElementByTagName(WebDriver webDriver, String tagName, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, timeout);
			// 查找id为“cssSelector"的元素是否加载出来了（已经在页面DOM中存在）
			WebElement until = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(tagName)));
			return until;
		} catch (Exception e) {
			log.error("waitElementByTagName is error,tagName:{}", tagName, e);
		}
		return null;
	}

	public static boolean existElementByExecuteScript(WebDriver webDriver, String cssSelector) {
		try {
			String script = "return document.querySelector('" + cssSelector + "')";
			Object executeScript = ((JavascriptExecutor) webDriver).executeScript(script);
			return !ObjectUtils.isEmpty(executeScript);
		} catch (Exception e) {
			log.error("existElementByExecuteScript is error,cssSelector:{}", cssSelector, e);
		}
		return false;
	}

	public static boolean executeScript(WebDriver webDriver, String script) {
		try {
			Object executeScript = ((JavascriptExecutor) webDriver).executeScript(script);
			return !ObjectUtils.isEmpty(executeScript);
		} catch (Exception e) {
			log.error("executeScript is error,script:{}", script, e);
		}
		return false;
	}

	public static boolean executeClick(WebDriver webDriver, String cssSelector, int size) {
		for (int i = 0; i < size; i++) {
			try {
				WebElement findElementByCssSelector = webDriver.findElement(By.cssSelector(cssSelector));
				Point location = findElementByCssSelector.getLocation();
				String script = "window.scrollTo(" + location.getX() + "," + (location.getY() - 200) + ")";
				executeScript(webDriver, script);
				findElementByCssSelector.click();
				return true;
			} catch (Exception e) {
				log.error("executeScript is error index:{}", i, e);
			}
		}
		return false;
	}

	public static String executeScript(WebDriver webDriver, String script, Object... objs) {
		try {
			String result = "";
			Object executeScript = ((JavascriptExecutor) webDriver).executeScript(script, objs);
			if (!ObjectUtils.isEmpty(executeScript)) {
				result = Objects.toString(executeScript);
			}
			return result;
		} catch (Exception e) {
			log.error("executeScript is error,script:{}", script, e);
		}
		return "";
	}

	/**
	 * 全屏截图，用来调试无头模式
	 * 
	 * @param webDriver
	 * @return
	 */
	public static boolean screenshotAs(WebDriver webDriver) {
		try {
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			org.apache.commons.io.FileUtils.copyFile(scrFile,
					new File(CommonUtils.DOWNLOAD_ROOT_PATH + "a" + System.currentTimeMillis() + ".png"));
			return true;
		} catch (Exception e) {
			log.error("screenshotAs is error");
		}
		return false;
	}
}
