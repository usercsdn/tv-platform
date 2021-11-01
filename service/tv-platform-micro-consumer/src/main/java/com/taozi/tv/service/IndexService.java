package com.taozi.tv.service;

import java.util.List;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.CardPojo;
import com.taozi.tv.bean.IndexCardRequest;

/**
 * @author xulijie
 * @date 2021/06/15
 */
public interface IndexService {
	CallResult<List<CardPojo>> getIndex(IndexCardRequest request);

	CallResult<CardPojo> getCard(IndexCardRequest request);
}
