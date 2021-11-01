package com.taozi.tv.service;

import java.util.List;

import com.taozi.tv.CallResult;
import com.taozi.tv.bean.GetVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;

public interface VideoService {
	CallResult<List<VideoResourceProto>> getRecommandVideo(GetVideoRequest request);

	CallResult<VideoResourceProto> getVideo(GetVideoRequest request);
}
