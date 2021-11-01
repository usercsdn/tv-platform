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
public class VideoDownloadFinishedEvent {
	private String mergeVideoName;
	private String filePathRoot;
}
