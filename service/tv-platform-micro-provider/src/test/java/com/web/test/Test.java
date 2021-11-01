package com.web.test;

import java.io.File;

import com.google.common.base.Splitter;
import com.taozi.tv.utils.CommonUtils;
import com.taozi.tv.utils.FileUtils;

public class Test {
	public static void main(String[] args) {
		String mac = CommonUtils.getMac();

		String path = "E:\\u01\\";
		if (mac.equals("F8-A2-D6-DA-00-C5")) {
			path = "D:\\u01\\";
		}
		String name = "5607_602";
		path = path + File.separator + name + File.separator;
		Integer size = Integer.valueOf(Splitter.on("_").trimResults().omitEmptyStrings().splitToList(name).get(1)) - 10;
		StringBuilder result = new StringBuilder();
		result.append("@Echo Off;").append("\n");
		for (int i = 0; i < size; i++) {
			int start = 6;
			int end = 15;
			int breakTag = start + end * i + end;
			if (breakTag > size) {
				break;
			}
			StringBuilder sb = new StringBuilder(" call ffmpeg -y -i ").append(path).append(name).append(".mp4")
					.append(" -ss ");
			sb.append(start + end * i);
			sb.append(" -t ");
			sb.append(end);
			sb.append(" -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y ");
			sb.append(path).append("keyoutput").append(i).append(".mp4");
			result.append(sb.toString()).append("\n");
		}
		result.append("Pause").append("\n");
		System.out.println(result.toString());
		FileUtils.putValueTocacheFile(path + name + ".bat", result.toString(), false);
	}
}
