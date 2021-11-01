package com.taozi.tv.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author xulijie
 * @date 2021/06/15
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexCardRequest implements Serializable {
	private static final long serialVersionUID = -9215333701809127550L;
	private String ip;
	private Long tagId;
	private int leftRecommandOffset;
	private int leftTotalOffset;
	private int rightRecommandOffset;
	private int rightTotalOffset;
}
