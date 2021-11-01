package com.taozi.tv.service.task;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.eventbus.AsyncEventBus;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/20
 */
@Slf4j
@Service
public class EventBusCenter {
	private AsyncEventBus bus;

	private int coreThreads = 10;
	private int maxThreads = 25;

	@PostConstruct
	public void init() {
		log.info("EventBusCenter init");
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreThreads, maxThreads, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(500000), Executors.defaultThreadFactory());
		bus = new AsyncEventBus(threadPoolExecutor);
	}

	/**
	 * 触发事件
	 * 
	 * @param event
	 */
	public void post(Object event) {
		if (ObjectUtils.isEmpty(event)) {
			log.info("post event is empty");
			return;
		}
		bus.post(event);
	}

	/**
	 * 注册事件监听器
	 * 
	 * @param listener
	 */
	public void register(Object listener) {
		bus.register(listener);
	}
}
