package com.taozi.tv.pojo.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author jacks
 *
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexCacheInitEvent {
	private String taskUUID;
}
