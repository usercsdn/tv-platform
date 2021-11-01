package com.web.test.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author xulijie
 * @date 2021/04/15
 */
public class FFmpegTest {
	private String TMP_PATH = "D:";
	private String FFMPEG_PATH = "D:\\soft\\ffmpeg-4.4-full_build\\bin\\ffmpeg.exe";

	public static void main(String[] args) throws Exception {
		FFmpegTest api = new FFmpegTest();
		// api.get();
		api.convetor("D:\\ffmpeg\\test.mp4", "D:\\ffmpeg\\output.mp3", "D:\\ffmpeg\\convetor.mp4");
		// Thread.sleep(100000L);
	}

	private void get() {
		String videoToAudio = videoToAudio("D://test.mp4");
		System.out.println(videoToAudio);
	}

	public String videoToAudio(String videoUrl) {
		String aacFile = "";
		try {
			aacFile = TMP_PATH + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
					+ UUID.randomUUID().toString().replaceAll("-", "") + ".mp3";
			String command = FFMPEG_PATH + " -i " + videoUrl + " -vn  " + aacFile;
			System.out.println("video to audio command : " + command);
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void convetor(String videoInputPath, String audioInputPath, String videoOutPath) throws Exception {
		Process process = null;
		try {
			String command = FFMPEG_PATH + " -i " + videoInputPath + " -i " + audioInputPath
					+ " -c:v copy -c:a aac -strict experimental " + " -map 0:v:0 -map 1:a:0 " + " -y " + videoOutPath;
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);

		String line = "";
		while ((line = br.readLine()) != null) {
		}
		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}

	}

}
