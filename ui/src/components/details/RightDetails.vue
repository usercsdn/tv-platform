<template>
  <div class="right-column">
    <div id="pagelet_newrecommend"
         style="visibility: visible;">
      <div class="recmd">
        <h2 class="recommend-title">大家都在看</h2>
        <div class="clearfix area recommendation">
          <figure class="fl block-box block-video no-animate auto-recommend block-show"
                  v-for="(video,index) in videos"
                  :key="'recommand-video-'+index"><a class="block-img has-danmu"
               @click="changeVideo(video.id)">
              <div class="action-data"><span class="viewCount">{{video.likeCount}}</span><span class="commentCount">{{video.durationFormat}}</span></div><img :src="video.videoImage"
                   width="100%"
                   height="100%"
                   :alt="video.name">
            </a>
            <figcaption class="block-title">
              <h3><a href="/v/ac4963929"
                   target="_blank"
                   :title="video.name">{{video.name}}</a></h3><a class="uper-name"
                 target="_blank"
                 href="/u/4773288">UP：GRE高频词汇-CATTI</a>
            </figcaption>
          </figure>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RightDetails',
  props: ['videoOfParent'],
  data() {
    return {
      videos: {},
    }
  },
  methods: {
    getDate() {
      const _this = this
      _this.$axios
        .get('/video/getRecommand?id=' + this.videoOfParent + '&offset=6')
        .then((res) => {
          _this.videos = res.data.data
          console.log('abc:' + _this.videos.length)
        })
    },
    changeVideo(id) {
      console.log('videoId:' + id)
      this.$emit('pushVideoId', id)
    },
  },
  created() {
    this.getDate()
  },
}
</script>

<style scoped>
</style>