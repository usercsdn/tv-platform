package com.taozi.tv.pojo.parse;

import org.openqa.selenium.WebDriver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/03/15
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsoupElementNodeRequest {
	private WebDriver webDriver;
	@Builder.Default
	private String loadBrowserSuccessSign = "video";
	@Builder.Default
	private String loadVideoExcludeSign = "blob:http";
}
