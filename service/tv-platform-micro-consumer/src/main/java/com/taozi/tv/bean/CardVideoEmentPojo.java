package com.taozi.tv.bean;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/06/09
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardVideoEmentPojo implements Serializable {
	private static final long serialVersionUID = 4772181523231013004L;
	private List<VideoResourceProto> videoRecommand;
	private List<VideoResourceProto> videos;
}
