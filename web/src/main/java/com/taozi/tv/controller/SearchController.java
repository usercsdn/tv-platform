package com.taozi.tv.controller;

import javax.validation.constraints.NotNull;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.PageResponse;
import com.taozi.tv.bean.SearchVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/search")
public class SearchController {
	@Reference(timeout = 10000)
	private SearchService searchService;

	@GetMapping("/keyword")
	public CallResult<PageResponse<VideoResourceProto>> search(@NotNull String key, @NotNull String cardId,
			@NotNull Integer limit, @NotNull Integer offset) {
		log.info("search is begin id:{},cardId:{},limit:{},offset:{}", key, cardId, limit, offset);
		if (ObjectUtils.isEmpty(limit)) {
			limit = 1;
		}
		if (limit > 30) {
			return new CallResult<>(null);
		}
		if (ObjectUtils.isEmpty(offset)) {
			offset = 20;
		}
		if (offset > 20) {
			return new CallResult<>(null);
		}
		PageResponse<VideoResourceProto> result = searchService
				.searchVideo(SearchVideoRequest.builder().name(key).cardId(cardId).limit(limit).offset(offset).build());
		log.info("search is finished id:{},cardId:{}", key, cardId);
		return new CallResult<>(result);
	}

}
