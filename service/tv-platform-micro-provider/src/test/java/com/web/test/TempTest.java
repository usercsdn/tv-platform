package com.web.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.taozi.tv.utils.FileUtils;

/**
 * @author xulijie
 * @date 2021/05/07
 */
public class TempTest {
	public static void main(String[] args) {
		String html = FileUtils.readFile("D://index.html");
		Document parse = Jsoup.parse(html);
		Elements elementsByTag = parse.getElementsByTag("style");
		for (Element el : elementsByTag) {
			// System.out.println(el.html());
		}
		Elements elementsByTag2 = parse.getElementsByTag("link");
		for (Element el : elementsByTag2) {
			if (el.attr("href").contains(".css")) {
				System.out.println("@import '" + el.attr("href") + "';");
			}

			// System.out.println(el.html());
		}

	}
}
