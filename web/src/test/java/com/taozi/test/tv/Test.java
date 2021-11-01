package com.taozi.test.tv;

import lombok.Data;

/**
 * @author xulijie
 * @date 2021/06/07
 */
public class Test {
	public static void main(String[] args) {
	}

	@Data
	class Blog {
		int id;
		String title;
		String description;
		String created;
	}
}
