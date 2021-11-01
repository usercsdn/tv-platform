package com.taozi.tv.service.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.pojo.HttpInputStreamResponse;
import com.taozi.tv.pojo.ffmpeg.M3u8DownloadBuilder;
import com.taozi.tv.pojo.task.M3u8DownloadEvent;
import com.taozi.tv.pojo.task.VideoDownloadFinishedEvent;
import com.taozi.tv.pojo.task.VideoResourceDownloadEvent;
import com.taozi.tv.pojo.task.VideoResourceUpdatePushEvent;
import com.taozi.tv.utils.CommonUtils;
import com.taozi.tv.utils.FileUtils;
import com.taozi.tv.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/19
 */
@Slf4j
@Service
public class M3u8DownloadTaskService {
	@Autowired
	private WebSiteTaskListener webSiteTaskListener;

	@Autowired
	private EventBusCenter eventBusCenter;

	@PostConstruct
	public void init() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	public static void main(String[] args) {
		String str = "<p style=\"text-align:center;color:#3E3E3E;font-family:&quot;font-size:14px;background-color:#FFFFFF;\">卡死加到风控【】		<img class=\"\" src=\"http://mmbiz.qpic.cn/mmbiz_jpg/A2cvk4ol9EQibp95ia2CUiaXfichh8Z1q2ia96F3mXRGqI9tbsNFsqZR8jHvkeibPT7FWGnHl4ic55AUT5hBoXRs2tHQw/640?wx_fmt=jpeg&amp;tp=webp&amp;wxfrom=5&amp;wx_lazy=1\" style=\"height:auto !important;width:236px !important;\" />		</p>		<p style=\"color:#3E3E3E;font-family:&quot;font-size:14px;background-color:#FFFFFF;text-align:justify;\">		<span style=\"font-size:16px;\">&nbsp; &nbsp; &nbsp; &nbsp; 教授，博士生导师。现任山东大学校长。兼任教育部经济学教学指导委员会委员、教育部专业设置与专业建设委员会委员、中国财政学会和中国税务学会常务理事等职，享受国务院政府特殊津贴专家。主要研究方向为税收基本理论、公共品供给、地方财政运行及公共政策等。</span></p>;";
		String reg = "[^\u4e00-\u9fa5]";
		reg = "[^a-z^A-Z]";
		reg = "[^0-9]";
		str = str.replaceAll(reg, "");
		System.out.println(str);
	}

	private M3u8DownloadBuilder getTsUrl(String m3u8Url) {
		String value = HttpUtils.get(m3u8Url, null);
		// 判断是否是m3u8链接
		if (!value.contains("#EXTM3U")) {
			return null;
		}
		List<String> tsUrls = new LinkedList<>();
		String[] split = value.split("\\n");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			if (s.contains("#EXTINF")) {
				String s1 = split[++i];
				tsUrls.add(CommonUtils.isHttpUrl(s1) ? s1 : mergeUrl(m3u8Url, s1));
			}
		}
		return M3u8DownloadBuilder.builder().tsUrls(tsUrls).m3u8Url(m3u8Url).build();
	}

	public String start(VideoResourceDownloadEvent event) {
		VideoResource data = event.getVideoResource();

		String m3u8Url = data.getVideoUrl();
		if (".m3u8".compareTo(CommonUtils.getMediaFormat(m3u8Url)) != 0) {
			log.info(m3u8Url + "不是一个完整m3u8链接！");
			return "";
		}
		String videoName = event.getDownloadName();
		M3u8DownloadBuilder builder = getTsUrl(m3u8Url);
		if (ObjectUtils.isEmpty(builder)) {
			log.info("getTsUrl is failed videoName:{}", videoName);
			eventBusCenter
					.post(VideoResourceUpdatePushEvent.builder().videoResource(data).videoUrlIsExpired(true).build());
			return "";
		}
		List<String> tsUrls = builder.getTsUrls();
		String taskUUID = UUID.randomUUID().toString();
		String separator = File.separator;
		String outFilePath = CommonUtils.DOWNLOAD_ROOT_PATH + videoName + separator + "readme.txt";
		event.setTaskUUID(taskUUID);
		event.setUrlSize(tsUrls.size());
		FileUtils.putValueTocacheFile(outFilePath, CommonUtils.toJson(event), false);
		webSiteTaskListener.register(taskUUID, tsUrls.size());
		for (int i = 0; i < tsUrls.size(); i++) {
			String downloadFileName = CommonUtils.DOWNLOAD_ROOT_PATH + videoName + separator + "ts" + separator + i
					+ ".ts";
			M3u8DownloadEvent metabase = M3u8DownloadEvent.builder().retryCount(50).taskUUID(taskUUID)
					.tsUrl(tsUrls.get(i)).downloadFileName(downloadFileName).mergeVideoName(videoName).build();
			eventBusCenter.post(metabase);
		}
		return taskUUID;
	}

	private String mergeUrl(String start, String end) {
		if (end.startsWith("/")) {
			end = end.replaceFirst("/", "");
		}
		String relativeUrl = "";
		if (start.contains(".m3u8")) {
			relativeUrl = start.substring(0, start.lastIndexOf("/") + 1);
		}
		int position = 0;
		String subEnd = "";
		String tempEnd = end;
		while ((position = end.indexOf('/', position)) != -1) {
			subEnd = end.substring(0, position + 1);
			if (start.endsWith(subEnd)) {
				tempEnd = end.replaceFirst(subEnd, "");
				break;
			}
			++position;
		}
		return relativeUrl + tempEnd;
	}

	@Subscribe
	@AllowConcurrentEvents
	public String downloadM3u8TS(M3u8DownloadEvent event) {
		// 重试次数判断
		int retryCount = event.getRetryCount();
		String path = event.getDownloadFileName();
		int count = 0;
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		HttpInputStreamResponse resp = null;
		while (count <= retryCount) {
			try {
				count++;
				resp = HttpUtils.getInputStream(event.getTsUrl(), null);
				inputStream = resp.getInput();
				File file = CommonUtils.createFile(path);
				if (ObjectUtils.isEmpty(file)) {
					log.error("taskDispose file is null path:{}", path);
					return "";
				}
				log.info("downloadM3u8TS path:{}", path);
				outputStream = new FileOutputStream(file);
				IOUtils.copy(inputStream, outputStream);
				IOUtils.closeQuietly(inputStream);
				IOUtils.closeQuietly(outputStream);
				if (ObjectUtils.isEmpty(resp)) {
					IOUtils.closeQuietly(resp.getResponse());
				}
				String filePathRoot = file.getParent();
				webSiteTaskListener.pushFinishedEvent(event.getTaskUUID(), VideoDownloadFinishedEvent.builder()
						.mergeVideoName(event.getMergeVideoName()).filePathRoot(filePathRoot).build());
				break;
			} catch (Exception e) {
				log.info("taskDispose is failed retryCount:{},count:{}, path:{}", retryCount, count, path);
			} finally {
				IOUtils.closeQuietly(inputStream);
				IOUtils.closeQuietly(outputStream);
				if (ObjectUtils.isEmpty(resp)) {
					IOUtils.closeQuietly(resp.getResponse());
				}
			}
		}
		return null;
	}

}
