<template>
  <div class="module-left">
    <div class="module-left-header">
      <div class="left-area"><a :href="'/search?cardId='+card.id"
           target="_blank"><img class="header-icon"
               v-bind:src="card.icon"
               loading="lazy"><span class="header-title"
                style="display:娱乐; line-height:娱乐;">{{card.name}}</span>
          <span class="header-subtitle"></span></a>
        <span class="header-change"
              v-on:click="refreshCard(card.id)">
          <i class="icon icon-loading"></i> 换一批
        </span>
      </div>
      <div class="right-area">
        <a class="header-right-more"
           :href="'/search?cardId='+card.id"
           target="_blank">
          更多<i class="icon icon-arrow-slim-right"></i></a>
      </div>
    </div>
    <div class="video-list-4">
      <div class="big-image"
           v-for="(video,index) in card.left.videoRecommand"
           :key="'big-image-'+index">
        <a class="cover"
           :href="'/details/'+video.id"
           target="_blank"><img v-bind:src="video.videoImage"
               v-bind:alt="video.name"
               loading="lazy"><span class="mask-video-icon"><i class="icon icon-triangle-right"></i></span>
          <div class="danmaku-mask "
               data-did="24958499">
            <div class="space-danmaku"></div><span class="video-time">{{video.durationFormat}}</span>
          </div>
        </a>
        <a class="title text-overflow"
           :href="'/details/'+video.id"
           target="_blank"
           v-bind:title="video.name">{{video.name}}</a><span class="sub-title text-overflow"></span>
      </div>

      <div class="normal-video-container"
           v-bind:class="calculate(index)"
           v-for="(video,index) in card.left.videos"
           :key="index">
        <div class="normal-video log-item"><a class="normal-video-cover"
             target="_blank"
             :href="'/details/'+video.id"><img v-bind:src="video.videoImage"
                 v-bind:alt="video.name"
                 loading="lazy">
            <div class="danmaku-mask "
                 data-did="25025392">
              <div class="space-danmaku"></div><span class="video-time">{{video.durationFormat}}</span>
            </div>
          </a><a class="normal-video-title"
             target="_blank"
             :href="'/details/'+video.id"
             v-bind:title="video.name">{{video.name}}</a>
          <p class="normal-video-info"
             style="background-color:white"><span class="icon icon-view-player">{{video.likeCount}}</span><span class="icon icon-danmu">0</span></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IndexLeft',
  props: ['cardOfIndex'],
  data: function () {
    return {
      card: this.cardOfIndex,
    }
  },
  methods: {
    calculate(index) {
      if (index == 2 || index == 5 || index == 10) {
        return 'no-margin-right'
      }
      return ''
    },
    refreshCard(cardId) {
      console.log('cardId:' + cardId)
      const _this = this
      _this.$axios.get('/card/getCard?cardId=' + cardId).then((res) => {
        this.card = res.data.data
      })
      return ''
    },
  },
}
</script>

<style scoped>
</style>