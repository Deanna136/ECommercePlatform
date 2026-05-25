import axios from 'axios'
import { useRouter } from 'vue-router'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 改为 ''，使用 /admin 或 /api 前缀由请求本身决定
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // Debug: 输出请求信息，便于定位代理或URL问题
    try {
      const fullUrl = `${config.baseURL || ''}${config.url || ''}`
      console.debug('[axios request] ', config.method ? config.method.toUpperCase() : 'UNKNOWN', fullUrl, config)
    } catch (e) {
      console.debug('[axios request] error building debug info', e)
    }

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
    // Debug: 输出响应信息，便于定位404/500
    try {
      console.debug('[axios response] ', response.status, response.config && `${response.config.baseURL || ''}${response.config.url || ''}`)
    } catch (e) {
      console.debug('[axios response] debug error', e)
    }

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
        // 跳转到管理员登录页
        window.location.href = '/admin/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    // 网络错误或服务器错误
    if (error.response) {
      try {
        console.debug('[axios response error] ', error.response.status, error.config && `${error.config.baseURL || ''}${error.config.url || ''}`)
      } catch (e) {
        console.debug('[axios response error] debug error', e)
      }
      if (error.response.status === 401) {
        localStorage.removeItem('admin')
        window.location.href = '/admin/login'
      }
    }
    return Promise.reject(error)
  }
)

export default service