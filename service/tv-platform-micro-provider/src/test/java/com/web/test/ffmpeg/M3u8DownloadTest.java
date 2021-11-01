package com.web.test.ffmpeg;

/**
 * @author xulijie
 * @date 2021/04/19
 */
public class M3u8DownloadTest {
	private static final String M3U8URL = "https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/hls/x3UMPK6JW4OgiDkdKOcroz67PIzcahzhJb68qOUjLR4Q120J8lNWS6SRoXB2fvMT.m3u8?pkey=ABBh6F1yTXiFbzj-Uly6Fu29-nUegZw2GahNASOAsk7agX-6tDjCyocGZjluMi7S0yAsbBjaMN2EhAqF1ObxXkwdMBmpjgfw1RIYhKcB7ZQIx-P15Oz_LEpB8I8PnqMJmWgYtlrmoyddZVQhqpiY0fp4I4gQbI4YDjBEzsK0RyQohDQdMplV_0qWPuZurYWceqlQll5MDzsrGZq5u2tGH58NC2NlYRWVG98RA06mHn4ezJv3-k1WGdoc_wD4FSG2DMg&safety_id=AAK-EP12J_1xzKI_AB7caO1M";

	public static void main(String[] args) throws Exception {
		M3u8DownloadTest api = new M3u8DownloadTest();
		api.download(M3U8URL, "test1");
	}

	private void download(String downloadUrl, String output) throws Exception {
		// M3u8DownloadAssist m3u8Download = new
		// M3u8DownloadAssist(downloadUrl);
		// // M3u8DownloadFactory factory = new M3u8DownloadFactory();
		// // factory.m3u8Download m3u8Download = new M3u8Download(downloadUrl);
		// // 设置生成目录
		// m3u8Download.setDir("D:\\ffmpeg\\m3u8JavaTest1");
		// // 设置视频名称
		// m3u8Download.setFileName(output);
		// // 设置线程数
		// m3u8Download.setThreadCount(30);
		// // 设置重试次数
		// m3u8Download.setRetryCount(10);
		// // 设置连接超时时间（单位：毫秒）
		// m3u8Download.setTimeoutMillisecond(10000L);
		// /*
		// * 设置日志级别 可选值：NONE INFO DEBUG ERROR
		// */
		// // 设置监听器间隔（单位：毫秒）
		// m3u8Download.setInterval(500L);
		// // 添加额外请求头
		// /*
		// * Map<String, Object> headersMap = new HashMap<>();
		// * headersMap.put("Content-Type", "text/html;charset=utf-8");
		// * m3u8Download.addRequestHeaderMap(headersMap);
		// */
		// boolean isFinished = false;
		// // 添加监听器
		// m3u8Download.addListener(new DownloadListener() {
		// @Override
		// public void start() {
		// System.out.println("开始下载！");
		// }
		//
		// @Override
		// public void process(String downloadUrl, int finished, int sum, float
		// percent) {
		// System.out
		// .println("下载网址：" + downloadUrl + "\t已下载" + finished + "个\t一共" + sum +
		// "个\t已完成" + percent + "%");
		// }
		//
		// @Override
		// public void speed(String speedPerSecond) {
		// System.out.println("下载速度：" + speedPerSecond);
		// }
		//
		// @Override
		// public void end() {
		// System.out.println("下载完毕");
		// }
		// });
		// // 开始下载
		// m3u8Download.start();
	}
}
