<template>
  <div class="search__main">
    <div class="search-toast"></div>
    <div class="search__main__container">
    </div>
    <div class="search__main__tab show1">
      <div class="main__tab__item"
           data-type="complex">综合</div>
    </div>
    <div class="search__main__list show1">
      <div class="main__list__item"
           v-if="params.datas!==undefined && params.datas.length > 0">
        <div id="pagelet_complex"
             style="visibility: visible;">
          <div class="complex-list">
            <div id="complex-list">
              <div class="search-video"
                   v-for="(video,index) in params.datas"
                   :key="'search-video-'+index">
                <div class="video__cover"><a :href="'/details/'+video.id"
                     target="_blank"><img :src=video.videoImage
                         alt=""><span class="video__duration">{{video.durationFormat}}</span></a></div>
                <div class="video__main">
                  <div class="video__main__title"><a :href="'/details/'+video.id"
                       target="_blank"><span v-html="video.name"></span></a></div>
                  <div class="video__main__info"><span class="info__view-count">{{video.likeCount}}次播放</span>
                  </div>
                  <div class="video__main__intro ellipsis2">视频来自快手等平台，如有侵权联系删除，谢谢！</div>
                </div>
              </div>
              <Pagination :pageIndex="currentPage"
                          :total="count"
                          :pageSize="pageSize"
                          @change="pageChange">
              </Pagination>

            </div>
          </div>

        </div>
      </div>
      <div class="main__list__item">
        <div id="pagelet_up"
             style="visibility: visible;">
          <div class="up-list">
            <div id="up-list">
              <div class="empty-page">
                <div class="content"><img class="empty-img"
                       src="https://imgs.aixifan.com/2020/03/06/page_empty@3x-2_f184121f613f1af47754d43276f4aba3.png"
                       alt="empty-img"><span class="text">空空如也...</span></div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Pagination from '../page/Page'
export default {
  name: 'SearchVideo',
  props: ['keywordParams', 'cardIdParams'],
  data: function () {
    return {
      params: '',
      keyword: this.keywordParams,
      cardId: this.cardIdParams,

      pageSize: 20, //每页显示20条数据
      currentPage: 1, //当前页码
      count: 200, //总记录数
    }
  },
  components: {
    Pagination,
  },
  methods: {
    getSearch() {
      const _this = this
      _this.$axios
        .get(
          '/search/keyword?key=' +
            this.keyword +
            '&cardId=' +
            this.cardId +
            '&limit=' +
            this.currentPage +
            '&offset=' +
            this.pageSize
        )
        .then((res) => {
          _this.params = res.data.data
          if (_this.params === null || _this.params.datas === null) {
            _this.params = {}
            _this.params.datas = []
          }
          _this.currentPage = _this.params.limit
          _this.count = _this.params.total
        })
    },
    pageChange(page) {
      this.currentPage = page
      this.getSearch()
    },
  },
  created() {
    this.getSearch()
  },
}
</script>
<style scoped>
@import './search.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/common/widget/searchBox/index.91efa775f0e042057c76.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/upList/index.4acb0f81020543c6cc4b.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/albumList/index.072f6fd13c419fc724f5.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/common/filter/index.4074a7669bac26e60e8e.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/articleList/index.7abb596fc0535c92489e.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/videoList/index.8566f0332d310178065e.css';
@import '//ali-imgs.acfun.cn/kos/nlav10360/static/search/widget/complexList/index.fcb32c99f553c333c6f1.css';
</style>