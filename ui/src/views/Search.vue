<template>
  <div>
    <Header v-bind:positionParams="'fixed'"
            v-bind:linkTarget="'_self'"
            v-bind:isRefreshParams="'true'"></Header>
    <Direct></Direct>
    <SearchVideo v-bind:keywordParams="keyword"
                 v-bind:cardIdParams="cardId"></SearchVideo>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from '../components/Header'
import Direct from '../components/Direct'
import Footer from '../components/Footer'
import SearchVideo from '../components/search/SearchVideo'

export default {
  name: 'Search',
  data() {
    return {
      keyword: '',
      cardId: '',
    }
  },
  components: {
    Header,
    Direct,
    Footer,
    SearchVideo,
  },
  methods: {
    getSearch() {
      let keyword = this.$route.query.keyword
      let cardId = this.$route.query.cardId
      if (keyword == undefined) {
        keyword = ''
      }
      if (cardId == undefined) {
        cardId = ''
      }
      this.keyword = keyword
      this.cardId = cardId
    },
  },
  created() {
    this.getSearch()
  },
}
</script>