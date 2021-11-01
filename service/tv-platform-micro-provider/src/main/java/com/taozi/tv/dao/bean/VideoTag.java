package com.taozi.tv.dao.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * VideoTag
 * 
 * @author auto create
 *
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_video_tag")
public class VideoTag {

	/**
	 * id
	 */
	@TableColumn("id")
	private Long id;
	/**
	 * tagName
	 */
	@TableColumn("name")
	private String name;
	@TableColumn("icon")
	private String icon;

	/**
	 * createTime
	 */
	@TableColumn("create_time")
	private java.util.Date createTime;
	/**
	 * modifyTime
	 */
	@TableColumn("modify_time")
	private java.util.Date modifyTime;

}