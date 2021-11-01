package com.taozi.tv.rpc;

import java.util.List;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.CardPojo;
import com.taozi.tv.bean.IndexCardRequest;
import com.taozi.tv.service.IndexService;
import com.taozi.tv.service.cache.IndexCacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Service
public class IndexServiceImpl implements IndexService {

	@Autowired
	private IndexCacheService indexCacheService;

	@Override
	public CallResult<List<CardPojo>> getIndex(IndexCardRequest request) {
		log.info("getIndex is begin request:{}", request);
		List<CardPojo> datas = indexCacheService.getCards(request);
		log.info("getIndex is finished size:{}", datas.size());
		return CallResult.success(datas);
	}

	@Override
	public CallResult<CardPojo> getCard(IndexCardRequest request) {
		log.info("getCard is begin request:{}", request);
		CardPojo datas = indexCacheService.getCard(request);
		log.info("getCard is finished datas:{}", datas);
		return CallResult.success(datas);
	}
}
