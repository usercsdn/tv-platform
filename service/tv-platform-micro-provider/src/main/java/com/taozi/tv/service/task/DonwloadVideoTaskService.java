package com.taozi.tv.service.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.pojo.ffmpeg.FFmpegMetabase;
import com.taozi.tv.pojo.task.VideoDownloadFinishedEvent;
import com.taozi.tv.pojo.task.VideoResourceDownloadEvent;
import com.taozi.tv.service.ffmpeg.FFMpegService;
import com.taozi.tv.utils.CommonUtils;
import com.taozi.tv.utils.DownloadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/19
 */
@Slf4j
@Service
public class DonwloadVideoTaskService {

	@Autowired
	private FFMpegService fFMpegService;
	@Autowired
	private M3u8DownloadTaskService m3u8DownloadTaskService;

	@Autowired
	private EventBusCenter eventBusCenter;

	@PostConstruct
	public void init() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void videoDownloadFinished(VideoDownloadFinishedEvent event) {
		String mergeTs = mergeTs(event);
		// String ffMpegCommand = getFFMpegCommand(mergeTs);
		// FileUtils.putValueTocacheFile(CommonUtils.DOWNLOAD_ROOT_PATH +
		// "shell.txt", ffMpegCommand, true);
		if (!ObjectUtils.isEmpty(mergeTs)) {
			deleteFiles(event);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void downloadVideoResource(VideoResourceDownloadEvent event) {
		VideoResource videoResource = event.getVideoResource();
		Integer duration = videoResource.getDuration();
		if (ObjectUtils.isEmpty(duration) || duration > 60 * 30) {
			log.info("video is too large while return videoName:{}", videoResource);
			return;
		}
		Long id = videoResource.getId();
		String text = videoResource.getName();
		String videoName = text + "_" + id + "_" + videoResource.getDuration();
		event.setDownloadName(videoName);
		if (checkVideoResourceIsExist(event)) {
			log.info("downloadVideoResource is exist videoName:{}", videoName);
			return;
		}
		downloadM3u8(event);
		downloadNoneM3u8(event);
	}

	private boolean checkVideoResourceIsExist(VideoResourceDownloadEvent event) {
		String videoName = event.getDownloadName();
		String outputPath = CommonUtils.DOWNLOAD_ROOT_PATH + videoName;
		File file = new File(outputPath);
		return file.exists();
	}

	private String mergeTs(VideoDownloadFinishedEvent event) {
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		try {
			File tsFile = new File(event.getFilePathRoot());
			String fileVideoName = event.getMergeVideoName();
			String path = CommonUtils.DOWNLOAD_ROOT_PATH + fileVideoName + ".mp4";
			File file = new File(path);
			System.gc();
			fileOutputStream = new FileOutputStream(file);
			File[] finishedFiles = tsFile.listFiles();
			List<File> list = new ArrayList<>(Arrays.asList(finishedFiles));
			sortFile(list);
			for (File f : list) {
				fileInputStream = new FileInputStream(f);
				IOUtils.copy(fileInputStream, fileOutputStream);
			}
			return path;
		} catch (Exception e) {
			log.error("mergeTs is error", e);
		} finally {
			IOUtils.closeQuietly(fileOutputStream);
			IOUtils.closeQuietly(fileInputStream);
			System.gc();
		}
		return "";
	}

	private void sortFile(List<File> list) {
		Collections.sort(list, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				Integer name1 = Integer.valueOf(o1.getName().substring(0, o1.getName().indexOf(".")));
				Integer name2 = Integer.valueOf(o2.getName().substring(0, o2.getName().indexOf(".")));
				return name1.compareTo(name2);
			}
		});
	}

	private void deleteFiles(VideoDownloadFinishedEvent event) {
		String path = event.getFilePathRoot();
		File file = new File(path);
		File[] listFiles = file.listFiles();
		System.out.println(listFiles.length);
		for (File f : file.listFiles()) {
			if (f.getName().endsWith(".ts")) {
				f.delete();
			}
		}
	}

	private void downloadM3u8(VideoResourceDownloadEvent event) {
		try {
			if (!event.getVideoResource().getType().equals(".m3u8")) {
				return;
			}
			m3u8DownloadTaskService.start(event);
		} catch (Exception e) {
			log.error("downloadM3u8 is error", e);
		}
	}

	private String getFFMpegCommand(String filePath) {
		FFmpegMetabase videoMetabase = fFMpegService.getVideoMetabase(filePath);
		Double bitrate = null;
		if (!ObjectUtils.isEmpty(videoMetabase) && !ObjectUtils.isEmpty(videoMetabase.getBitrate())) {
			bitrate = Double.valueOf(videoMetabase.getBitrate());
		}
		if (ObjectUtils.isEmpty(bitrate)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(CommonUtils.FFMPEG_PATH).append(" -i \"concat:").append(filePath);
		sb.append("\" ");
		bitrate = NumberUtils.toScaledBigDecimal(bitrate * 0.6, 2, RoundingMode.DOWN).doubleValue();
		sb.append(" -threads 25 -preset ultrafast -strict -2 ");
		sb.append(" -b:v ").append(bitrate).append("k -bufsize ").append(bitrate).append("k ");
		filePath = filePath.substring(0, filePath.lastIndexOf("."));
		sb.append(filePath).append("_compress").append(".mp4");
		return sb.toString();
	}

	private void downloadNoneM3u8(VideoResourceDownloadEvent event) {
		try {
			VideoResource videoResource = event.getVideoResource();
			if (videoResource.getType().equals(".m3u8")) {
				return;
			}
			String videoName = event.getDownloadName();
			String outputPath = CommonUtils.DOWNLOAD_ROOT_PATH + videoName + File.separator + videoName
					+ videoResource.getType();
			DownloadUtils downloadTask = new DownloadUtils(videoResource.getVideoUrl(), outputPath, 20, null);
			downloadTask.startDown();
		} catch (Exception e) {
			log.error("downloadNoneM3u8 is error", e);
		}

	}

}
