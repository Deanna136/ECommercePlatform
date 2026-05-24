import axios from 'axios'
import { useRouter } from 'vue-router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const admin = localStorage.getItem('admin')
    if (admin) {
      try {
        const adminObj = JSON.parse(admin)
        if (adminObj.token) {
          config.headers['admin_token'] = adminObj.token
        }
      } catch (e) {
        console.error('解析admin信息失败', e)
      }
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果是二进制响应（Blob），直接返回原始响应
    if (res instanceof Blob) {
      return response
    }
    
    // 业务逻辑错误处理
    if (res.code !== 200) {
      // Token失效或未登录
      if (res.code === 401) {
        localStorage.removeItem('admin')
        // 跳转到登录页
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    // 网络错误或服务器错误
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('admin')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default service