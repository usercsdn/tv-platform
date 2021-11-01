<template>
  <div class="ranked-list">
    <div class="ranked-list-header"><span class="header-title"><a href="/rank/list/#cid=60;range=1;pcid=-3"
           target="_blank">排行榜</a></span>
      <div class="right-area"><span class="day-rank rank-item active">日榜</span></div>
    </div>
    <div class="ranked-list-content">
      <div class="list-content-videos active">
        <div class="video-item-big log-item"
             v-for="(video,index) in card.right.videoRecommand"
             :key="'video-item-big'+index">
          <div class="block-left"><span>1</span><a :href="'/details/'+video.id"
               :title="video.name"
               target="_blank"><img :src="video.videoImage"
                   :alt="video.name"
                   loading="lazy"></a></div>
          <div class="block-right"><a class="video-title text-overflow"
               :href="'/details/'+video.id"
               :title="video.name"
               target="_blank">{{video.name}}</a><a class="video-up text-overflow"
               href="/u/16986934"
               :title="video.videoAuthor"
               target="_blank">
              UP:&nbsp;{{video.videoAuthor}}</a>
            <div class="video-info"><span class="icon icon-view-player">{{video.likeCount}}</span><span class="icon icon-comments">280</span></div>
          </div>
        </div>

        <div class="video-item-small log-item"
             v-for="(video,index) in card.right.videos"
             :key="'video-item-small'+index"><span>{{card.right.videoRecommand.length=card.right.videoRecommand.length+1}}</span><a class="video-title text-overflow"
             :href="'/details/'+video.id"
             :title="video.name"
             target="_blank">{{video.name}}</a></div>
      </div>

    </div><a :href="'/search?cardId='+card.id"
       target="_blank"
       class="more">
      查看完整榜单
      <i class="icon icon-arrow-slim-right"></i></a>
  </div>
</template>

<script>
export default {
  name: 'IndexRight',
  props: ['cardOfIndex'],
  data: function () {
    return {
      card: this.cardOfIndex,
    }
  },
  methods: {},
}
</script>

<style scoped>
</style>