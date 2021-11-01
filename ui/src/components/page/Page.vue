<template>

  <div id="complex-list__pager">
    <div id="pager_97981830598217EF"
         class="pager__wrapper"><a :class="['pager__btn', 'pager__btn__prev'
                     ,{'pager__btn__disabled' : index === 1}]"
         @click="prev">上一页</a>
      <a :class="['pager__btn', {'pager__btn__selected' : index === pager}]"
         v-for="(pager,indexOf) in pagers"
         @click="go(pager)"
         :key="'search-page-'+indexOf">{{ pager }}</a>
      <span class="pager__ellipsis"
            v-if="showNextMore">...</span>
      <a class="pager__btn"
         v-if="pages !== lastNum && pagers.length>0"
         @click="go(pages)">{{this.pages}}</a><a class="pager__btn pager__btn__next"
         :class="['pager__btn', 'pager__btn__next'
                     ,{'pager__btn__disabled' : index === lastNum}]"
         @click="next">下一页</a>
      <div class="pager__input">跳至<input type="text"
               v-model="testVal"
               @blur="go(testVal)">页</div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Pagination',
  //通过props来接受从父组件传递过来的值
  props: {
    //页面中的可见页码，其他的以...替代, 必须是奇数
    perPages: {
      type: Number,
      default: 5,
    },

    //当前页码
    pageIndex: {
      default: 1,
    },

    //每页显示条数
    pageSize: {
      type: Number,
      default: 10,
    },

    //总记录数
    total: {
      type: Number,
      default: 1,
    },
  },
  methods: {
    prev() {
      if (this.index > 1) {
        this.go(this.index - 1)
      }
    },
    next() {
      if (this.index < this.pages) {
        this.go(this.index + 1)
      }
    },
    first() {
      if (this.index !== 1) {
        this.go(1)
      }
    },
    last() {
      if (this.index != this.pages) {
        this.go(this.pages)
      }
    },
    go(page) {
      if (this.index !== page) {
        if (isNaN(page)) {
          return
        }
        if (parseInt(page) > this.pages) {
          return
        }
        this.index = page
        //父组件通过change方法来接受当前的页码
        this.$emit('change', this.index)
      }
    },
  },
  computed: {
    //计算总页码
    pages() {
      return Math.ceil(this.size / this.limit)
    },
    //计算页码，当count等变化时自动计算
    pagers() {
      const array = []
      const perPages = this.perPages
      const pageCount = this.pages
      let current = this.index
      const _offset = (perPages - 1) / 2

      const offset = {
        start: current - _offset,
        end: current + _offset,
      }

      //-1, 3
      if (offset.start < 1) {
        offset.end = offset.end + (1 - offset.start)
        offset.start = 1
      }
      if (offset.end > pageCount) {
        offset.start = offset.start - (offset.end - pageCount)
        offset.end = pageCount
      }
      if (offset.start < 1) offset.start = 1

      this.showPrevMore = offset.start > 1
      this.showNextMore = offset.end < pageCount

      for (let i = offset.start; i <= offset.end; i++) {
        array.push(i)
      }
      this.lastNum = array[array.length - 1]
      return array
    },
  },
  data() {
    return {
      index: this.pageIndex, //当前页码
      limit: this.pageSize, //每页显示条数
      size: this.total || 1, //总记录数
      lastNum: 0, //总记录数
      testVal: '',
      showPrevMore: false,
      showNextMore: false,
    }
  },
  watch: {
    pageIndex(val) {
      this.index = val || 1
    },
    pageSize(val) {
      this.limit = val || 10
    },
    total(val) {
      this.size = val || 1
    },
  },
}
</script>