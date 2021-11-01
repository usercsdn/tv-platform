package com.taozi.tv.rpc;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.bean.PageResponse;
import com.taozi.tv.bean.SearchVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.elastic.VideoResourceESDao;
import com.taozi.tv.service.SearchService;
import com.taozi.tv.service.cache.IndexCacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private VideoResourceESDao videoResourceESDao;

	@Autowired
	private IndexCacheService indexCacheService;

	@Override
	public PageResponse<VideoResourceProto> searchVideo(SearchVideoRequest request) {
		log.info("search is begin request:{}", request);
		PageResponse<VideoResourceProto> videoByES = getVideoByES(request);
		if (!ObjectUtils.isEmpty(videoByES)) {
			return videoByES;
		}
		videoByES = getVideoByDB(request);
		log.info("search is finished request:{}", request);
		return videoByES;
	}

	private PageResponse<VideoResourceProto> getVideoByES(SearchVideoRequest request) {
		String key = request.getName();
		if (ObjectUtils.isEmpty(key)) {
			return null;
		}
		Integer limit = request.getLimit();
		limit = limit - 1;
		Integer offset = request.getOffset();
		PageResponse<VideoResourceProto> result = new PageResponse<>();
		Page<SearchHit<VideoResource>> datas = videoResourceESDao.findByNameLike(key, PageRequest.of(limit, offset));
		if (ObjectUtils.isEmpty(datas)) {
			return result;
		}
		Long totalElements = datas.getTotalElements();
		List<VideoResourceProto> elements = new ArrayList<>();
		datas.getContent().forEach(el -> {
			List<String> highlightField = el.getHighlightField("name");
			if (!ObjectUtils.isEmpty(highlightField)) {
				el.getContent().setName(String.join("", highlightField));
			}
			VideoResourceProto target = VideoResourceProto.builder().build();
			BeanUtils.copyProperties(el.getContent(), target);

			if (target.getVideoImage().contains(".gif")) {
				target.setVideoImage(target.getVideoImage().replaceAll("imageslim",
						"imageMogr2/auto-orient/format/webp/quality/45!/ignore-error/1"));
			} else {
				target.setVideoImage(target.getVideoImage().replaceAll("imageslim", "imageView2/1/w/320/h/180"));
			}
			elements.add(target);
		});
		result.setTotal(totalElements.intValue());
		result.setDatas(elements);
		result.setLimit(limit + 1);
		log.info("search is finished key:{},size:{},totalElements:{}", key, elements.size(), totalElements);
		return result;
	}

	private PageResponse<VideoResourceProto> getVideoByDB(SearchVideoRequest request) {
		String key = request.getCardId();
		if (ObjectUtils.isEmpty(key)) {
			return null;
		}
		return indexCacheService.getCardByPage(request);
	}

}
