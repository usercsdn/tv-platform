chromeexe文件下载地址：https://npm.taobao.org/mirrors/chromedriver
https://my.oschina.net/u/4315935/blog/4187572
https://zhuanlan.zhihu.com/p/60852696
https://www.jianshu.com/p/7a2541cd2ffa
滚动条相关：https://blog.csdn.net/weixin_41082042/article/details/79164046
鼠标点击相关：https://blog.csdn.net/huilan_same/article/details/52305176
m3u8在线播放器：https://m3u8play.com/
seleuim操作chrome教材：https://www.kancloud.cn/apachecn/guru99-zh/1953814
springboot集成robot实现selenium键盘操作：https://blog.csdn.net/weixin_44216706/article/details/107138556
批量kill进程：ps -ef|grep google|grep -v grep|cut -c 9-15|xargs kill -9


解析blob:http格式：
Object executeAsyncScript = webDriver.executeScript("var blob=function myFunction(url){	var xhr = new XMLHttpRequest();   	xhr.open('get', url, true);	xhr.responseType = \"blob\";    xhr.onload = function() {  	  console.log(this.response);	  let reader = new FileReader(); console.log('test'+this.response);	reader.readAsDataURL(this.response); 	reader.onload = function() {	console.log(reader.result);    };	    };  xhr.send();console.log(\"页面加载完成\"+url);};blob(arguments[0]);console.log(arguments[0]);",
					source);
查看blob:http命令：
chrome://media-internals/
https://blog.csdn.net/LY_Dengle/article/details/78543770


ffmpeg常用命令：
https://zhuanlan.zhihu.com/p/67878761
https://blog.nowcoder.net/n/78f1c2007b024ae983b9d9e3d0fdcf1f

ffmpeg指令解析：
https://www.ruanyifeng.com/blog/2020/01/ffmpeg.html
https://blog.csdn.net/ice_ly000/article/details/88032056
https://blog.nowcoder.net/n/78f1c2007b024ae983b9d9e3d0fdcf1f
javacpp使用ffmpeg：https://protogalaxy.me/ffmpeg%E4%B8%8E%E5%85%B6javacpp%E5%AE%9E%E7%8E%B0%E8%B8%A9%E5%9D%91%E7%BA%AA%E5%BD%95/
windows下自建rtmp推流服务器：https://blog.csdn.net/u014552102/article/details/100906058


ffmpeg指令实例：
列出电脑的设备：ffmpeg -list_devices true -f dshow -i dummy
测试摄像头是否可用：ffplay -f dshow -i video="Integrated Camera"  
查看摄像头和麦克风信息：ffmpeg -list_options true -f dshow -i video="Integrated Camera"  
按分辨率压缩：“-threads 25 -preset ultrafast ”这一串是多线程执行
D:\soft\ffmpeg-4.4-full_build\bin\ffmpeg.exe -i 他从停尸房醒来_1618918776605.mp4 -threads 25 -preset ultrafast -strict -2 -b:v 1291.2k -bufsize 1291.2k D:\download\他从停尸房醒来_1618918776605\他从停尸房醒来_16189187766055.mp4

ffmpeg.exe -y -threads 12 -i mda-mauk53gy1nvtvzbbb.mp4 -b:v 1775k 1231_ffmpeg.mp4

time ffmpeg -y -threads 2 -i in.mp4 -s 320x240 -b 290000 out290.mp4
ffmpeg -y -threads 2 -i mda-mauk53gy1nvtvzbbb.mp4 -s 320x240 -b 290000 out290.mp4

ffmpeg.exe -y   -i mda-mauk53gy1nvtvzbbb.mp4 -b:v 1775k 1231_ffmpeg2.mp4

ffmpeg -f dshow -i video="Integrated Camera" -vcodec libx264 -preset:v ultrafast -tune:v zerolatency -f flv rtmp://127.0.0.1/live/test1


ffmpeg -f dshow -i audio="@device_cm_{33D9A762-90C8-11D0-BD43-00A0C911CE86}\wave_{8788F632-8623-4719-8197-3681D3C64157}" -vcodec libx264 -preset:v ultrafast -tune:v zerolatency -f flv rtmp://127.0.0.1/live/test1
ffmpeg -i mda-mauk53gy1nvtvzbbb.mp4 -vcodec libx265 -crf 20 output.mp4


acfun:
https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/hls/Rw6y5vVWajyb2P_rhoghbWoNJZdtlfjtAKAEcko0KuddMDJT32sW3EKUXV_ypo_7.m3u8?pkey=ABCeaZZDXxOHapqnerk3epn61n9onAyJBG3wlp_QStRtVMkJ7ETnlWxNQgzbKfG92NRF3Ec0BeBuT4lMA8accadZj9zRwPSubrwg7FBG8q8wVbtNS-WS6vXe9D9Z-jwRK7_rQO-j8UkEXYsX2_X7a2gj5omcsab6ss9SPzh73zt_BDRGZwuNXwgVFRVwyBjukiJXI1VP6llcJBZy0eGTdJ8gmaeWxtU0hFYtfRB8dIMQIsU2m_bURHgLtUI8xBBQmzo&safety_id=AAK-EP12J_1xzKI_AB7caO1M


curl -s https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/hls/Rw6y5vVWajyb2P_rhoghbWoNJZdtlfjtAKAEcko0KuddMDJT32sW3EKUXV_ypo_7.m3u8?pkey=ABCeaZZDXxOHapqnerk3epn61n9onAyJBG3wlp_QStRtVMkJ7ETnlWxNQgzbKfG92NRF3Ec0BeBuT4lMA8accadZj9zRwPSubrwg7FBG8q8wVbtNS-WS6vXe9D9Z-jwRK7_rQO-j8UkEXYsX2_X7a2gj5omcsab6ss9SPzh73zt_BDRGZwuNXwgVFRVwyBjukiJXI1VP6llcJBZy0eGTdJ8gmaeWxtU0hFYtfRB8dIMQIsU2m_bURHgLtUI8xBBQmzo&safety_id=AAK-EP12J_1xzKI_AB7caO1M | grep '.ts' | xargs -n 1 -I{} echo 'https://tx-safety-video.acfun.cn/mediacloud/{}' > download-list.txt
