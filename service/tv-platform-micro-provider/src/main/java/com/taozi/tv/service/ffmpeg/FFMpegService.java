package com.taozi.tv.service.ffmpeg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Splitter;
import com.taozi.tv.pojo.ffmpeg.FFmpegMetabase;
import com.taozi.tv.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/18
 */
@Slf4j
@Service
public class FFMpegService {

	public static void main(String[] args) throws Exception {
		FFMpegService api = new FFMpegService();
		String downloadUrl = "1";
		String url = downloadUrl.replaceAll("=", "\\=");
		FFmpegMetabase videoMetabase = api.getVideoMetabase(url);
		System.out.println(videoMetabase);
	}

	public String ffmpegProcess(String command) {
		try {
			log.info("video to audio command : " + command);
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			InputStream in = process.getErrorStream();
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inputStreamReader);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			log.info("ffmpegProcess is error command:{}", command, e);
		}

		return "";
	}

	public FFmpegMetabase getVideoMetabase(String videoPath) {
		BufferedReader br = null;
		InputStreamReader inputStreamReader = null;
		InputStream in = null;
		// ffmepg工具地址
		// 视频文件地址
		// 拼接cmd命令语句
		StringBuffer buffer = new StringBuffer();
		buffer.append(CommonUtils.FFMPEG_PATH);
		// 注意要保留单词之间有空格
		buffer.append(" -i ");
		buffer.append(videoPath);
		// log.info("getVideoMetabase script:{}", buffer.toString());
		// buffer.append(" -show_entries format=duration -v quiet -of
		// csv=\"p=0\" ");
		FFmpegMetabase result = FFmpegMetabase.builder().build();
		// 执行命令语句并返回执行结果
		try {
			Process process = Runtime.getRuntime().exec(buffer.toString());
			// process.waitFor();
			in = process.getErrorStream();
			inputStreamReader = new InputStreamReader(in);
			br = new BufferedReader(inputStreamReader);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().startsWith("Duration:")) {
					String[] split = line.trim().split(",");
					for (String el : split) {
						List<String> temp = Splitter.on(": ").trimResults().omitEmptyStrings().splitToList(el); // 去前后空格&&去空string
						String key = temp.get(0);
						String value = temp.get(1);
						if (StringUtils.trim(key).startsWith("Duration")) {
							result.setDuration(getDuration(value));
						} else if (StringUtils.trim(key).startsWith("bitrate")) {
							result.setBitrate(
									Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(value).get(0));
						}
					}
				}
			}
			return result;
		} catch (Exception e) {
			log.error("getVideoMetabase is error ", e);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(inputStreamReader);
		}
		return null;
	}

	private String getDuration(String duration) {
		if (ObjectUtils.isEmpty(duration)) {
			return "";
		}
		String[] split = StringUtils.trim(duration).split("\\.");
		String[] time = split[0].split(":");
		long hour = TimeUnit.HOURS.toSeconds(NumberUtils.toLong(time[0]));
		long minutes = TimeUnit.MINUTES.toSeconds(NumberUtils.toLong(time[1]));
		long seconds = NumberUtils.toLong(time[2]);
		return Objects.toString(hour + minutes + seconds, "");
	}
}
