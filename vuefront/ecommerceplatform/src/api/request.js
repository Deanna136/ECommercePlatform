import axios from 'axios'

const service = axios.create({
  baseURL: '/',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const buyerToken = localStorage.getItem('buyer_token')
    if (buyerToken) {
      config.headers['buyer_token'] = buyerToken
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      if (res.code === 401 || res.msg?.includes('Token')) {
        localStorage.removeItem('buyer_token')
        localStorage.removeItem('buyer_user')
        window.location.href = '/buyer/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    const errMsg = error?.response?.data?.msg || error?.message || '请求失败'
    if (errMsg.includes('Token')) {
      localStorage.removeItem('buyer_token')
      localStorage.removeItem('buyer_user')
      window.location.href = '/buyer/login'
    }
    return Promise.reject(new Error(errMsg))
  }
)

const request = {
  get: async function (url, params) {
    try {
      const response = await service.get(url, { params })
      return response
    } catch (error) {
      throw error
    }
  },

  post: async function (url, data) {
    try {
      const response = await service.post(url, data)
      return response
    } catch (error) {
      throw error
    }
  },

  put: async function (url, data) {
    try {
      const response = await service.put(url, data)
      return response
    } catch (error) {
      throw error
    }
  },

  delete: async function (url, params) {
    try {
      const response = await service.delete(url, { params })
      return response
    } catch (error) {
      throw error
    }
  }
}

const requestFn = async function (config) {
  try {
    const response = await service(config)
    return response
  } catch (error) {
    throw error
  }
}

requestFn.get = request.get
requestFn.post = request.post
requestFn.put = request.put
requestFn.delete = request.delete

export default requestFn