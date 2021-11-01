package com.taozi.tv.service.cache;

import java.util.List;

public interface CacheService {
	public Boolean exist(String key);

	public Boolean del(String key);

	public String get(String key);

	public <T> T get(String key, Class<T> classOfT);

	public <T> List<T> getArray(String key, Class<T> classOfT);

	int set(String key, String value, long expireTime);

	int set(String key, Object value, long expireTime);
}
