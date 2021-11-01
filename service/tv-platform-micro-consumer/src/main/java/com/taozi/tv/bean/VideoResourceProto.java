package com.taozi.tv.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/04/23
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResourceProto implements Serializable {
	private static final long serialVersionUID = -8830496409219523634L;
	private Long id;
	private Long parseConfId;
	private String name;
	private String type;
	private String pageUrl;
	private String videoImage;
	private String videoAuthor;
	private String videoUrl;
	private Long likeCount;
	private Integer duration;
	private String durationFormat;
	private Integer activeStatus;
	private Integer videoIsExpired;
	private String nameSha1;
}
