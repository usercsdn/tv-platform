package com.taozi.tv.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taozi.tv.service.SeleniumParseStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OpsHalfAutoController {
	@Autowired
	private SeleniumParseStart seleniumParseStart;

	@GetMapping("/hi")
	public String index() {
		log.info("index is begin");
		seleniumParseStart.startWebSite();
		return "abc";
	}

	@GetMapping("/do")
	public String download(Integer size, Long id, Long parseConfId) {
		log.info("download is begin size:{},id:{},parseConfId:{}", size, id, parseConfId);
		seleniumParseStart.startDownload(ObjectUtils.isEmpty(size) ? 20 : size, id, parseConfId);
		return "abc";
	}

	@GetMapping("/update")
	public String update() {
		log.info("download is begin");
		seleniumParseStart.startUpdateNode();
		return "abc";
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLEncoder.encode("阿凯", "UTF-8"));
	}

	@GetMapping("/add")
	public String specialUrl(String url) {
		log.info("specialUrl is begin");
		seleniumParseStart.addSpecialVideoResource(url);
		return "abc";
	}

	@GetMapping("/play")
	public String play(String urls) {
		log.info("play is begin");
		seleniumParseStart.startPlay(urls);
		return "abc";
	}

}
