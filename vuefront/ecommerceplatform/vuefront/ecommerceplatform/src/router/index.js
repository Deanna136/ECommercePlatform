import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Admin from '../views/Admin.vue'   // 您的原 admin.vue 组件路径

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true }   // 需要登录才能访问
  },
  {
    path: '/',
    redirect: '/login'   // 根路径重定向到 admin
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：检查登录状态
router.beforeEach((to, from, next) => {
  const adminStr = localStorage.getItem('admin')
  const isAuthenticated = adminStr !== null

  if (to.meta.requiresAuth && !isAuthenticated) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    // 已登录的情况下访问登录页，直接跳转到 admin
    next('/admin')
  } else {
    next()
  }
})

export default router