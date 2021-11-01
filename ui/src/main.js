import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import "./axios"
import videojs from 'video.js'
import 'video.js/dist/video-js.css'


Vue.config.productionTip = false
Vue.prototype.$axios = axios
Vue.prototype.$video = videojs

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
