package com.taozi.test.tv;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import com.google.common.collect.Lists;

/**
 * @author xulijie
 * @date 2021/05/22
 */
public class ForkJoinParticeTest {
	public static void main(String[] args) throws Exception {
		ForkJoinParticeTest api = new ForkJoinParticeTest();
		for (int i = 0; i < 30; i++) {
			api.test();
		}

	}

	ForkJoinPool pool = new ForkJoinPool();

	public void test() {
		Instant start = Instant.now();

		List<ForkJoinTask<String>> elements = new ArrayList<>();
		ForkJoinTask<String> task = new Task1(100L);
		ForkJoinTask<String> task2 = new Task1(100L);
		ForkJoinTask.invokeAll(Lists.newArrayList(task));
		// task.fork();
		// task2.fork();
		// task.join();
		// task2.join();
		// pool.invoke(task);
		// pool.invoke(task2);
		Instant end = Instant.now();
		System.out.println("耗时：" + Duration.between(start, end).toMillis());
	}

}

class Task1 extends RecursiveTask<String> {
	private Long sleep;

	public Task1(Long sleep) {
		this.sleep = sleep;
	}

	@Override
	protected String compute() {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ForkJoinTask<String> task = new Task2(100L);
		// task.fork();
		// task.join();
		// ForkJoinTask.invokeAll(Lists.newArrayList(task));
		return "K";
	}
}

class Task2 extends RecursiveTask<String> {
	private Long sleep;

	public Task2(Long sleep) {
		this.sleep = sleep;
	}

	@Override
	protected String compute() {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// System.out.println("Hello");
		return "Hello";
	}
}
