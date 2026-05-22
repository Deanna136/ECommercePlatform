<template>
  <div class="buyer-container">
    <div class="create-card">
      <div class="page-header">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2 class="buyer-title">确认订单</h2>
      </div>

      <!-- 收货地址选择 -->
      <div class="section-title">收货地址</div>
      <div class="address-selector" @click="showAddressDialog = true">
        <div v-if="selectedAddress" class="address-card">
          <div class="address-info">
            <span class="receiver">{{ selectedAddress.receiver }}</span>
            <span class="phone">{{ selectedAddress.phone }}</span>
          </div>
          <div class="address-detail">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.area }}
            {{ selectedAddress.detail }}
          </div>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div v-else class="address-placeholder">
          <el-icon class="placeholder-icon"><Location /></el-icon>
          <span>请选择收货地址</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="section-title">商品清单</div>
      <div class="seller-info" v-if="currentSeller">
        <span>卖家：{{ currentSeller.sellerName }} ({{ currentSeller.storeName }})</span>
      </div>
      <el-table :data="orderItems" border class="goods-table">
        <el-table-column label="商品图片" width="80">
          <template #default="{ row }">
            <el-image :src="row.image" fit="cover" style="width: 60px; height: 60px;" />
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="productName" />
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" width="100" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>

      <div class="total-row">
        <span>共 {{ totalQuantity }} 件商品，合计：</span>
        <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
      </div>

      <div class="action-bar">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" size="large" @click="submitOrder" :loading="submitting" :disabled="!selectedAddress">
          提交订单
        </el-button>
      </div>
    </div>

    <!-- 收货地址选择对话框 -->
    <el-dialog title="选择收货地址" v-model="showAddressDialog" width="500px">
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ active: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-info">
            <span class="receiver">{{ addr.receiver }}</span>
            <span class="phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="success" class="default-tag">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ addr.province }}{{ addr.city }}{{ addr.area }}{{ addr.detail }}
          </div>
        </div>
        <div v-if="addressList.length === 0" class="empty-address">
          <el-empty description="暂无收货地址" />
        </div>
      </div>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="goToAddAddress">
          <el-icon><Plus /></el-icon>
          新增地址
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Location, Plus } from '@element-plus/icons-vue'
import { orderApi } from '@/api/buyer-order'
import { cartApi } from '@/api/buyer-cart'

const router = useRouter()
const submitting = ref(false)
const showAddressDialog = ref(false)

const orderItems = ref([])
const currentSeller = ref(null)
const addressList = ref([])
const selectedAddress = ref(null)

const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
})
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const loadAddressList = async () => {
  try {
    const addresses = await cartApi.getAddressList()
    addressList.value = Array.isArray(addresses) ? addresses : []
    // 优先选中默认地址
    if (!selectedAddress.value) {
      const defaultAddr = addressList.value.find(addr => addr.isDefault)
      if (defaultAddr) {
        selectedAddress.value = defaultAddr
      } else if (addressList.value.length > 0) {
        selectedAddress.value = addressList.value[0]
      }
    }
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const selectAddress = (addr) => {
  selectedAddress.value = addr
  showAddressDialog.value = false
}

const goToAddAddress = () => {
  showAddressDialog.value = false
  router.push('/buyer/address')
}

const goBack = () => {
  // 如果是直接购买模式，返回首页；否则返回购物车
  const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
  if (isBuyNow) {
    router.push('/buyer/home')
  } else {
    router.push('/buyer/cart')
  }
}

const initOrderData = async () => {
  // 检查是否是直接购买模式
  const buyNowProduct = sessionStorage.getItem('buy_now_product')
  const route = useRoute()
  
  if (buyNowProduct && route.query.productId) {
    // 直接购买模式
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
      
      // 清除直接购买标记
      sessionStorage.removeItem('buy_now_product')
      return
    } catch (e) {
      console.error('解析直接购买数据失败', e)
    }
  }
  
  // 购物车结算模式
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
  if (!selectedAddress.value) {
    ElMessage.warning('请先选择收货地址')
    return
  }

  const orderData = {
    phone: selectedAddress.value.phone,
    address: `${selectedAddress.value.province}${selectedAddress.value.city}${selectedAddress.value.area}${selectedAddress.value.detail}`,
    orderItems: orderItems.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  }

  submitting.value = true
  try {
    const result = await orderApi.createOrder(orderData)
    ElMessage.success('订单创建成功')
    
    // 只有从购物车结算时才删除购物车中的对应商品（直接购买的商品ID以 'buy_now_' 开头）
    const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
    if (!isBuyNow) {
      const itemIds = orderItems.value.map(item => item.id)
      try {
        await cartApi.deleteItems(itemIds)
      } catch (err) {
        console.error('删除购物车商品失败', err)
      }
    }

    sessionStorage.removeItem('checkout_items')
    router.push(`/buyer/pay/result?orderId=${result.id}&amount=${result.amount}`)
  } catch (error) {
    console.error('下单失败', error)
    const errMsg = error?.message || (typeof error === 'string' ? error : '下单失败，请稍后重试')
    ElMessage.error(errMsg)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initOrderData()
  loadAddressList()
})
</script>

<style scoped>
.create-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.back-btn {
  padding: 8px 12px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 24px 0 12px;
}
.seller-info {
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 14px;
}
.goods-table {
  margin-bottom: 20px;
}
.total-row {
  text-align: right;
  font-size: 18px;
  margin: 16px 0;
}
.total-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 22px;
}
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.address-selector {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s;
}
.address-selector:hover {
  border-color: #409eff;
}
.address-card .address-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.receiver {
  font-weight: 600;
}
.phone {
  color: #666;
}
.address-detail {
  color: #666;
  font-size: 14px;
}
.address-placeholder {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  width: 100%;
  justify-content: space-between;
}
.placeholder-icon {
  font-size: 20px;
}
.arrow {
  color: #909399;
  font-size: 16px;
}
.address-list {
  max-height: 400px;
  overflow-y: auto;
}
.address-item {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}
.address-item:hover {
  border-color: #409eff;
}
.address-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}
.address-item .address-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.default-tag {
  margin-left: auto;
}
.empty-address {
  padding: 40px 0;
}
</style>
