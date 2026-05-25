
import { createRouter, createWebHistory } from 'vue-router'
import { sellerRoutes } from './sellerRouter'
import { useSellerUserStore } from '@/stores/sellerUserStore'


const routes = [
  {
    path: '/',
    redirect: '/seller/login'
  },

  ...sellerRoutes
]


const router = createRouter({
  history: createWebHistory(),
  routes
})

// 卖家路由守卫
router.beforeEach((to, from, next) => {
  const sellerUserStore = useSellerUserStore()

  // 不需要登录的页面
  const whiteList = ['/seller/login', '/seller/register']

  // 访问卖家页面
  if (to.path.startsWith('/seller')) {
    // 白名单直接放行
    if (whiteList.includes(to.path)) {
      next()
      return
    }

    // 没有token
    if (!sellerUserStore.sellerToken) {
      next('/seller/login')
      return
    }
  }

  next()
})

export default router
