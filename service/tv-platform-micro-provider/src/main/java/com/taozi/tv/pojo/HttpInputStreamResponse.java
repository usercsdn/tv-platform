package com.taozi.tv.pojo;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import okhttp3.Response;

/**
 * @author xulijie
 * @date 2021/03/15
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpInputStreamResponse {
	private InputStream input;
	private Response response;
}
