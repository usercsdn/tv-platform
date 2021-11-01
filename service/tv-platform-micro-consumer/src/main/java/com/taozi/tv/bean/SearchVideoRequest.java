package com.taozi.tv.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/06/30
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchVideoRequest implements Serializable {
	private static final long serialVersionUID = 8115237926067494503L;
	private String name;
	private String cardId;
	private Integer limit;
	private Integer offset;
}
