package com.taozi.tv.pojo.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/04/20
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class M3u8DownloadEvent {
	private String taskUUID;
	private String downloadFileName;
	private String tsUrl;
	private int retryCount;
	private String mergeVideoName;
	private String duration;
}
