import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Admin from '../views/Admin.vue'
import { buyerRoutes } from './buyer-router'

const routes = [
  { path: '/login', name: 'Login', component: Login, meta: { requiresAuth: false } },
  { path: '/admin', name: 'Admin', component: Admin, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/', redirect: '/buyer/login' },
  ...buyerRoutes
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) document.title = to.meta.title

  const buyerToken = localStorage.getItem('buyer_token')
  const isBuyerLoggedIn = !!buyerToken

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

  // 管理员端守卫（保留原逻辑，可根据需要修改）
  if (to.path.startsWith('/admin')) {
    const adminToken = localStorage.getItem('admin_token')
    if (to.meta.requiresAuth && !adminToken) {
      next('/login')
      return
    }
    if (to.path === '/login' && adminToken) {
      next('/admin')
      return
    }
    next()
    return
  }

  next()
})

export default router