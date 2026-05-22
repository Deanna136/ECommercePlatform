import request from './request'

export const payApi = {
  getPayStatus(orderId) {
    return request({ url: `/buyer/order/${orderId}`, method: 'get' })
  }
}