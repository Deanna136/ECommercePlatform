import request from './buyer-request'

export const cartApi = {
  getCart() {
    return request({ url: '/buyer/cart', method: 'get' })
  },
  addToCart(data) {
    return request({ url: '/buyer/cart/add', method: 'post', data })
  },
  updateCartItem(data) {
    return request({ url: '/buyer/cart/update', method: 'put', data })
  },
  removeCartItem(cartItemId) {
    return request({ url: `/buyer/cart/remove/${cartItemId}`, method: 'delete' })
  }
}