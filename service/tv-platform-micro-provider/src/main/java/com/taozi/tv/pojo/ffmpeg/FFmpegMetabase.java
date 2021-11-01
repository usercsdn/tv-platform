package com.taozi.tv.pojo.ffmpeg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FFmpegMetabase {
	private String duration;
	private String bitrate;
}
