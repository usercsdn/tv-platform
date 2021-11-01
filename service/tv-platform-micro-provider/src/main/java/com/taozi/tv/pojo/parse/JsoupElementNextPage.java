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
public class JsoupElementNextPage {
	private WebDriver webDriver;
	private String nextPageText;
}
