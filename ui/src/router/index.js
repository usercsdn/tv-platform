import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from '../views/Index.vue'
import Search from '../views/Search.vue'
import Details from '../views/Details.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Index',
    component: Index
  },
  {
    path: '/search',
    name: 'Search',
    component: Search
  },
  {
    path: '/details/:id',
    name: 'Details',
    component: Details
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
