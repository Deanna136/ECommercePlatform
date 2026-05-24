import request from './request'

export const orderApi = {
  getOrderList() {
    return request({ url: '/buyer/order/list', method: 'get' })
  },
  queryOrders(params) {
    return request({ url: '/buyer/order/query', method: 'get', params })
  },
  getOrderDetail(orderId) {
    return request({ url: `/buyer/order/${orderId}`, method: 'get' })
  },
  createOrder(data) {
    return request({ url: '/buyer/order/create', method: 'post', data })
  },
  refundOrder(orderId, data) {
    return request({ url: `/buyer/order/${orderId}/refund`, method: 'put', data })
  },
  updateOrderInfo(orderId, data) {
    return request({ url: `/buyer/order/${orderId}/updateInfo`, method: 'put', data })
  },
  reportAbnormal(orderId, data) {
    return request({ url: `/buyer/order/${orderId}/reportAbnormal`, method: 'put', data })
  }
}