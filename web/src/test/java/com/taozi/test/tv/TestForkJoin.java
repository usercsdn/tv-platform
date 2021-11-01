package com.taozi.test.tv;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author xulijie
 * @date 2021/05/22
 */
public class TestForkJoin {

	public static void main(String[] args) {
		TestForkJoin api = new TestForkJoin();
		for (int i = 0; i < 100; i++) {
			api.test01();
		}

	}

	public long max = 1000000000L;
	ForkJoinPool pool = new ForkJoinPool();

	public void test01() {
		Instant start = Instant.now();

		ForkJoinTask<Long> task = new ForkJoinCaculate(0, max);
		Long sum = pool.invoke(task);
		System.out.println(sum);
		Instant end = Instant.now();
		System.out.println("耗时：" + Duration.between(start, end).toMillis());
	}

	public void test02() {
		Instant start = Instant.now();
		long sum = 0L;
		for (long i = 0; i <= max; i++) {
			sum += i;
		}

		System.out.println(sum);
		Instant end = Instant.now();
		System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());
	}

	public void test03() {
		System.out.println("java8 并行流");

		Instant start = Instant.now();

		long sum = LongStream.rangeClosed(0, max).parallel().reduce(Long::sum).getAsLong();

		System.out.println(sum);

		Instant end = Instant.now();

		System.out.println("执行耗时：" + Duration.between(start, end).toMillis());
	}

}

class ForkJoinCaculate extends RecursiveTask<Long> {

	private long start;
	private long end;

	public ForkJoinCaculate(long start, long end) {
		this.start = start;
		this.end = end;
	}

	private static final long THRESHOLD = 10000L;

	@Override
	protected Long compute() {
		long length = end - start;

		if (length < THRESHOLD) {
			long sum = 0;
			for (long i = start; i <= end; i++) {
				sum += i;
			}
			return sum;
		} else {
			long middle = (end + start) / 2; // 中间位置
			ForkJoinCaculate left = new ForkJoinCaculate(start, middle);
			left.fork(); // 拆分子任务，同时压入线程队列
			ForkJoinCaculate right = new ForkJoinCaculate(middle + 1, end);
			right.fork();
			return left.join() + right.join();

		}
	}
}
