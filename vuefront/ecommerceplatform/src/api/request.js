// ==================== Mock 数据配置 ====================
let mockToken = null
let mockUser = {
  id: 1,
  userName: 'testbuyer',
  name: '测试买家',
  phone: '13800138000',
  address: '北京市朝阳区建国路88号',
  image: '',
  status: 'active'
}

// 商品列表
const mockProductList = [
  { id: 1, productNo: 'P001', name: '纯棉T恤', category: 'clothing', sku: 98, price: 59.90, image: 'https://picsum.photos/200/200?random=1', description: '夏季透气纯棉T恤', sellerId: 1, sellerName: '甲商家', storeName: '潮流服饰店', status: 'onsale', salesCount: 120, createTime: '2025-01-01 10:00:00', updateTime: '2025-01-01 10:00:00' },
  { id: 2, productNo: 'P002', name: '无线蓝牙耳机', category: 'electronics', sku: 56, price: 199.00, image: 'https://picsum.photos/200/200?random=2', description: '降噪长续航蓝牙耳机', sellerId: 2, sellerName: '乙商家', storeName: '数码生活馆', status: 'onsale', salesCount: 340, createTime: '2025-01-02 10:00:00', updateTime: '2025-01-02 10:00:00' },
  { id: 3, productNo: 'P003', name: '运动跑鞋', category: 'sports', sku: 120, price: 299.00, image: 'https://picsum.photos/200/200?random=3', description: '轻便缓震运动跑鞋', sellerId: 3, sellerName: '丙商家', storeName: '运动户外', status: 'onsale', salesCount: 89, createTime: '2025-01-03 10:00:00', updateTime: '2025-01-03 10:00:00' },
  { id: 4, productNo: 'P004', name: '不锈钢锅具套装', category: 'home_living', sku: 30, price: 399.00, image: 'https://picsum.photos/200/200?random=4', description: '厨房必备锅具套装', sellerId: 4, sellerName: '丁商家', storeName: '家居好物', status: 'onsale', salesCount: 45, createTime: '2025-01-04 10:00:00', updateTime: '2025-01-04 10:00:00' },
  { id: 5, productNo: 'P005', name: '婴儿奶瓶', category: 'mother_baby', sku: 50, price: 89.00, image: 'https://picsum.photos/200/200?random=5', description: '安全材质婴儿奶瓶', sellerId: 5, sellerName: '戊商家', storeName: '母婴专营店', status: 'onsale', salesCount: 200, createTime: '2025-01-05 10:00:00', updateTime: '2025-01-05 10:00:00' },
  { id: 6, productNo: 'P006', name: '零食大礼包', category: 'food', sku: 200, price: 68.00, image: 'https://picsum.photos/200/200?random=6', description: '多种口味零食组合', sellerId: 6, sellerName: '己商家', storeName: '零食铺子', status: 'onsale', salesCount: 500, createTime: '2025-01-06 10:00:00', updateTime: '2025-01-06 10:00:00' },
  { id: 7, productNo: 'P007', name: 'Python编程书', category: 'books', sku: 80, price: 79.00, image: 'https://picsum.photos/200/200?random=7', description: 'Python从入门到精通', sellerId: 7, sellerName: '庚商家', storeName: '书店天地', status: 'onsale', salesCount: 150, createTime: '2025-01-07 10:00:00', updateTime: '2025-01-07 10:00:00' },
  { id: 8, productNo: 'P008', name: '时尚背包', category: 'others', sku: 60, price: 159.00, image: 'https://picsum.photos/200/200?random=8', description: '大容量时尚背包', sellerId: 8, sellerName: '辛商家', storeName: '箱包店', status: 'onsale', salesCount: 95, createTime: '2025-01-08 10:00:00', updateTime: '2025-01-08 10:00:00' }
]

// 购物车数据
let mockCart = {
  cartId: 100,
  totalCount: 3,
  totalPrice: 358.80,
  cartItems: [
    { id: 101, productId: 1, productName: '纯棉T恤', productNo: 'P001', category: 'clothing', price: 59.90, image: 'https://picsum.photos/200/200?random=1', storeName: '潮流服饰店', sellerId: 1, sellerName: '甲商家', quantity: 2, subTotal: 119.80, productStatus: 'onsale', sku: 98 },
    { id: 102, productId: 2, productName: '无线蓝牙耳机', productNo: 'P002', category: 'electronics', price: 199.00, image: 'https://picsum.photos/200/200?random=2', storeName: '数码生活馆', sellerId: 2, sellerName: '乙商家', quantity: 1, subTotal: 199.00, productStatus: 'onsale', sku: 56 },
    { id: 103, productId: 3, productName: '运动跑鞋', productNo: 'P003', category: 'sports', price: 299.00, image: 'https://picsum.photos/200/200?random=3', storeName: '运动户外', sellerId: 3, sellerName: '丙商家', quantity: 1, subTotal: 299.00, productStatus: 'onsale', sku: 120 }
  ]
}

// 订单列表
let mockOrders = [
  {
    id: 1001,
    orderNo: 'ORD20260001',
    sellerId: 1,
    sellerName: '甲商家',
    storeName: '潮流服饰店',
    amount: 119.80,
    status: 'paid',
    createTime: '2026-05-20 10:00:00',
    updateTime: '2026-05-20 10:00:00',
    phone: '13800138000',
    address: '北京市朝阳区建国路88号',
    orderItems: [
      { id: 10001, productId: 1, productName: '纯棉T恤', productNo: 'P001', quantity: 2, unitPrice: 59.90, totalPrice: 119.80, image: 'https://picsum.photos/200/200?random=1' }
    ]
  },
  {
    id: 1002,
    orderNo: 'ORD20260002',
    sellerId: 2,
    sellerName: '乙商家',
    storeName: '数码生活馆',
    amount: 199.00,
    status: 'shipped',
    createTime: '2026-05-19 15:30:00',
    updateTime: '2026-05-20 09:00:00',
    phone: '13800138000',
    address: '北京市朝阳区建国路88号',
    orderItems: [
      { id: 10002, productId: 2, productName: '无线蓝牙耳机', productNo: 'P002', quantity: 1, unitPrice: 199.00, totalPrice: 199.00, image: 'https://picsum.photos/200/200?random=2' }
    ]
  },
  {
    id: 1003,
    orderNo: 'ORD20260003',
    sellerId: 3,
    sellerName: '丙商家',
    storeName: '运动户外',
    amount: 299.00,
    status: 'completed',
    createTime: '2026-05-18 08:00:00',
    updateTime: '2026-05-19 18:00:00',
    phone: '13800138000',
    address: '北京市朝阳区建国路88号',
    orderItems: [
      { id: 10003, productId: 3, productName: '运动跑鞋', productNo: 'P003', quantity: 1, unitPrice: 299.00, totalPrice: 299.00, image: 'https://picsum.photos/200/200?random=3' }
    ]
  }
]

// 地址列表
let mockAddresses = [
  { id: 1, receiver: '张三', phone: '13800138000', province: '北京市', city: '北京市', area: '朝阳区', detail: '建国路88号', isDefault: true },
  { id: 2, receiver: '李四', phone: '13900139000', province: '上海市', city: '上海市', area: '浦东新区', detail: '世纪大道100号', isDefault: false }
]

// 辅助函数
const delay = (ms = 200) => new Promise(resolve => setTimeout(resolve, ms))

// Mock 请求处理 - 返回数据或抛出错误
async function mockRequest(config) {
  const { url, method, data, params } = config
  console.log('[Mock]', method, url, data || params)

  // 登录
  if (url === '/buyer/login' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    if (body.userName && body.password) {
      mockToken = 'mock_token_' + Date.now()
      mockUser = { ...mockUser, userName: body.userName }
      return { ...mockUser, token: mockToken }
    }
    throw new Error('用户不存在')
  }

  // 注册
  if (url === '/buyer/register' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    if (!body.userName || !body.password || body.password.length < 6) {
      throw new Error('密码长度不能少于6位')
    }
    return '注册成功'
  }

  // 修改个人信息
  if (url === '/buyer/updateInfo' && method === 'put') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    if (mockUser) Object.assign(mockUser, body)
    return '个人信息更新成功'
  }

  // 获取用户信息
  if (url === '/buyer/user/info' && method === 'get') {
    if (mockUser) return mockUser
    throw new Error('用户未登录')
  }

  // 商品列表
  if (url === '/buyer/product/list' && method === 'get') {
    await delay()
    return mockProductList
  }

  // 商品详情
  if (url.match(/\/buyer\/product\/\d+$/) && method === 'get') {
    const id = parseInt(url.split('/').pop())
    const product = mockProductList.find(p => p.id === id)
    if (product) {
      return { ...product }
    }
    throw new Error('商品不存在')
  }

  // 商品筛选
  if (url === '/buyer/product/query' && method === 'get') {
    await delay()
    let filtered = [...mockProductList]
    if (params) {
      if (params.name) filtered = filtered.filter(p => p.name.includes(params.name))
      if (params.category) filtered = filtered.filter(p => p.category === params.category)
      if (params.minPrice) filtered = filtered.filter(p => p.price >= parseFloat(params.minPrice))
      if (params.maxPrice) filtered = filtered.filter(p => p.price <= parseFloat(params.maxPrice))
    }
    return filtered
  }

  // 首页推荐商品（也支持 goods 路径）
  if (url === '/buyer/goods/recommend' && method === 'get') {
    await delay()
    return mockProductList.slice(0, 4)
  }

  // 商品详情（product 路径）
  if (url.match(/\/buyer\/product\/\d+$/) && method === 'get') {
    const id = parseInt(url.split('/').pop())
    const product = mockProductList.find(p => p.id === id)
    if (product) {
      return { ...product }
    }
    throw new Error('商品不存在')
  }

  // 商品详情（goods 路径）
  if (url.match(/\/buyer\/goods\/detail\/\d+$/) && method === 'get') {
    const id = parseInt(url.split('/').pop())
    const product = mockProductList.find(p => p.id === id)
    if (product) {
      return { ...product }
    }
    throw new Error('商品不存在')
  }

  // 商品收藏
  if (url === '/buyer/goods/collect' && method === 'post') {
    return '收藏成功'
  }

  // 取消收藏
  if (url === '/buyer/goods/cancelCollect' && method === 'delete') {
    return '取消收藏成功'
  }

  // 获取收藏列表
  if (url === '/buyer/goods/collectList' && method === 'get') {
    return mockProductList.slice(0, 2)
  }

  // 购物车查询
  if (url === '/buyer/cart' && method === 'get') {
    const stored = localStorage.getItem('mock_cart')
    if (stored) mockCart = JSON.parse(stored)
    return mockCart
  }

  // 购物车列表
  if (url === '/buyer/cart/list' && method === 'get') {
    const stored = localStorage.getItem('mock_cart')
    if (stored) mockCart = JSON.parse(stored)
    return mockCart.cartItems
  }

  // 加入购物车
  if (url === '/buyer/cart/add' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const { goodsId, productId, num, quantity } = body
    const productIdToUse = goodsId || productId
    const numToUse = num || quantity
    const product = mockProductList.find(p => p.id === productIdToUse)
    if (!product) throw new Error('商品不存在')
    if (product.sku < numToUse) throw new Error('库存不足')

    const existing = mockCart.cartItems.find(item => item.productId === productIdToUse)
    if (existing) {
      existing.quantity += numToUse
      existing.subTotal = existing.price * existing.quantity
    } else {
      mockCart.cartItems.push({
        id: Date.now(),
        productId: product.id,
        productName: product.name,
        productNo: product.productNo,
        category: product.category,
        price: product.price,
        image: product.image,
        storeName: product.storeName,
        sellerId: product.sellerId,
        sellerName: product.sellerName || product.storeName + '店主',
        quantity: numToUse,
        subTotal: product.price * numToUse,
        productStatus: 'onsale',
        sku: product.sku
      })
    }
    mockCart.totalCount = mockCart.cartItems.reduce((sum, i) => sum + i.quantity, 0)
    mockCart.totalPrice = mockCart.cartItems.reduce((sum, i) => sum + i.subTotal, 0)
    localStorage.setItem('mock_cart', JSON.stringify(mockCart))
    return '已加入购物车'
  }

  // 修改购物车数量
  if (url === '/buyer/cart/update' && method === 'put') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const { cartItemId, num } = body
    const item = mockCart.cartItems.find(i => i.id === cartItemId)
    if (!item) throw new Error('购物车商品不存在')
    item.quantity = num
    item.subTotal = item.price * num
    mockCart.totalCount = mockCart.cartItems.reduce((sum, i) => sum + i.quantity, 0)
    mockCart.totalPrice = mockCart.cartItems.reduce((sum, i) => sum + i.subTotal, 0)
    localStorage.setItem('mock_cart', JSON.stringify(mockCart))
    return '购物车已更新'
  }

  // 更新购物车数量（另一种格式）
  if (url === '/buyer/cart/updateNum' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const { id, num } = body
    const item = mockCart.cartItems.find(i => i.id === id)
    if (!item) throw new Error('购物车商品不存在')
    item.quantity = num
    item.subTotal = item.price * num
    mockCart.totalCount = mockCart.cartItems.reduce((sum, i) => sum + i.quantity, 0)
    mockCart.totalPrice = mockCart.cartItems.reduce((sum, i) => sum + i.subTotal, 0)
    localStorage.setItem('mock_cart', JSON.stringify(mockCart))
    return '修改成功'
  }

  // 移出购物车
  if (url.match(/\/buyer\/cart\/remove\/\d+$/) && method === 'delete') {
    const cartItemId = parseInt(url.split('/').pop())
    mockCart.cartItems = mockCart.cartItems.filter(i => i.id !== cartItemId)
    mockCart.totalCount = mockCart.cartItems.reduce((sum, i) => sum + i.quantity, 0)
    mockCart.totalPrice = mockCart.cartItems.reduce((sum, i) => sum + i.subTotal, 0)
    localStorage.setItem('mock_cart', JSON.stringify(mockCart))
    return '商品已从购物车移出'
  }

  // 删除购物车项
  if (url === '/buyer/cart/delete' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const ids = body.ids || []
    mockCart.cartItems = mockCart.cartItems.filter(i => !ids.includes(i.id))
    mockCart.totalCount = mockCart.cartItems.reduce((sum, i) => sum + i.quantity, 0)
    mockCart.totalPrice = mockCart.cartItems.reduce((sum, i) => sum + i.subTotal, 0)
    localStorage.setItem('mock_cart', JSON.stringify(mockCart))
    return '删除成功'
  }

  // 获取购物车列表（另一种路径）
  if (url === '/buyer/cart/getList' && method === 'get') {
    const stored = localStorage.getItem('mock_cart')
    if (stored) mockCart = JSON.parse(stored)
    return mockCart
  }

  // 订单列表
  if (url === '/buyer/order/list' && method === 'get') {
    return mockOrders
  }

  // 订单查询
  if (url === '/buyer/order/query' && method === 'get') {
    return mockOrders
  }

  // 订单详情
  if (url.match(/\/buyer\/order\/\d+$/) && method === 'get') {
    const id = parseInt(url.split('/').pop())
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    return order
  }

  // 创建订单
  if (url === '/buyer/order/create' && method === 'post') {
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const { items } = body
    const newOrder = {
      id: Date.now(),
      orderNo: 'ORD' + Date.now(),
      sellerId: 1,
      sellerName: '甲商家',
      storeName: '潮流服饰店',
      amount: items ? items.reduce((sum, item) => sum + (item.price || 59.90) * (item.quantity || 1), 0) : 59.90,
      status: 'paid',
      createTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
      updateTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
      phone: '13800138000',
      address: '北京市朝阳区建国路88号',
      orderItems: items || []
    }
    mockOrders.unshift(newOrder)
    return { id: newOrder.id, orderNo: newOrder.orderNo, amount: newOrder.amount, status: 'paid', createTime: newOrder.createTime }
  }

  // 退单
  if (url.match(/\/buyer\/order\/\d+\/refund$/) && method === 'put') {
    const orderId = parseInt(url.split('/')[3])
    const order = mockOrders.find(o => o.id === orderId)
    if (order && order.status === 'paid') {
      order.status = 'abnormal'
      return '退单申请已提交，等待卖家审核'
    }
    throw new Error('订单状态不允许此操作')
  }

  // 修改订单信息
  if (url.match(/\/buyer\/order\/\d+\/updateInfo$/) && method === 'put') {
    const orderId = parseInt(url.split('/')[3])
    const body = typeof data === 'string' ? JSON.parse(data) : data
    const order = mockOrders.find(o => o.id === orderId)
    if (order) {
      if (body.phone) order.phone = body.phone
      if (body.address) order.address = body.address
      order.updateTime = new Date().toISOString().replace('T', ' ').slice(0, 19)
      return '订单信息更新成功'
    }
    throw new Error('订单不存在')
  }

  // 异常申报
  if (url.match(/\/buyer\/order\/\d+\/reportAbnormal$/) && method === 'put') {
    const orderId = parseInt(url.split('/')[3])
    const order = mockOrders.find(o => o.id === orderId)
    if (order && !['completed', 'cancelled', 'refunded', 'failed'].includes(order.status)) {
      order.status = 'abnormal'
      return '订单异常已申报，请等待管理员处理'
    }
    throw new Error('订单状态不允许此操作')
  }

  // 地址列表
  if (url === '/buyer/address/list' && method === 'get') {
    let addresses = JSON.parse(localStorage.getItem('mock_addresses') || '[]')
    if (addresses.length === 0) {
      addresses = [...mockAddresses]
      localStorage.setItem('mock_addresses', JSON.stringify(addresses))
    }
    return addresses
  }

  // 添加地址
  if (url === '/buyer/address/add' && method === 'post') {
    const addr = typeof data === 'string' ? JSON.parse(data) : data
    addr.id = Date.now()
    let addresses = JSON.parse(localStorage.getItem('mock_addresses') || '[]')
    if (addr.isDefault) addresses.forEach(a => a.isDefault = false)
    addresses.push(addr)
    localStorage.setItem('mock_addresses', JSON.stringify(addresses))
    return '添加成功'
  }

  // 更新地址
  if (url === '/buyer/address/update' && method === 'put') {
    const newAddr = typeof data === 'string' ? JSON.parse(data) : data
    let addresses = JSON.parse(localStorage.getItem('mock_addresses') || '[]')
    const index = addresses.findIndex(a => a.id === newAddr.id)
    if (index !== -1) {
      if (newAddr.isDefault) addresses.forEach(a => a.isDefault = false)
      addresses[index] = newAddr
      localStorage.setItem('mock_addresses', JSON.stringify(addresses))
      return '更新成功'
    }
    throw new Error('地址不存在')
  }

  // 删除地址
  if (url.match(/\/buyer\/address\/delete\/\d+$/) && method === 'delete') {
    const id = parseInt(url.split('/').pop())
    let addresses = JSON.parse(localStorage.getItem('mock_addresses') || '[]')
    addresses = addresses.filter(a => a.id !== id)
    localStorage.setItem('mock_addresses', JSON.stringify(addresses))
    return '删除成功'
  }

  // 设为默认地址
  if (url.match(/\/buyer\/address\/default\/\d+$/) && method === 'put') {
    const id = parseInt(url.split('/').pop())
    let addresses = JSON.parse(localStorage.getItem('mock_addresses') || '[]')
    addresses.forEach(a => a.isDefault = (a.id === id))
    localStorage.setItem('mock_addresses', JSON.stringify(addresses))
    return '设置成功'
  }

  // 支付状态查询
  if (url.match(/\/buyer\/order\/\d+$/) && method === 'get') {
    const id = parseInt(url.split('/').pop())
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    return order
  }

  // 未匹配的请求
  throw new Error('接口不存在')
}

// 创建一个简单的请求对象
const request = {
  get: async function (url, params) {
    try {
      return await mockRequest({ url, method: 'get', params })
    } catch (error) {
      throw error
    }
  },

  post: async function (url, data) {
    try {
      return await mockRequest({ url, method: 'post', data })
    } catch (error) {
      throw error
    }
  },

  put: async function (url, data) {
    try {
      return await mockRequest({ url, method: 'put', data })
    } catch (error) {
      throw error
    }
  },

  delete: async function (url, data) {
    try {
      return await mockRequest({ url, method: 'delete', data })
    } catch (error) {
      throw error
    }
  }
}

// 让 request 可以直接作为函数调用
const requestFn = async function (config) {
  return await mockRequest(config)
}

// 复制所有 HTTP 方法到函数上
requestFn.get = request.get
requestFn.post = request.post
requestFn.put = request.put
requestFn.delete = request.delete

export default requestFn
