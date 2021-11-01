package com.taozi.tv.service.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.pojo.parse.JsoupElementList;
import com.taozi.tv.utils.FileUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/12
 */
@Slf4j
@Service
public class JsoupParse {

	public Element getElement(String html, String cssSelect) {
		Document parse = Jsoup.parse(html);
		Elements select = parse.select(cssSelect);
		if (ObjectUtils.isEmpty(select)) {
			return null;
		}
		return select.get(0);
	}

	public Element searchText(String html, String text) {
		Document parse = Jsoup.parse(html);
		return searchText(parse.children(), text);
	}

	private Element searchText(Elements ele, String text) {
		if (ObjectUtils.isEmpty(ele)) {
			return null;
		}
		for (Element el : ele) {
			if (el.text().equals(text) && ObjectUtils.isEmpty(el.children())) {
				return el;
			} else {
				Element searchText = searchText(el.children(), text);
				if (!ObjectUtils.isEmpty(searchText)) {
					return searchText;
				}
			}
		}
		return null;
	}

	private Element getParent(Element ele) {
		Element parent = ele.parent();
		if (ObjectUtils.isEmpty(parent)) {
			return null;
		}
		String className = parent.className();
		if (!ObjectUtils.isEmpty(className)) {
			return parent;
		} else {
			Element result = getParent(parent);
			if (!ObjectUtils.isEmpty(parent)) {
				return result;
			}
		}
		return null;
	}

	private List<Element> getImageElements(Elements elements) {
		List<Element> result = new ArrayList<>();
		if (ObjectUtils.isEmpty(elements)) {
			return result;
		}
		for (Element ele : elements) {
			Attributes attributes = ele.attributes();
			for (Attribute el : attributes) {
				String value = el.getValue();
				if (value.contains(".jpg") || value.contains(".png") || value.contains(".gif")
						|| value.contains(".jpeg") || value.contains(".JPG") || value.contains(".svg")) {
					result.add(ele);
				}
			}
			List<Element> imageElements = getImageElements(ele.children());
			if (!ObjectUtils.isEmpty(imageElements)) {
				result.addAll(imageElements);
			}
		}
		return result;
	}

	public String getImage(Elements elements) {
		if (ObjectUtils.isEmpty(elements)) {
			return "";
		}
		for (Element ele : elements) {
			Attributes attributes = ele.attributes();
			for (Attribute el : attributes) {
				String value = el.getValue();
				if (value.contains(".jpg") || value.contains(".png") || value.contains(".gif")
						|| value.contains(".jpeg") || value.contains(".JPG") || value.contains(".svg")) {
					return value;
				}
			}
			String image = getImage(ele.children());
			if (!ObjectUtils.isEmpty(image)) {
				return image;
			}
		}
		return "";
	}

	public String getText(Elements elements) {
		if (ObjectUtils.isEmpty(elements)) {
			return "";
		}
		for (Element ele : elements) {
			if (ele.hasText()) {
				return ele.text();
			}
			String image = getImage(ele.children());
			if (!ObjectUtils.isEmpty(image)) {
				return image;
			}
		}
		return "";
	}

	public List<String> getElementText(List<Element> elements) {
		List<String> result = new ArrayList<>();
		for (Element e : elements) {
			result.add(e.text());
		}
		return result;

	}

	public List<Element> getElements(String html) {
		Document parse = Jsoup.parse(html);
		List<Element> select = getImageElements(parse.children());
		Map<String, JsoupElementList> map = new HashMap<>();

		for (Element s : select) {
			Element parent = getParent(s);
			if (ObjectUtils.isEmpty(parent)) {
				continue;
			}
			String className = parent.className();
			JsoupElementList value = map.get(className);
			if (ObjectUtils.isEmpty(value)) {
				value = JsoupElementList.builder().elements(new ArrayList<Element>()).build();
			}
			value.getElements().add(parent);
			map.put(className, value);
		}
		JsoupElementList sort2Element = getSort2Element(map);
		if (ObjectUtils.isEmpty(sort2Element)) {
			return Collections.emptyList();
		}
		map.clear();
		String cssName = sort2Element.getCssName();
		log.info("cssName:{},size:{}", cssName, sort2Element.getElements().size());
		List<Element> els = new ArrayList<>();

		for (Element ele : sort2Element.getElements()) {

			Element elementList = getElementList(ele, cssName);
			if (!ObjectUtils.isEmpty(elementList) && !els.contains(elementList)) {
				els.add(elementList);
			}
		}
		return els;
	}

	public static void main(String[] args) {
		JsoupParse api = new JsoupParse();
		List<List<Element>> elements2 = api.getElementsArray(FileUtils.readFile("D://index2.html"));
		elements2.forEach(item -> {
			System.out.println(item.size());
		});
	}

	public List<List<Element>> getElementsArray(String html) {
		Document parse = Jsoup.parse(html);
		List<Element> select = getImageElements(parse.children());
		Map<String, JsoupElementList> map = new HashMap<>();

		for (Element s : select) {
			Element parent = getParent(s);
			if (ObjectUtils.isEmpty(parent)) {
				continue;
			}
			String className = parent.className();
			JsoupElementList value = map.get(className);
			if (ObjectUtils.isEmpty(value)) {
				value = JsoupElementList.builder().elements(new ArrayList<Element>()).build();
			}
			value.getElements().add(parent);
			map.put(className, value);
		}
		List<List<Element>> result = new ArrayList<>();

		for (Entry<String, JsoupElementList> el : map.entrySet()) {
			String cssName = el.getKey();
			List<Element> value = el.getValue().getElements();
			List<Element> els = new ArrayList<>();
			for (Element ele : value) {
				Element elementList = getElementList(ele, cssName);
				if (!ObjectUtils.isEmpty(elementList) && !els.contains(elementList)) {
					els.add(elementList);
				}
			}
			result.add(els);
		}
		result.sort((a, b) -> b.size() - (a.size()));

		return result;
	}

	private JsoupElementList getSort2Element(Map<String, JsoupElementList> map) {
		if (ObjectUtils.isEmpty(map)) {
			return null;
		}
		List<Map.Entry<String, JsoupElementList>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, JsoupElementList>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, JsoupElementList> o1, Entry<String, JsoupElementList> o2) {
				return o2.getValue().getElements().size() - o1.getValue().getElements().size();
			}
		});
		Entry<String, JsoupElementList> entry = list.get(0);
		entry.getValue().setCssName(entry.getKey());
		return entry.getValue();
	}

	private Element getElementList(Element ele, String cssName) {
		while (!ObjectUtils.isEmpty(getParent(ele))) {
			Element parent = ele.parent();
			String classNameFlag = parent.className();
			Elements siblingElements = parent.siblingElements();
			int sampleSize = 0;
			for (Element el : siblingElements) {
				String className = el.className();
				if (className.contains(classNameFlag) && classNameFlag.contains(className)) {
					sampleSize++;
				}
			}
			if (siblingElements.size() / 2 < sampleSize && sampleSize > 0) {
				String replaceAll = cssName.replaceAll(" ", "\\.");
				Elements checkSize = parent.select("." + replaceAll);
				if (!ObjectUtils.isEmpty(checkSize) && checkSize.size() == 1) {
					return parent;
				}
			}
			ele = parent;
		}
		return null;
	}

}
