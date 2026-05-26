import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSellerUserStore } from '@/stores/sellerUserStore'

const sellerRequest = axios.create({
  baseURL: '',  // 空字符串，让请求走 vue.config.js 中的代理
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
sellerRequest.interceptors.request.use(
  (config) => {
    const sellerUserStore = useSellerUserStore()

    if (sellerUserStore.sellerToken) {
      config.headers.seller_token = sellerUserStore.sellerToken
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
sellerRequest.interceptors.response.use(
  (response) => {
    const res = response.data

    if (res.code === 200) {
      return res
    }

    ElMessage.error(res.msg || '请求失败')

    if (res.code === 401) {
      ElMessageBox.confirm(
        '登录已过期，请重新登录',
        '提示',
        {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const sellerUserStore = useSellerUserStore()

        sellerUserStore.sellerLogout()

        window.location.href = '/seller/login'
      })
    }

    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    ElMessage.error(error.response?.data?.msg || '网络异常')

    return Promise.reject(error)
  }
)

export default sellerRequest