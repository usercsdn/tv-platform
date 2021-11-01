package com.taozi.tv.pojo.ffmpeg;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/04/20
 */
@ToString(exclude = { "tsUrls" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class M3u8DownloadBuilder {
	private String m3u8Url;
	private String text;

	private List<String> tsUrls;
}
