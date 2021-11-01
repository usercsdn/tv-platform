<template>
  <div data-c-w-header=""
       id="header"
       :style="'position:'+positionParams">
    <div class="wp clearfix header-top-con">
      <div id="search-box"
           class="fl search-box">
        <div class="form"><input type="text"
                 placeholder="爽夏上线—摸鱼我是专业哒！"
                 autocomplete="off"
                 v-model="keywordValue"
                 id="search-text"><button id="search-btn"
                  class="search-btn">
            <router-link :to="{name: 'Search', query: {keyword: keywordValue}}"
                         :target=linkTarget
                         @click.native="refresh">
              <i class="icon-ks icon-dao-hang-search">
              </i>
            </router-link>
          </button>
        </div>
      </div>
    </div>
    <div class="nav-sub">
      <div class="nav-sub-con"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Header',
  props: ['positionParams', 'linkTarget', 'isRefreshParams'],
  data() {
    return {
      keywordValue: '',
    }
  },
  methods: {
    refresh: function () {
      if (this.isRefreshParams === 'true') {
        this.$router.go(0)
      }
    },
  },
}
</script>

<style scoped>
</style>
