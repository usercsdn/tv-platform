package com.taozi.tv.service.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taozi.tv.pojo.task.VideoDownloadFinishedEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/20
 */
@Slf4j
@Service
public class WebSiteTaskListener {

	@Autowired
	private DonwloadVideoTaskService donwloadVideoTaskService;

	private Map<String, Integer> taskMap = new HashMap<>(500);

	public void register(String taskUUID, Integer totalTaskSize) {
		if (taskMap.containsKey(taskUUID)) {
			log.info("register is failed task is exist,taskUUID:{}", taskUUID);
			return;
		}
		taskMap.put(taskUUID, totalTaskSize);
	}

	public void pushFinishedEvent(String taskUUID, Object event) {
		if (!taskMap.containsKey(taskUUID)) {
			log.info("pushFinishedEvent is failed task not exist,taskUUID:{}", taskUUID);
			return;
		}
		Integer value = taskMap.get(taskUUID);
		taskMap.put(taskUUID, --value);
		log.info("pushFinishedEvent value:{}", value);
		if (value <= 0) {
			// eventBusCenter.post(event);
			donwloadVideoTaskService.videoDownloadFinished((VideoDownloadFinishedEvent) event);
			taskMap.remove(taskUUID);
		}
	}

	public Boolean isFinished(String taskUUID) {
		if (!taskMap.containsKey(taskUUID)) {
			log.info("isFinished is failed task not exist,taskUUID:{}", taskUUID);
			return true;
		}
		Integer value = taskMap.get(taskUUID);
		return value <= 0;
	}
}
