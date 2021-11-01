<template>
  <div>
    <Header v-bind:positionParams="'fixed'"
            v-bind:linkTarget="'_blank'"
            v-bind:isRefreshParams="'false'"></Header>
    <Banner></Banner>
    <Direct></Direct>
    <IndexVideo v-for="(card,index) in cards"
                :key="index"
                v-bind:cardOfIndex="card"></IndexVideo>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from '../components/Header'
import Banner from '../components/Banner'
import Direct from '../components/Direct'
import IndexVideo from '../components/Index/IndexVideo'
import Footer from '../components/Footer'

export default {
  name: 'Index',
  data() {
    return {
      cards: {},
    }
  },
  components: {
    Header,
    Banner,
    Direct,
    IndexVideo,
    Footer,
  },
  methods: {
    getCards() {
      const _this = this
      _this.$axios.get('/card/getCards').then((res) => {
        console.log(res.data)
        _this.cards = res.data.data
      })
    },
  },
  created() {
    this.getCards()
  },
}
</script>