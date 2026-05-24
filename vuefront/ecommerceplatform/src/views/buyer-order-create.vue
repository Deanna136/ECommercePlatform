<template>
  <div class="buyer-container">
    <div class="create-card buyer-card">
      <div class="page-header">
        <el-button @click="goBack" class="back-btn buyer-btn-light">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2 class="buyer-title">确认订单</h2>
      </div>

      <!-- 收货地址 -->
      <div class="section-title">收货地址</div>
      <el-form :model="addressForm" class="address-form buyer-card">
        <div class="address-row">
          <el-form-item label="收货人" prop="receiver" class="flex-1">
            <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" class="buyer-input" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone" class="flex-1">
            <el-input v-model="addressForm.phone" placeholder="请输入联系电话" class="buyer-input" />
          </el-form-item>
        </div>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="addressForm.address" placeholder="请输入详细收货地址" class="buyer-input" />
        </el-form-item>
      </el-form>

      <!-- 商品清单 -->
      <div class="section-title">商品清单</div>
      <div class="seller-info" v-if="currentSeller">
        <span>卖家：{{ currentSeller.sellerName }} ({{ currentSeller.storeName }})</span>
      </div>
      <div class="buyer-table">
        <el-table :data="orderItems" border class="goods-table">
          <el-table-column label="商品图片" width="80">
            <template #default="{ row }">
              <el-image :src="row.image" fit="cover" class="goods-thumb" />
            </template>
          </el-table-column>
          <el-table-column label="商品名称" prop="productName" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }"><span class="buyer-price">¥{{ row.price }}</span></template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }"><span class="buyer-price">¥{{ (row.price * row.quantity).toFixed(2) }}</span></template>
          </el-table-column>
        </el-table>
      </div>

      <div class="total-row">
        <span>共 {{ totalQuantity }} 件商品，合计：</span>
        <span class="buyer-price-large">¥{{ totalAmount.toFixed(2) }}</span>
      </div>

      <div class="action-bar">
        <el-button class="buyer-btn-secondary" @click="goBack">取消</el-button>
        <el-button class="buyer-btn-primary" @click="submitOrder" :loading="submitting" :disabled="!canSubmit">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api/buyer-order'
import { userApi } from '@/api/buyer-user'
import { cartApi } from '@/api/buyer-cart'

const router = useRouter()
const submitting = ref(false)

const orderItems = ref([])
const currentSeller = ref(null)
const addressForm = reactive({
  receiver: '',
  phone: '',
  address: ''
})

// 从用户信息中加载默认收货地址
const loadDefaultAddress = () => {
  const user = userApi.getLocalUserInfo()
  if (user) {
    addressForm.receiver = user.name || user.userName || ''
    addressForm.phone = user.phone || ''
    addressForm.address = user.address || ''
  }
}

const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
})
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const canSubmit = computed(() => {
  return addressForm.receiver.trim() && addressForm.phone.trim() && addressForm.address.trim()
})

const goBack = () => {
  const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
  if (isBuyNow) {
    router.push('/buyer/home')
  } else {
    router.push('/buyer/cart')
  }
}

const initOrderData = async () => {
  const buyNowProduct = sessionStorage.getItem('buy_now_product')
  const route = useRoute()
  
  if (buyNowProduct && route.query.productId) {
    try {
      const productInfo = JSON.parse(buyNowProduct)
      const quantity = parseInt(route.query.quantity) || 1
      
      orderItems.value = [{
        id: 'buy_now_' + Date.now(),
        productId: parseInt(route.query.productId),
        productName: productInfo.name,
        productNo: productInfo.productNo || '',
        price: productInfo.price,
        image: productInfo.image,
        quantity: quantity,
        sellerId: productInfo.sellerId,
        sellerName: productInfo.sellerName,
        storeName: productInfo.storeName
      }]
      
      currentSeller.value = {
        sellerId: productInfo.sellerId,
        sellerName: productInfo.sellerName,
        storeName: productInfo.storeName
      }
      
      sessionStorage.removeItem('buy_now_product')
      return
    } catch (e) {
      console.error('解析直接购买数据失败', e)
    }
  }
  
  const checkoutData = sessionStorage.getItem('checkout_items')
  if (!checkoutData) {
    ElMessage.error('请从购物车选择商品')
    router.push('/buyer/cart')
    return
  }
  try {
    const selected = JSON.parse(checkoutData)
    if (!selected || selected.length === 0) {
      ElMessage.error('未选择任何商品')
      router.push('/buyer/cart')
      return
    }
    orderItems.value = selected
    const first = selected[0]
    const isSameSeller = selected.every(item => item.sellerId === first.sellerId)
    if (!isSameSeller) {
      ElMessage.error('一个订单只能包含同一卖家的商品，请分开下单')
      router.push('/buyer/cart')
      return
    }
    currentSeller.value = {
      sellerId: first.sellerId,
      sellerName: first.sellerName,
      storeName: first.storeName
    }
  } catch (e) {
    console.error('解析购物车数据失败', e)
    ElMessage.error('商品数据异常，请重新选择')
    router.push('/buyer/cart')
  }
}

const submitOrder = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }

  const orderData = {
    phone: addressForm.phone,
    address: addressForm.address,
    orderItems: orderItems.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  }

  submitting.value = true
  try {
    const result = await orderApi.createOrder(orderData)
    ElMessage.success('订单创建成功')

    const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
    if (!isBuyNow) {
      for (const item of orderItems.value) {
        try {
          await cartApi.removeCartItem(item.id)
        } catch (err) {
          console.error('删除购物车商品失败', err)
        }
      }
    }

    sessionStorage.removeItem('checkout_items')
    router.push(`/buyer/pay/result?orderId=${result.id}&amount=${result.amount}`)
  } catch (error) {
    console.error('下单失败', error)
    const errMsg = error?.message || (typeof error === 'string' ? error : '下单失败，请稍后重试')
    
    // 根据错误信息提供更友好的提示
    let friendlyMsg = errMsg
    if (errMsg === '参数校验失败' || errMsg === '参数错误') {
      // 检查是否是不同商家的商品
      const storeNames = orderItems.value.map(item => item.storeName)
      const uniqueStores = [...new Set(storeNames)]
      if (uniqueStores.length > 1) {
        friendlyMsg = '不同商家的商品无法合并下单，请分别下单'
      } else if (!addressForm.phone || !addressForm.address) {
        friendlyMsg = '请填写完整的收货信息（手机号和收货地址）'
      } else if (!addressForm.phone.match(/^1[3-9]\d{9}$/)) {
        friendlyMsg = '手机号格式不正确，请重新输入'
      } else {
        friendlyMsg = '下单失败，商品或数量可能存在问题，请检查后重试'
      }
    }
    ElMessage.error(friendlyMsg)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initOrderData()
  loadDefaultAddress()
})
</script>

<style scoped>
.create-card {
  padding: var(--buyer-spacing-lg);
}
.page-header {
  display: flex;
  align-items: center;
  gap: var(--buyer-spacing-sm);
  margin-bottom: var(--buyer-spacing-lg);
}
.back-btn {
  padding: 8px 16px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--buyer-text-primary);
  margin: var(--buyer-spacing-lg) 0 var(--buyer-spacing-sm);
}
.seller-info {
  background: var(--buyer-bg-input);
  padding: var(--buyer-spacing-sm);
  border-radius: var(--buyer-border-radius-sm);
  margin-bottom: var(--buyer-spacing-sm);
  font-size: var(--buyer-font-main);
  color: var(--buyer-text-regular);
}
.goods-table {
  margin-bottom: var(--buyer-spacing-md);
}
.goods-thumb {
  width: 50px;
  height: 50px;
  border-radius: var(--buyer-border-radius-sm);
  object-fit: cover;
}
.total-row {
  text-align: right;
  font-size: var(--buyer-font-main);
  color: var(--buyer-text-regular);
  margin: var(--buyer-spacing-md) 0;
}
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: var(--buyer-spacing-md);
  margin-top: var(--buyer-spacing-lg);
  padding-top: var(--buyer-spacing-md);
  border-top: 1px solid var(--buyer-border-color);
}
.address-form {
  padding: var(--buyer-spacing-md);
  border: none;
  box-shadow: none;
}
.address-row {
  display: flex;
  gap: var(--buyer-spacing-md);
}
.flex-1 {
  flex: 1;
}

@media (max-width: 768px) {
  .address-row {
    flex-wrap: wrap;
  }
  .flex-1 {
    width: 100%;
  }
}
</style>
