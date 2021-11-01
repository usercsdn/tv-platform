<template>
  <div id="main"
       class="single-part">
    <div id="main-content"
         class="clearfix"
         style="width: 1164px; margin: 0px auto;">
      <LeftDetails v-bind:videoOfParent="videoIdParams"></LeftDetails>
      <RightDetails v-bind:videoOfParent="videoIdParams"
                    @pushVideoId="pushVideoId"></RightDetails>
    </div>
  </div>
</template>

<script>
import LeftDetails from './LeftDetails'
import RightDetails from './RightDetails'

export default {
  name: 'DetailsVideo',
  props: ['videoId'],
  components: {
    LeftDetails,
    RightDetails,
  },
  data: function () {
    return {
      videoIdParams: this.videoId,
    }
  },
  methods: {
    pushVideoId(data) {
      console.log('father:' + data)
      this.videoIdParams = data
    },
  },
}
</script>

<style scoped>
@import './details.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/userCard/userCard.24512576fa7bbacbf480.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/notEnoughBanana/notEnoughBanana.4a42b5e1d8dc35be9028.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/toast/toast.8fac7db773ce15b3b732.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/customToast/customToast.e2714e814888ddb6c43d.css';

@import '//ali-imgs.acfun.cn/kos/nlav10360/static/newVideo/widget/right-recommend/right-recommend.821060d1e69500db4d74.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/newVideo/widget/comment/comment.5499520ecd24535cccd0.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/popConfirm/popConfirm.d97921ad0d27f6ce0bd6.css';

@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/followGroup/followGroup.8b5eba7c551e11ad63af.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/footer/footer.a947dfef28d5d091fe20.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/toolbar/toolbar.42c7b96be64f2452eb28.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/newVideo/widget/bottom-recommend/bottom-recommend.521804a8dab045345695.css';
</style>