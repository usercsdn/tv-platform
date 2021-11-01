package com.taozi.tv.dao.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * WebsiteParseConfMetabase
 * 
 * @author auto create
 *
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_website_parse_conf_metabase")
public class WebsiteParseConfMetabase {
	@TableColumn("id")
	private Long id;
	@TableColumn("parse_conf_id")
	private Long parseConfId;
	@TableColumn("active_status")
	private Integer activeStatus;
	@TableColumn("sort_level")
	private Integer sortLevel;
	@TableColumn("meta_load_css")
	private String metaLoadCss;
	@TableColumn("meta_screen_css")
	private String metaScreenCss;
	@TableColumn("meta_next_text")
	private String metaNextText;
	@TableColumn("meta_next_size")
	private Integer metaNextSize;
	@TableColumn("meta_video_name_filter")
	private String metaVideoNameFilter;
	@TableColumn("create_time")
	private java.util.Date createTime;
	@TableColumn("modify_time")
	private java.util.Date modifyTime;
}