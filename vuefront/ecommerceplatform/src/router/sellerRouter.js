export const sellerRoutes = [
  {
    path: '/seller/login',
    name: 'SellerLogin',
    component: () => import('@/views/SellerLogin.vue')
  },
  {
    path: '/seller/register',
    name: 'SellerRegister',
    component: () => import('@/views/SellerRegister.vue')
  },
  {
    path: '/seller',
    component: () => import('@/views/SellerLayout.vue'),
    redirect: '/seller/product/list',
    children: [
      {
        path: 'product/list',
        name: 'SellerProductList',
        component: () => import('@/views/SellerProductList.vue')
      },
      {
        path: 'product/detail/:id',
        name: 'SellerProductDetail',
        component: () => import('@/views/SellerProductDetail.vue')
      },
      {
        path: 'order/list',        
        name: 'SellerOrderList',
        component: () => import('@/views/SellerOrderList.vue')
      },
      {
        path: 'product/add',
        name: 'SellerProductAdd',
        component: () => import('@/views/SellerProductForm.vue')
      },
      {
        path: 'product/edit/:id',
        name: 'SellerProductEdit',
        component: () => import('@/views/SellerProductForm.vue')
      },
      {
        path: 'profile',
        name: 'SellerProfile',
        component: () => import('@/views/SellerProfile.vue')
      }
    ]
  }
]