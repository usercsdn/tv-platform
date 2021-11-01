package com.taozi.tv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xulijie
 * @date 2021/04/12
 */
@Slf4j
public class FileUtils {
	private FileUtils() {

	}

	private static Gson gson = new Gson();

	public static String readFile(String filePath) {
		File file = new File(filePath);
		Long filelength = file.length(); // 获取文件长度
		byte[] filecontent = new byte[filelength.intValue()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (Exception e) {
			log.error("readFile is error", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		String fileContentArr = new String(filecontent);
		return fileContentArr;// 返回文件内容,默认编码
	}

	public static <T> List<T> readCacheFile(String outFilePath, Class<T> cla) {
		FileInputStream is = null;
		BufferedReader reader = null;
		try {
			if (ObjectUtils.isEmpty(outFilePath)) {
				return Collections.emptyList();
			}
			File file = new File(outFilePath);
			if (!file.exists()) {
				return Collections.emptyList();
			}
			List<T> result = new ArrayList<>();
			is = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(is));
			String readLine = reader.readLine();

			while (!ObjectUtils.isEmpty(readLine)) {
				if (cla == String.class) {
					result.add((T) readLine);
				} else {
					T fromJson = gson.fromJson(readLine, cla);
					result.add(fromJson);
				}
				readLine = reader.readLine();
			}
			return result;
		} catch (Exception e) {
			log.error("readCacheFile is error", e);
			return Collections.emptyList();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(reader);
		}
	}

	public static <T> boolean putValueTocacheFile(String outFilePath, List<T> query, boolean append) {
		FileWriter buffer = null;
		try {
			if (ObjectUtils.isEmpty(outFilePath)) {
				return false;
			}
			if (ObjectUtils.isEmpty(query)) {
				return false;
			}
			String flag = File.separator;
			String root = outFilePath.substring(0, outFilePath.lastIndexOf(flag));
			File fileRoot = new File(root);
			if (!fileRoot.exists()) {
				fileRoot.mkdirs();
			}
			File file = new File(outFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			buffer = new FileWriter(file, append);
			if (!ObjectUtils.isEmpty(query)) {
				for (T q : query) {
					String json = "";
					if (q instanceof String) {
						json = (String) q;
					} else {
						json = gson.toJson(q);
					}
					buffer.write(json + "\n");
				}
			}
			buffer.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			IOUtils.closeQuietly(buffer);
		}
	}

	public static boolean putValueTocacheFile(String outFilePath, String data, boolean append) {
		return putValueTocacheFile(outFilePath, Lists.newArrayList(data), append);
	}
}
