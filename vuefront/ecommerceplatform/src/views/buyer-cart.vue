<template>
  <div class="buyer-container">
    <div class="page-header">
      <el-button @click="$router.push('/buyer/home')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="buyer-title">我的购物车</h2>
    </div>

    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车还是空的~ 快去逛逛吧" />
      <el-button type="primary" @click="$router.push('/buyer/home')">去购物</el-button>
    </div>
    <div v-else class="cart-wrapper">
      <el-table :data="cartItems" border @selection-change="handleSelectionChange" ref="tableRef">
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="goods-info">
              <el-image :src="row.image" fit="cover" class="goods-img" />
              <div class="goods-detail">
                <div class="goods-name">{{ row.productName }}</div>
                <div class="goods-sku">编号：{{ row.productNo }}</div>
                <div class="goods-status" :class="row.productStatus !== 'onsale' ? 'error' : ''">
                  {{ productStatusMap[row.productStatus] || '正常' }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            <span class="buyer-price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="row.sku"
              size="small"
              @change="(val) => updateQuantity(row, val)"
              :disabled="row.productStatus !== 'onsale'"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="buyer-price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="left-actions">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          <el-button link type="danger" @click="batchRemove" :disabled="selectedIds.length === 0">
            批量删除
          </el-button>
        </div>
        <div class="right-total">
          <div class="total-amount">
            <span>合计（不含运费）：</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="checkout" :disabled="selectedItems.length === 0">
            去结算 ({{ selectedItems.length }})
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { cartApi } from '@/api/buyer-cart'

const router = useRouter()
const loading = ref(false)
const cartItems = ref([])
const selectedItems = ref([])
const tableRef = ref(null)

const productStatusMap = {
  onsale: '在售',
  out_of_stock: '缺货',
  suspend: '已下架',
  offsale: '已下架'
}

const selectedIds = computed(() => selectedItems.value.map(item => item.id))

const selectAll = computed({
  get: () => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length,
  set: (val) => {}
})

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    if (item.productStatus === 'onsale') {
      return sum + item.price * item.quantity
    }
    return sum
  }, 0)
})

const fetchCart = async () => {
  loading.value = true
  try {
    const data = await cartApi.getCart()
    cartItems.value = data?.cartItems || []
  } catch (error) {
    ElMessage.error(error.message || '加载购物车失败')
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (row, newQuantity) => {
  if (newQuantity === row.quantity) return
  const oldQuantity = row.quantity
  row.quantity = newQuantity
  try {
    await cartApi.updateCartItem({ cartItemId: row.id, quantity: newQuantity })
    // 更新本地小计
    row.subTotal = row.price * newQuantity
    // 如果该商品已选中，刷新总价（重新计算选中项即可）
    ElMessage.success('数量已更新')
  } catch (error) {
    row.quantity = oldQuantity
    ElMessage.error(error.message || '修改数量失败')
  }
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定移出该商品吗？', '提示', { type: 'warning' })
    await cartApi.removeCartItem(id)
    ElMessage.success('已移出购物车')
    await fetchCart()
    selectedItems.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const batchRemove = async () => {
  if (selectedIds.value.length === 0) return
  try {
    await ElMessageBox.confirm(`确定移出选中的 ${selectedIds.value.length} 件商品吗？`, '批量删除', { type: 'warning' })
    for (const id of selectedIds.value) {
      await cartApi.removeCartItem(id)
    }
    ElMessage.success('已批量移出')
    await fetchCart()
    selectedItems.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

const handleSelectAll = (val) => {
  if (tableRef.value) {
    if (val) {
      tableRef.value.toggleAllSelection()
    } else {
      tableRef.value.clearSelection()
    }
  }
}

const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  const invalidItems = selectedItems.value.filter(item => item.productStatus !== 'onsale')
  if (invalidItems.length > 0) {
    ElMessage.warning(`选中的商品中包含 ${invalidItems.length} 件已下架/缺货商品，请重新选择`)
    return
  }
  const selectedInfo = selectedItems.value.map(item => ({
    id: item.id,
    productId: item.productId,
    quantity: item.quantity,
    price: item.price,
    productName: item.productName,
    image: item.image,
    sellerId: item.sellerId,
    sellerName: item.sellerName,
    storeName: item.storeName,
    cartItemId: item.id
  }))
  sessionStorage.setItem('checkout_items', JSON.stringify(selectedInfo))
  router.push('/buyer/order/create')
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 24px;
}
.empty-cart {
  text-align: center;
  padding: 60px 0;
}
.cart-wrapper {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.goods-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.goods-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}
.goods-detail {
  flex: 1;
}
.goods-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
}
.goods-sku {
  font-size: 12px;
  color: #909399;
}
.goods-status {
  font-size: 12px;
  color: #67c23a;
}
.goods-status.error {
  color: #f56c6c;
}
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.left-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}
.right-total {
  display: flex;
  align-items: center;
  gap: 24px;
}
.total-amount {
  font-size: 16px;
}
.total-price {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}
</style>