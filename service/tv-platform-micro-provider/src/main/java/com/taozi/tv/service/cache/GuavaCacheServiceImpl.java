package com.taozi.tv.service.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class GuavaCacheServiceImpl implements CacheService {

	Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(100000) // 设置缓存的最大容量
			.expireAfterWrite(1, TimeUnit.DAYS) // 设置缓存在写入一分钟后失效
			.concurrencyLevel(10) // 设置并发级别为10
			.build();
	private Gson gson = new Gson();

	@Override
	public <T> T get(String key, Class<T> classOfT) {
		String value = cache.getIfPresent(key);
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		return gson.fromJson(value, classOfT);
	}

	@Override
	public <T> List<T> getArray(String key, Class<T> classOfT) {
		String value = cache.getIfPresent(key);
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		return gson.fromJson(value, new TypeToken<List<T>>() {
		}.getType());
	}

	@Override
	public int set(String key, String value, long expireTime) {
		cache.put(key, value);
		return 1;
	}

	@Override
	public int set(String key, Object value, long expireTime) {
		set(key, gson.toJson(value), expireTime);
		return 1;
	}

	@Override
	public String get(String key) {
		return cache.getIfPresent(key);
	}

	@Override
	public Boolean exist(String key) {
		return !ObjectUtils.isEmpty(get(key));
	}

	@Override
	public Boolean del(String key) {
		cache.invalidate(key);
		return true;
	}

}
