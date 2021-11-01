package com.taozi.tv.pojo.task;

import org.openqa.selenium.WebDriver;

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
@ToString(exclude = { "webDriver" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResourceUpdateEvent {
	private WebDriver webDriver;
	private VideoResource videoResource;
}
