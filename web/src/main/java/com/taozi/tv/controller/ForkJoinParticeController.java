package com.taozi.tv.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.taozi.tv.CallResult;
import com.taozi.tv.bean.VideoResourceProto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/05/22
 */
@Slf4j
@RestController
@RequestMapping(value = "/video")
public class ForkJoinParticeController {
	public static void main(String[] args) {
		ForkJoinParticeController api = new ForkJoinParticeController();
		for (int i = 0; i < 100; i++) {
			api.test();
		}
	}

	@GetMapping("/getVideo2")
	public CallResult<VideoResourceProto> getVideo(@NotNull Long id) {
		Instant start = Instant.now();
		log.info("getVideo is begin id:{}", id);
		ForkJoinTask<Long> task = new Task1();
		ForkJoinTask<String> task2 = new Task2();
		pool.invoke(task);
		task2.fork();
		task2.join();
		List<ForkJoinTask> tasks = Lists.newArrayList(task, task2);
		ForkJoinTask.invokeAll(tasks);
		Instant end = Instant.now();
		System.out.println(Thread.currentThread().getId() + ">耗时：" + Duration.between(start, end).toMillis());
		log.info("getVideo is finished id:{}", id);
		return null;
	}

	ForkJoinPool pool = new ForkJoinPool();

	public void test() {
		Instant start = Instant.now();

		ForkJoinTask<Long> task = new Task1();
		// task.fork();
		// Long sum = pool.invoke(task);
		ForkJoinTask<String> task2 = new Task2();
		// task2.fork();

		// pool.invokeAll(tasks);
		// String sum2 = pool.invoke(task2);
		// String sum2 = task2.join();
		// Long sum = task.join();
		// System.out.println(sum);
		// System.out.println(sum2);
		// pool.invoke(task);
		// pool.invoke(task2);

		List<ForkJoinTask> tasks = new ArrayList<>();
		tasks.add(task2);
		tasks.add(task);
		Collection<ForkJoinTask> invokeAll = ForkJoinTask.invokeAll(tasks);
		// for (ForkJoinTask el : invokeAll) {
		// Object join = el.join();
		// System.out.println(join);
		// }
		Instant end = Instant.now();
		System.out.println("耗时：" + Duration.between(start, end).toMillis());
	}

}

class Task1 extends RecursiveTask<Long> {
	@Override
	protected Long compute() {
		try {
			Thread.sleep(1300L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// System.out.println("1L");
		// ForkJoinTask<String> task2 = new Task2();
		// task2.fork();
		// task2.join();
		return 1L;
	}
}

class Task2 extends RecursiveTask<String> {
	@Override
	protected String compute() {
		try {
			Thread.sleep(1100L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// System.out.println("Hello");
		return "Hello";
	}
}
