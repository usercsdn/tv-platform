package com.taozi.tv.dao.bean;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@Table("t_video_resource")
@Document(indexName = "video_resource_v1")
public class VideoResource {
	@TableColumn("id")
	@Id
	private Long id;

	@TableColumn("parse_conf_id")
	@Transient
	private Long parseConfId;

	@TableColumn("name")
	@Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
	private String name;

	@Transient
	@TableColumn("type")
	private String type;

	@Transient
	@TableColumn("tag_id")
	private Long tagId;

	@Transient
	@TableColumn("tag_name")
	private String tagName;

	@Transient
	@TableColumn("page_url")
	private String pageUrl;

	@Transient
	@TableColumn("video_url")
	private String videoUrl;

	@TableColumn("video_image")
	private String videoImage;

	@TableColumn("like_count")
	private Long likeCount;

	@TableColumn("duration")
	private Integer duration;

	@TableColumn("duration_format")
	private String durationFormat;

	@TableColumn("active_status")
	private Integer activeStatus;

	@Transient
	@TableColumn("video_is_expired")
	private Integer videoIsExpired;

	@Transient
	@TableColumn("video_expired_time")
	private Date videoExpiredTime;

	@Transient
	@TableColumn("name_sha1")
	private String nameSha1;

	@Transient
	@TableColumn("high_quality_level")
	private Integer highQualityLevel;

	@TableColumn("create_time")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
	private Date createTime;

	@TableColumn("modify_time")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
	private Date modifyTime;

	@Transient
	private Integer limit;
	@Transient
	private Integer offset;
	@Transient
	@Builder.Default
	private String orderType = "create_time";
	@Transient
	@Builder.Default
	private String descType = "DESC";
}
