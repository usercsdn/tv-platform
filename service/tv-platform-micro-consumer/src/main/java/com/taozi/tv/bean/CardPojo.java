package com.taozi.tv.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/06/09
 */
@ToString(exclude = { "right", "left" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardPojo implements Serializable {
	private static final long serialVersionUID = -9215333701809127550L;
	private Long id;
	private String name;
	private String icon;
	private CardVideoEmentPojo right;
	private CardVideoEmentPojo left;
}
