package com.taozi.tv.pojo.task;

import com.taozi.tv.dao.bean.VideoResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/04/21
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResourceDownloadEvent {
	private VideoResource videoResource;
	private int urlSize;
	private String taskUUID;
	private String downloadName;
}
