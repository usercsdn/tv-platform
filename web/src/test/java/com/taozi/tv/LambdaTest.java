package com.taozi.tv;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class LambdaTest {
	@Test
	public void anonymous() {
		new Thread(new Runnable() {// 接口名
			@Override
			public void run() {// 方法名
				System.out.println("Thread run()");
			}
		}).start();
		new Thread(() -> {
			System.out.println("lambda Thread run1()");
			System.out.println("lambda Thread run2()");
		}).start();
	}

	@Test
	public void anonymousWithParams() {
		List<String> list = Arrays.asList("I", "love", "you", "too");
		Collections.sort(list, new Comparator<String>() {// 接口名
			@Override
			public int compare(String s1, String s2) {// 方法名
				if (s1 == null)
					return -1;
				if (s2 == null)
					return 1;
				return s1.length() - s2.length();
			}
		});
		System.out.println(list);
		Collections.sort(list, (s1, s2) -> {
			if (s1 == null)
				return -1;
			if (s2 == null)
				return 1;
			return s2.length() - s1.length();
		});
		System.out.println(list);
	}

	@Test
	public void forEach() {
		Stream<String> stream = Stream.of("1", "2");
		stream.forEach(str -> System.out.println(str));
	}
}
