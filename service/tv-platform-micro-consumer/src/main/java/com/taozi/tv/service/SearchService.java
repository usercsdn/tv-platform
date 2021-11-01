package com.taozi.tv.service;

import com.taozi.tv.bean.PageResponse;
import com.taozi.tv.bean.SearchVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;

public interface SearchService {
	public PageResponse<VideoResourceProto> searchVideo(SearchVideoRequest request);
}
