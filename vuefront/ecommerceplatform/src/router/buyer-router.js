const BuyerHome = () => import('../views/buyer-home.vue')
const BuyerGoodsDetail = () => import('../views/buyer-goods-detail.vue')
const BuyerCart = () => import('../views/buyer-cart.vue')
const BuyerOrderList = () => import('../views/buyer-order-list.vue')
const BuyerOrderDetail = () => import('../views/buyer-order-detail.vue')
const BuyerOrderCreate = () => import('../views/buyer-order-create.vue')
const BuyerPayResult = () => import('../views/buyer-pay-result.vue')
const BuyerUserCenter = () => import('../views/buyer-user-center.vue')
const BuyerUserInfo = () => import('../views/buyer-user-info.vue')
const BuyerLogin = () => import('../views/buyer-login.vue')
const BuyerRegister = () => import('../views/buyer-register.vue')
const BuyerAddress = () => import('../views/buyer-address.vue')

export const buyerRoutes = [
  { path: '/buyer', redirect: '/buyer/home' },
  { path: '/buyer/home', name: 'BuyerHome', component: BuyerHome, meta: { title: '商城首页', requiresAuth: true } },
  { path: '/buyer/goods/detail/:id', name: 'BuyerGoodsDetail', component: BuyerGoodsDetail, meta: { title: '商品详情', requiresAuth: true } },
  { path: '/buyer/cart', name: 'BuyerCart', component: BuyerCart, meta: { title: '购物车', requiresAuth: true } },
  { path: '/buyer/order/list', name: 'BuyerOrderList', component: BuyerOrderList, meta: { title: '我的订单', requiresAuth: true } },
  { path: '/buyer/order/detail/:id', name: 'BuyerOrderDetail', component: BuyerOrderDetail, meta: { title: '订单详情', requiresAuth: true } },
  { path: '/buyer/order/create', name: 'BuyerOrderCreate', component: BuyerOrderCreate, meta: { title: '确认订单', requiresAuth: true } },
  { path: '/buyer/pay/result', name: 'BuyerPayResult', component: BuyerPayResult, meta: { title: '支付结果', requiresAuth: true } },
  { path: '/buyer/user/center', name: 'BuyerUserCenter', component: BuyerUserCenter, meta: { title: '个人中心', requiresAuth: true } },
  { path: '/buyer/user/info', name: 'BuyerUserInfo', component: BuyerUserInfo, meta: { title: '个人信息', requiresAuth: true } },
  { path: '/buyer/login', name: 'BuyerLogin', component: BuyerLogin, meta: { title: '买家登录', requiresAuth: false } },
  { path: '/buyer/register', name: 'BuyerRegister', component: BuyerRegister, meta: { title: '买家注册', requiresAuth: false } },
  { path: '/buyer/address', name: 'BuyerAddress', component: BuyerAddress, meta: { title: '地址管理', requiresAuth: true } }
]