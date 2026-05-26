import sellerRequest from '@/utils/sellerRequest'

// 获取商品列表
export const sellerGetProductListApi = () => {
  return sellerRequest({
    url: '/seller/product/list',
    method: 'GET'
  })
}

// 获取商品详情
export const sellerGetProductDetailApi = (id) => {
  return sellerRequest({
    url: `/seller/product/${id}`,
    method: 'GET'
  })
}

// 新增商品
export const sellerAddProductApi = (data) => {
  return sellerRequest({
    url: '/seller/product/add',
    method: 'POST',
    data
  })
}

// 修改商品
export const sellerUpdateProductApi = (id, data) => {
  return sellerRequest({
    url: `/seller/product/${id}`,
    method: 'PUT',
    data
  })
}

// 停售商品
export const sellerStopSaleApi = (id) => {
  return sellerRequest({
    url: `/seller/product/${id}/stopSale`,
    method: 'PUT'
  })
}

// 条件查询商品
export const sellerQueryProductApi = (params) => {
  return sellerRequest({
    url: '/seller/product/query',
    method: 'GET',
    params
  })
}