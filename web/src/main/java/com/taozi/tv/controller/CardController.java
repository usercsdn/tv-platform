package com.taozi.tv.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.CardPojo;
import com.taozi.tv.bean.IndexCardRequest;
import com.taozi.tv.service.IndexService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/card")
public class CardController {
	@Reference(timeout = 10000)
	private IndexService indexService;

	@GetMapping("/getCard")
	public CallResult<CardPojo> getCard(@NotNull String cardId) {
		log.info("getCard is begin id:{}", cardId);
		CallResult<CardPojo> card = indexService.getCard(IndexCardRequest.builder().leftRecommandOffset(1)
				.leftTotalOffset(13).tagId(NumberUtils.toLong(cardId)).build());
		log.info("getCard is finished id:{}", cardId);
		return card;
	}

	@GetMapping("/getCards")
	public CallResult<List<CardPojo>> getCards() {
		log.info("getCards is begin ");
		CallResult<List<CardPojo>> datas = indexService.getIndex(IndexCardRequest.builder().leftRecommandOffset(1)
				.leftTotalOffset(13).rightRecommandOffset(3).rightTotalOffset(7).build());
		log.info("getCards is finished ");
		return datas;
	}
}
