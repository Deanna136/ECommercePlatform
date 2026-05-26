import sellerRequest from '@/utils/sellerRequest'

// 获取卖家订单列表
export const sellerGetOrderListApi = () => {
  return sellerRequest({
    url: '/seller/order/list',
    method: 'GET'
  })
}

// 条件查询订单
export const sellerQueryOrderApi = (params) => {
  return sellerRequest({
    url: '/seller/order/query',
    method: 'GET',
    params
  })
}

// 获取订单详情
export const sellerGetOrderDetailApi = (id) => {
  return sellerRequest({
    url: `/seller/order/${id}`,
    method: 'GET'
  })
}

// 修改订单信息
export const sellerUpdateOrderInfoApi = (id, data) => {
  return sellerRequest({
    url: `/seller/order/${id}/updateInfo`,
    method: 'PUT',
    data
  })
}

// 修改订单状态
export const sellerUpdateOrderStatusApi = (id, data) => {
  return sellerRequest({
    url: `/seller/order/${id}/updateStatus`,
    method: 'PUT',
    data
  })
}

// 异常申报
export const sellerReportAbnormalApi = (id, data) => {
  return sellerRequest({
    url: `/seller/order/${id}/reportAbnormal`,
    method: 'PUT',
    data
  })
}