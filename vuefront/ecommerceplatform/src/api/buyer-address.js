import request from './request'

export const addressApi = {
  getList() {
    return request({ url: '/buyer/address/list', method: 'get' })
  },
  add(data) {
    return request({ url: '/buyer/address/add', method: 'post', data })
  },
  update(data) {
    return request({ url: '/buyer/address/update', method: 'put', data })
  },
  delete(id) {
    return request({ url: `/buyer/address/delete/${id}`, method: 'delete' })
  },
  setDefault(id) {
    return request({ url: `/buyer/address/default/${id}`, method: 'put' })
  }
}