<template>
  <div>
    <Header v-bind:positionParams="''"></Header>
    <DetailsVideo v-bind:videoId="videoId"></DetailsVideo>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from '../components/Header'
import Footer from '../components/Footer'
import DetailsVideo from '../components/details/DetailsVideo'

export default {
  name: 'Details',
  data() {
    return { videoId: {} }
  },
  components: {
    Header,
    Footer,
    DetailsVideo,
  },
  methods: {
    getVideo() {
      const keyword = this.$route.params.id
      console.log(keyword)
      const _this = this
      _this.videoId = keyword
      // _this.$axios.get('/video/getVideo?id=' + keyword).then((res) => {
      //   _this.video = res.data.data
      //   console.log(_this.video)
      // })
    },
  },
  created: function () {
    this.getVideo()
  },
  // created() {
  //   this.getVideo()
  // },
}
</script>