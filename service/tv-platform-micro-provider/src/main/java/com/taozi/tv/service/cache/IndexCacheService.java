package com.taozi.tv.service.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.taozi.tv.bean.CardPojo;
import com.taozi.tv.bean.CardVideoEmentPojo;
import com.taozi.tv.bean.IndexCardRequest;
import com.taozi.tv.bean.PageResponse;
import com.taozi.tv.bean.SearchVideoRequest;
import com.taozi.tv.bean.VideoResourceProto;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.VideoTagDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.dao.bean.VideoTag;
import com.taozi.tv.pojo.task.IndexCacheInitEvent;
import com.taozi.tv.service.task.EventBusCenter;
import com.taozi.tv.utils.CacheKey;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/06/15
 */
@Slf4j
@Service
public class IndexCacheService {
	@Autowired
	@Qualifier("guavaCacheServiceImpl")
	private CacheService cacheService;
	@Autowired
	private VideoCacheService videoCacheService;

	@Autowired
	private VideoTagDao videoTagDao;
	@Autowired
	private EventBusCenter eventBusCenter;
	@Autowired
	private VideoResourceDao videoResourceDao;

	private Map<Long, List<Long>> tagOfVideoCache = new HashMap<>();
	private Long currentTime = System.currentTimeMillis();

	private void refreshTagOfVideoCache() {
		long end = System.currentTimeMillis();
		Long formatPeriod = NumberUtils.toLong(DurationFormatUtils.formatPeriod(currentTime, end, "m"));
		if (formatPeriod > 5) {
			eventBusCenter.post(IndexCacheInitEvent.builder().build());
			currentTime = end;
		}
	}

	@PostConstruct
	public void init() {
		log.info("EventBusListener is init");
		eventBusCenter.register(this);
		eventBusCenter.post(IndexCacheInitEvent.builder().build());
		currentTime = System.currentTimeMillis();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void initCache(IndexCacheInitEvent event) {
		log.info("IndexCacheService is init");
		List<VideoTag> tags = videoTagDao.query(VideoTag.builder().build());
		if (ObjectUtils.isEmpty(tags)) {
			log.info("tags is empty and return");
			return;
		}
		initTagsAndVideoCache(tags);
		log.info("IndexCacheService is finished");
	}

	private void initTagsAndVideoCache(List<VideoTag> tags) {
		for (VideoTag tag : tags) {
			List<VideoResource> videos = videoResourceDao
					.queryByPage(VideoResource.builder().tagId(tag.getId()).limit(0).offset(100).build());
			if (ObjectUtils.isEmpty(videos)) {
				continue;
			}
			cacheService.set(CacheKey.KEY_TV_TAGINFO_TAGID.replace(CacheKey.TAGID, tag.getId() + ""), tag, 5L);
			List<Long> tagOfVideo = new ArrayList<>(videos.size());
			for (VideoResource video : videos) {
				tagOfVideo.add(video.getId());
				videoCacheService.putVideoCache(video);
			}
			tagOfVideoCache.put(tag.getId(), tagOfVideo);
		}
	}

	public List<CardPojo> getCards(IndexCardRequest request) {
		refreshTagOfVideoCache();
		Set<Entry<Long, List<Long>>> entrySet = tagOfVideoCache.entrySet();
		List<CardPojo> result = new ArrayList<>(tagOfVideoCache.size());
		for (Entry<Long, List<Long>> el : entrySet) {
			Long tagId = el.getKey();
			List<Long> value = el.getValue();
			List<VideoResourceProto> leftRecommand = getVideoResourceProto(request.getLeftRecommandOffset(), value);
			List<VideoResourceProto> leftVideo = getVideoResourceProto(request.getLeftTotalOffset(), value);

			List<VideoResourceProto> rightRecommand = getVideoResourceProto(request.getRightRecommandOffset(), value);
			List<VideoResourceProto> rightVideo = getVideoResourceProto(request.getRightTotalOffset(), value);

			VideoTag videoTag = cacheService.get(CacheKey.KEY_TV_TAGINFO_TAGID.replace(CacheKey.TAGID, tagId + ""),
					VideoTag.class);
			CardPojo target = CardPojo.builder().build();
			BeanUtils.copyProperties(videoTag, target);
			target.setLeft(CardVideoEmentPojo.builder().videoRecommand(leftRecommand).videos(leftVideo).build());
			target.setRight(CardVideoEmentPojo.builder().videoRecommand(rightRecommand).videos(rightVideo).build());
			result.add(target);
		}
		return result;
	}

	public CardPojo getCard(IndexCardRequest request) {
		refreshTagOfVideoCache();
		List<Long> value = tagOfVideoCache.get(request.getTagId());
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		List<VideoResourceProto> leftRecommand = getVideoResourceProto(request.getLeftRecommandOffset(), value);
		List<VideoResourceProto> leftVideo = getVideoResourceProto(request.getLeftTotalOffset(), value);
		VideoTag videoTag = cacheService
				.get(CacheKey.KEY_TV_TAGINFO_TAGID.replace(CacheKey.TAGID, request.getTagId() + ""), VideoTag.class);
		CardPojo target = CardPojo.builder().build();
		BeanUtils.copyProperties(videoTag, target);
		target.setLeft(CardVideoEmentPojo.builder().videoRecommand(leftRecommand).videos(leftVideo).build());
		return target;
	}

	public List<VideoResourceProto> getVideosOfTag(IndexCardRequest request) {
		refreshTagOfVideoCache();
		List<Long> value = tagOfVideoCache.get(request.getTagId());
		if (ObjectUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		List<VideoResourceProto> leftVideo = getVideoResourceProto(request.getLeftTotalOffset(), value);
		return leftVideo;
	}

	public PageResponse<VideoResourceProto> getCardByPage(SearchVideoRequest request) {
		refreshTagOfVideoCache();
		List<Long> value = tagOfVideoCache.get(NumberUtils.toLong(request.getCardId()));
		PageResponse<VideoResourceProto> result = new PageResponse<>();
		if (ObjectUtils.isEmpty(value)) {
			return result;
		}
		int size = value.size();
		Integer limit = request.getLimit();
		limit = limit - 1;
		List<List<Long>> partition = Lists.partition(value, request.getOffset());
		if (limit >= partition.size()) {
			log.info("getCardByPage limit more then size,limit:{},size:{}", limit, partition.size());
			return result;
		}
		value = partition.get(limit);
		List<VideoResourceProto> leftVideo = getVideoResourceProtoNotRandom(request.getOffset(), value);
		result.setDatas(leftVideo);
		result.setTotal(size);
		result.setLimit(limit + 1);
		return result;
	}

	private List<VideoResourceProto> getVideoResourceProto(int offset, List<Long> value) {
		List<VideoResourceProto> temp = new ArrayList<>(offset);
		int length = value.size();
		List<Long> exist = new ArrayList<>(offset);
		Long videoId = 0L;
		for (int i = 0; i < offset; i++) {
			int nextInt = RandomUtils.nextInt(0, length);
			videoId = value.get(nextInt);
			if (exist.contains(videoId)) {
				i = i - 1;
				continue;
			}
			exist.add(videoId);
			VideoResourceProto video = videoCacheService.getIfPresentVideoProtoCache(videoId);
			if (!ObjectUtils.isEmpty(video)) {
				temp.add(video);
			}
		}
		return temp;
	}

	private List<VideoResourceProto> getVideoResourceProtoNotRandom(int offset, List<Long> value) {
		List<VideoResourceProto> temp = new ArrayList<>(offset);
		Long videoId = 0L;
		for (int i = 0; i < offset; i++) {
			videoId = value.get(i);
			VideoResourceProto video = videoCacheService.getIfPresentVideoProtoCache(videoId);
			if (!ObjectUtils.isEmpty(video)) {
				temp.add(video);
			}
		}
		return temp;
	}
}
