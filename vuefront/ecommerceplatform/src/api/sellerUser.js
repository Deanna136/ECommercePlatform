import request from '@/utils/sellerRequest'

export const sellerLoginApi = (data) => {
  return request({
    url: '/seller/login',
    method: 'post',
    data
  })
}

export const sellerRegisterApi = (data) => {
  return request({
    url: '/seller/register',
    method: 'post',
    data
  })
}

export const sellerUpdateInfoApi = (data) => {
  return request({
    url: '/seller/updateInfo',
    method: 'put',
    data
  })
}