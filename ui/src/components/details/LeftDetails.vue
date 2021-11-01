<template>
  <div class="left-column"
       style="width: 794px;">
    <div class="player-box">
      <div id="player">
        <div id="ACPlayer">
          <div class="container-player">
            <video id="myVideo"
                   class="video-js vjs-big-play-centered"
                   :poster="video.videoImage">
              <!-- <source src="video.vi"
                      type="video/mp4" /> -->
              <p class="vjs-no-js">
                To view this video please enable JavaScript, and consider upgrading to a
                web browser that
              </p>
            </video>
            <div class="container-plugins-outer"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="video-description clearfix dark-style">
      <h1 class="title"><span>{{video.name}}</span></h1>
      <div class="action-area">
        <div class="left-area">
          <div class="originalDeclare"
               style="display: none;"><span class="originalDeclareText icon-ks icon-icon_-jin-zhi-zhuan-zai">未经作者授权，禁止转载</span></div>
          <div class="views"><span class="viewsCount">{{video.likeCount}}</span>播放
          </div>
        </div>
        <div class="right-area">
          <div class="like"><span class="icon"></span><span class="likeCount">277</span></div>
          <div class="collection"><span class="icon"></span><span class="collectionCount">推荐</span></div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'LeftDetails',
  props: ['videoOfParent'],
  data() {
    return {
      video: {},
      myPlayer: {},
      retrySize: 0,
    }
  },
  mounted() {
    this.initVideo() //初始化视频播放器
  },
  methods: {
    getDate() {
      const _this = this
      _this.$axios
        .get('/video/getVideo?id=' + this.videoOfParent)
        .then((res) => {
          _this.video = res.data.data
          console.log('abc:' + _this.video.videoUrl)
          this.myPlayer.src([
            { type: this.video.type, src: this.video.videoUrl },
          ])
        })
    },
    initVideo() {
      const _this = this
      this.$nextTick(() => {
        console.log('nextTick is call')
        //初始化视频方法
        this.myPlayer = this.$video(
          myVideo,
          {
            //是否显示控制栏
            controls: true,
            //是否自动播放,muted:静音播放
            autoplay: false,
            language: 'zh-CN',
            //是否静音播放
            muted: false,
            //是否流体自适应容器宽高
            fluid: false,
            //设置视频播放器的显示宽度（以像素为单位）
            width: '794px',
            //设置视频播放器的显示高度（以像素为单位）
            height: '447px',
            LoadingSpinner: true,
            controlBar: {
              currentTimeDisplay: true,
              timeDivider: true,
              durationDisplay: true,
              remainingTimeDisplay: false,
              liveDisplay: false,
              playbackRates: [0.5, 1, 1.5, 2],
            },
          },
          function onPlayerReady() {
            console.log('Your player is ready!')
            this.on('error', function () {
              console.log('加载错误')
              _this.retrySize = _this.retrySize + 1
              if (_this.retrySize > 5) {
                return
              }
              setTimeout(function () {
                _this.getDate()
              }, 3000)
            }),
              this.on('ended', function () {
                console.log('视频播放结束')
              })
          }
        )
        this.$video.addLanguage('zh-CN', {
          'The media could not be loaded, either because the server or network failed or because the format is not supported.':
            '加载中,请稍等....',
          'No compatible source was found for this media.': '加载中,请稍等....',
        })
      })
    },
  },
  created() {
    this.getDate()
  },
  watch: {
    videoOfParent: 'getDate',
  },
}
</script>

<style scoped>
</style>