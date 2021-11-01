package com.taozi.tv.pojo.parse;

import java.util.List;

import org.jsoup.nodes.Element;

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
public class JsoupElementList {
	private String cssName;
	private List<Element> elements;

}
