import request from './request'

export const userApi = {
  login(data) {
    return request({ url: '/buyer/login', method: 'post', data })
  },
  register(data) {
    return request({ url: '/buyer/register', method: 'post', data })
  },
  updateUserInfo(data) {
    return request({ url: '/buyer/updateInfo', method: 'put', data })
  },
  getLocalUserInfo() {
    const userStr = localStorage.getItem('buyer_user')
    return userStr ? JSON.parse(userStr) : null
  },
  saveUserInfo(userInfo) {
    localStorage.setItem('buyer_user', JSON.stringify(userInfo))
  },
  clearUserInfo() {
    localStorage.removeItem('buyer_user')
  }
}