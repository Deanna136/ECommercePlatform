// 卖家端常量配置（根据接口文档 v1.0）

// 商品上下架状态（卖家端实际只用到了 offsale 和 onsale，但文档还有其他状态）
export const PRODUCT_STATUS = [
  { label: '在售', value: 'onsale' },
  { label: '已停售', value: 'offsale' },
  { label: '待审核', value: 'pending_review' },
  { label: '审核通过', value: 'approved' },
  { label: '审核拒绝', value: 'rejected' },
  { label: '缺货', value: 'out_of_stock' },
  { label: '强制下架', value: 'suspend' }
]

// 订单状态（完全按照文档）
export const ORDER_STATUS = [
  { label: '待付款', value: 'pending' },
  { label: '已付款', value: 'paid' },
  { label: '处理中', value: 'processing' },
  { label: '已发货', value: 'shipped' },
  { label: '已送达', value: 'delivered' },
  { label: '交易完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
  { label: '已退款', value: 'refunded' },
  { label: '交易失败', value: 'failed' },
  { label: '异常', value: 'abnormal' }
]

// 店铺/商品类型
export const CATEGORY_OPTIONS = [
  { label: '服饰', value: 'clothing' },
  { label: '食品', value: 'food' },
  { label: '数码电子', value: 'electronics' },
  { label: '家居生活', value: 'home_living' },
  { label: '母婴', value: 'mother_baby' },
  { label: '运动', value: 'sports' },
  { label: '图书', value: 'books' },
  { label: '其他', value: 'others' }
]

// 卖家账号状态
export const SELLER_STATUS = [
  { label: '正常', value: 'active' },
  { label: '已封禁', value: 'locked' },
  { label: '已删除', value: 'deleted' }
]

// 性别
export const SEX_OPTIONS = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' }
]

export const DEFAULT_PAGE_SIZE = 10
export const BASE_URL = '/seller'