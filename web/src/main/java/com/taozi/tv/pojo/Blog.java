package com.taozi.tv.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/06/07
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Blog {
	int id;
	String title;
	String description;
	String created;
}
