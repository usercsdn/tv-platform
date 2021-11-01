package com.taozi.tv.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.taozi.tv.pojo.HttpInputStreamResponse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author xulijie
 * @date 2020/12/11
 */
@Slf4j
public class HttpUtils {

	static Gson gson = new Gson();

	private static OkHttpClient okHttpClient = new OkHttpClient();

	public static void main(String[] args) throws IOException {
		HttpUtils api = new HttpUtils();
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis();
			String url = "https://weibo.com/tv/home";
			// url = "http://www.baidu.com";
			String html = api.get(url, buildCommonHeaders());
			long end = System.currentTimeMillis();
			System.out.println((end - start) + ":" + html);
		}
	}

	static {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
			@Override
			public void log(String message) {
				try {
					log.info("OKHttp-----{}", message);
				} catch (Exception e) {
					log.info("OKHttp-----{}", message, e);
				}
			}
		});
		// 这里可以builder(). 添加更多的内容 具体看需求
		// okHttpClient = new
		// OkHttpClient.Builder().addInterceptor(interceptor).build();
		// // // 这行必须加 不然默认不打印
		// interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
	}

	public static Map<String, String> buildCommonHeaders() {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("User-Agent",
				"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Mobile Safari/537.36");
		return headers;
	}

	public static String get(String url, Map<String, String> headerMap) {
		Headers headers = getHeaders(headerMap).build();
		Request request = new Request.Builder().headers(headers).url(url).get().build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			String value = response.body().string();
			response.close();
			return value;
		} catch (Exception e) {
			log.info("post is error url:{}", url, e);
		}
		return "";
	}

	public static HttpInputStreamResponse getInputStream(String url, Map<String, String> headerMap) {
		Headers headers = getHeaders(headerMap).build();
		Request request = new Request.Builder().headers(headers).url(url).get().build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			return HttpInputStreamResponse.builder().input(response.body().byteStream()).response(response).build();
		} catch (Exception e) {
			log.info("post is error url:{}", url, e);
		}
		return null;
	}

	private static Headers.Builder getHeaders(Map<String, String> paramsMap) {
		Map<String, String> defaultHeaders = getDefaultHeaders();
		if (!ObjectUtils.isEmpty(paramsMap)) {
			defaultHeaders.putAll(paramsMap);
		}
		Headers.Builder formBodyBuilder = new Headers.Builder();
		if (!ObjectUtils.isEmpty(defaultHeaders)) {
			Set<String> keySet = defaultHeaders.keySet();
			for (String key : keySet) {
				String value = defaultHeaders.get(key);
				formBodyBuilder.add(key, value);
			}
		}
		return formBodyBuilder;
	}

	private static Map<String, String> getDefaultHeaders() {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
		return paramsMap;
	}

	public static String post(String url, String body, Map<String, String> headerMap) {
		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", body)
				.build();
		Headers headers = getHeaders(headerMap).build();
		Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			String responseBody = response.body().string();
			response.close();
			return responseBody;
		} catch (Exception e) {
			log.info("post is error url:{},body:{}", url, body, e);
		}
		return "";
	}

	private static FormBody.Builder getFormBod(Map<String, String> paramsMap) {
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		if (!ObjectUtils.isEmpty(paramsMap)) {
			Set<String> keySet = paramsMap.keySet();
			for (String key : keySet) {
				String value = paramsMap.get(key);
				formBodyBuilder.add(key, value);
			}
		}
		return formBodyBuilder;
	}
}
