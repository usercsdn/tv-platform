package com.taozi.tv.dao.bean;

import java.util.Date;

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
@Table("t_website_parse_conf")
public class WebsiteParseConf {
	@TableColumn("id")
	private Long id;
	@TableColumn("name")
	private String name;
	@TableColumn("url")
	private String url;
	@TableColumn("tag_id")
	private Long tagId;
	@TableColumn("tag_name")
	private String tagName;
	@TableColumn("high_quality_level")
	private Integer highQualityLevel;
	@TableColumn("active_status")
	private Integer activeStatus;
	@TableColumn("create_time")
	private Date createTime;
	@TableColumn("modify_time")
	private Date modifyTime;

	private WebsiteParseConfMetabase metabase;
}
