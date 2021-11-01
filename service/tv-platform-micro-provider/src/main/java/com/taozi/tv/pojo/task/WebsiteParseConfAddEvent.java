package com.taozi.tv.pojo.task;

import org.openqa.selenium.WebDriver;

import com.taozi.tv.dao.bean.WebsiteParseConf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = { "webDriver" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebsiteParseConfAddEvent {
	private WebDriver webDriver;
	private WebsiteParseConf parseConf;
}
