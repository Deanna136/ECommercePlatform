import request from './buyer-request'

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
  },
  // 上传图片到腾讯云
  async uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/buyer/file/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}