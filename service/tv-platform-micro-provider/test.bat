@Echo Off
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 3 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput0.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 13 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput1.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 23 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput2.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 33 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput3.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 43 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput4.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 53 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput5.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 63 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput6.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 73 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput7.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 83 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput8.mp4
call ffmpeg -y -i E:\u01\3699_141.mp4 -ss 93 -t 10 -c:a copy -vcodec libx264 -keyint_min 2 -g 1  -y E:\u01\keyoutput9.mp4

Pause