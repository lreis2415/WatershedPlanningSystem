import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/home'
import Optimize from '@/views/optimize'
import Analysis from '@/views/analysis'
// import Plots from '@/views/Plots'
// import Plotsv2 from '@/views/Plotsv2'
// import BMPEffectivenessLinePlots from '@/views/BMPEffectivenessLinePlots'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/optimize',
    name: 'Optimize',
    component: Optimize
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: Analysis
  },
  // {
  //   path: '/plots',
  //   name: 'Plots',
  //   component: Plots
  // },
  // {
  //   path: '/plotsv2',
  //   name: 'Plots V2',
  //   component: Plotsv2
  // },
  // {
  //   path: '/bmp-effectiveness-line-plots',
  //   name: 'BMP effectiveness line plots',
  //   component: BMPEffectivenessLinePlots
  // },
  // {
  //   path: '/about',
  //   name: 'About',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  // }
]

const router = new VueRouter({
  base:'/bmps-order-optimization/',
  routes
})

export default router
