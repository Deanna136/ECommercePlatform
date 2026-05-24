import request from './request'

export const goodsApi = {
  getProductList() {
    return request({ url: '/buyer/product/list', method: 'get' })
  },
  getProductDetail(id) {
    return request({ url: `/buyer/product/${id}`, method: 'get' })
  },
  queryProducts(params) {
    return request({ url: '/buyer/product/query', method: 'get', params })
  }
}