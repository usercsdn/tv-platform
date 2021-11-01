<template>
  <div class="home-main-content">
    <div id="pagelet_amusement"
         style="visibility: visible;">
      <div class="normal-module">
        <IndexLeft v-bind:cardOfIndex="cardOfIndex"></IndexLeft>
        <IndexRight v-bind:cardOfIndex="cardOfIndex"></IndexRight>
      </div>
    </div>
  </div>
</template>

<script>
import IndexLeft from './IndexLeft'
import IndexRight from './IndexRight'
export default {
  name: 'IndexVideo',
  components: {
    IndexLeft,
    IndexRight,
  },
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
/* @import './index.css'; */
</style>