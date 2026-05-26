import { defineStore } from 'pinia'

// 安全解析JSON，防止localStorage被篡改导致崩溃
const safeParseJSON = (str, defaultValue) => {
  try {
    return str ? JSON.parse(str) : defaultValue
  } catch (e) {
    console.error('localStorage解析失败:', e)
    return defaultValue
  }
}

export const useSellerUserStore = defineStore('sellerUser', {
  state: () => ({
    sellerToken: localStorage.getItem('seller_token') || '',
    sellerInfo: safeParseJSON(localStorage.getItem('seller_info'), {})
  }),
  
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.sellerToken,
    // 店铺名称（带默认值）
    storeName: (state) => state.sellerInfo?.storeName || '未命名店铺',
    // 用户名称（带默认值）
    userName: (state) => state.sellerInfo?.name || '未知用户',
    // 电话
    phone: (state) => state.sellerInfo?.phone || '',
    // 地址
    address: (state) => state.sellerInfo?.address || '',
    // 真实姓名
    name: (state) => state.sellerInfo?.name || '',
    // 身份证号
    idNumber: (state) => state.sellerInfo?.idNumber || '',
    // 性别
    sex: (state) => state.sellerInfo?.sex || '',
    // 头像
    image: (state) => state.sellerInfo?.image || '',
    // 店铺类型
    storeCategory: (state) => state.sellerInfo?.storeCategory || ''
  },
  
  actions: {
    sellerLogin(data) {
      // 验证必要字段
      if (!data || !data.token) {
        throw new Error('登录响应数据异常')
      }
      
      this.sellerToken = data.token
      this.sellerInfo = {
        id: data.id || null,
        userName: data.userName || '',
        name: data.name || '',
        storeName: data.storeName || '',
        storeCategory: data.storeCategory || '',
        status: data.status || 'active',
        phone: data.phone || '',
        address: data.address || '',
        idNumber: data.idNumber || '',
        sex: data.sex || '',
        image: data.image || ''
      }
      
      localStorage.setItem('seller_token', data.token)
      localStorage.setItem('seller_info', JSON.stringify(this.sellerInfo))
    },
    
    sellerLogout() {
      this.sellerToken = ''
      this.sellerInfo = {}
      localStorage.removeItem('seller_token')
      localStorage.removeItem('seller_info')
    },
    
    // 更新本地信息（只更新有值的字段）
    updateSellerInfo(info) {
      if (info) {
        // 过滤掉空字段，只更新有实际值的字段
        const filteredInfo = Object.keys(info).reduce((acc, key) => {
          const value = info[key]
          if (value !== '' && value !== null && value !== undefined) {
            acc[key] = value
          }
          return acc
        }, {})
        this.sellerInfo = { ...this.sellerInfo, ...filteredInfo }
        localStorage.setItem('seller_info', JSON.stringify(this.sellerInfo))
      }
    }
  }
})