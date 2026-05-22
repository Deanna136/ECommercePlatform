import { userApi } from './buyer-user'
import { goodsApi } from './buyer-goods'
import { cartApi } from './buyer-cart'
import { orderApi } from './buyer-order'
import { addressApi } from './buyer-address'
import { payApi } from './buyer-pay'

export const buyerApi = {
  user: userApi,
  goods: goodsApi,
  cart: cartApi,
  order: orderApi,
  address: addressApi,
  pay: payApi
}

export { userApi, goodsApi, cartApi, orderApi, addressApi, payApi }