
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Admin from '../views/Admin.vue'
import { buyerRoutes } from './buyer-router'
import { sellerRoutes } from './sellerRouter'
import { useSellerUserStore } from '@/stores/sellerUserStore'

const routes = [
  { path: '/admin/login', name: 'Login', component: Login, meta: { requiresAuth: false } },
  { path: '/admin', name: 'Admin', component: Admin, meta: { requiresAuth: true, role: 'admin' } },
  ...sellerRoutes,
  { path: '/', redirect: '/buyer/login' },
  ...buyerRoutes
]


const router = createRouter({
  history: createWebHistory(),
  routes
})

function getAdminTokenFromStorage() {
  // 首先尝试单独存储的 admin_token
  const adminToken = localStorage.getItem('admin_token')
  if (adminToken) return adminToken

  // 再尝试从 admin 对象里解析 token 字段
  const admin = localStorage.getItem('admin')
  if (admin) {
    try {
      const adminObj = JSON.parse(admin)
      if (adminObj && adminObj.token) return adminObj.token
    } catch (e) {
      console.error('解析admin信息失败', e)
    }
  }
  return null
}

router.beforeEach((to, from, next) => {
  if (to.meta.title) document.title = to.meta.title

  const buyerToken = localStorage.getItem('buyer_token')
  const isBuyerLoggedIn = !!buyerToken

  const sellerUserStore = useSellerUserStore()
  // 不需要登录的页面
  const whiteList = ['/seller/login', '/seller/register']

  // 买家端守卫
  if (to.path.startsWith('/buyer')) {
    if (to.meta.requiresAuth !== false && !isBuyerLoggedIn) {
      next({ path: '/buyer/login', query: { redirect: to.fullPath } })
      return
    }
    if (to.path === '/buyer/login' && isBuyerLoggedIn) {
      next('/buyer/home')
      return
    }
    next()
    return
  }

  // 管理员端守卫（更稳健的 token 检查）
  if (to.path.startsWith('/admin')) {
    const adminToken = getAdminTokenFromStorage()
    if (to.meta.requiresAuth && !adminToken) {
      next('/admin/login')
      return
    }
    if (to.path === '/admin/login' && adminToken) {
      next('/admin')
      return
    }
    next()
    return
  }

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
