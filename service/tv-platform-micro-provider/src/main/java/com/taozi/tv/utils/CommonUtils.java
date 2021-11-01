package com.taozi.tv.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/19
 */
@Slf4j
public class CommonUtils {
	private CommonUtils() {
	}

	public static String DOWNLOAD_ROOT_PATH = "E:" + File.separator + "download" + File.separator;
	public static String CHROMEDRIVER_PATH = "E:\\work\\temp\\tv-platform\\chromedriver.exe";
	public static String FFMPEG_PATH = "F:\\soft\\work\\ffmpeg-full\\bin\\ffmpeg.exe";

	static {
		String os = System.getProperty("os.name");
		boolean isWin = os.toLowerCase().startsWith("win");
		if (!isWin) {
			FFMPEG_PATH = "/usr/local/soft/ffmpeg/ffmpeg-install-4.4-ssl/bin/ffprobe";
			DOWNLOAD_ROOT_PATH = "/usr/local/soft/video/download/";
			CHROMEDRIVER_PATH = "/usr/bin/chromedriver";
		} else {
			String mac = getMac();
			if (mac.equals("F8-A2-D6-DA-00-C5")) {
				DOWNLOAD_ROOT_PATH = "D:" + File.separator + "download" + File.separator;
				FFMPEG_PATH = "D:\\soft\\ffmpeg-4.4-full_build\\bin\\ffmpeg.exe";
				CHROMEDRIVER_PATH = "D:\\work\\learn\\tv-platform\\chromedriver.exe";
			}
		}
	}

	public static final String CHROMEDRIVER_REMOTE_PATH = "http://127.0.0.1:9515";

	public static final int ACTIVE_ON = 0;
	public static final int ACTIVE_OFF = 1;

	public static final int RESPONSE_SUCCESS = 0;
	public static final int RESPONSE_FAILED = 1;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();;

	private static Set<String> set = new HashSet<>();

	private static List<String> pattern = Lists.newArrayList("[^\u4e00-\u9fa5]", "[^a-z^A-Z\\s]", "[^0-9]");

	public static void main(String[] args) {
		System.out.println(formatSeconds(90));
	}

	static {
		set.add(".mp4");
		set.add(".mkv");
		set.add(".webm");
		set.add(".gif");
		set.add(".mov");
		set.add(".ogg");
		set.add(".flv");
		set.add(".avi");
		set.add(".3gp");
		set.add(".wmv");
		set.add(".mpg");
		set.add(".vob");
		set.add(".swf");
		set.add(".m3u8");
	}

	public static <T> String toJson(T clz) {
		return gson.toJson(clz);
	}

	public static <T> T toObject(String data, Class<T> clz) {
		return gson.fromJson(data, clz);
	}

	public static File createFile(String filePath) {
		try {
			String flag = File.separator;
			String root = filePath.substring(0, filePath.lastIndexOf(flag));
			File fileRoot = new File(root);
			if (!fileRoot.exists()) {
				fileRoot.mkdirs();
			}
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			log.error("createFile is error filePath:{}", filePath, e);
		}
		return null;
	}

	public static boolean isHttpUrl(String str) {
		if (ObjectUtils.isEmpty(str)) {
			return false;
		}
		str = str.trim();
		return str.matches("^(http|https)://.+");
	}

	public static String getMediaFormat(String url) {
		if (!isHttpUrl(url)) {
			log.info("不是一个完整URL链接！url:{}", url);
			return "";
		}
		url = url.substring(url.lastIndexOf("/") - 1);
		for (String s : set) {
			if (url.contains(s))
				return s;
		}
		log.info("非视频链接！url:{}", url);
		return "";
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			log.error("sleep is error", e);
		}
	}

	public static String getText2(String text, String textFilter) {
		List<String> split = null;
		if (!ObjectUtils.isEmpty(textFilter)) {
			split = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(textFilter); // 去前后空格&&去空string
		}
		StringBuilder sb = new StringBuilder();
		for (String el : pattern) {
			String value = text.replaceAll(el, "");
			if (!ObjectUtils.isEmpty(value)) {
				if (!ObjectUtils.isEmpty(split)) {
					for (String ele : split) {
						value = value.replace(ele, "");
					}
				}
				if (!ObjectUtils.isEmpty(value)) {
					sb.append(value);
				}
			}
		}
		String string = sb.toString();
		if (!ObjectUtils.isEmpty(string)) {
			return string;
		}
		return "";
	}

	public static String getText(String text, String textFilter, boolean allFilter) {
		List<String> split = null;
		if (!ObjectUtils.isEmpty(textFilter)) {
			split = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(textFilter); // 去前后空格&&去空string
		}
		StringBuilder sb = new StringBuilder();
		for (String el : pattern) {
			String value = text.replaceAll(el, "");
			if (!ObjectUtils.isEmpty(value)) {
				if (!ObjectUtils.isEmpty(split)) {
					for (String ele : split) {
						value = value.replace(ele, "");
					}
				}
				if (!ObjectUtils.isEmpty(value)) {
					if (!allFilter) {
						sb.append(value);
					} else {
						return value;
					}
				}
			}
		}
		String string = sb.toString();
		if (!ObjectUtils.isEmpty(string)) {
			return string;
		}
		return "";
	}

	public static String getMac() {
		InetAddress ia;
		byte[] mac = null;
		try {
			ia = InetAddress.getLocalHost();
			mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}

		return sb.toString().toUpperCase();
	}

	public static String formatSeconds(long seconds) {
		String standardTime;
		if (seconds <= 0) {
			standardTime = "00:00";
		} else if (seconds < 60) {
			standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
		} else if (seconds < 3600) {
			standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
		} else {
			standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60,
					seconds % 60);
		}
		return standardTime;
	}

}
