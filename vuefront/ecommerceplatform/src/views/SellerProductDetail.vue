<template>
  <div class="product-detail">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">商品详情</h2>
            <p class="page-subtitle">查看商品详细信息</p>
          </div>
          <el-button @click="handleBack" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="detail-content">
        <template v-if="hasProduct">
          <div class="detail-header">
            <div class="product-image" v-if="product.image">
              <el-image :src="product.image" fit="cover" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-no">商品编号：{{ product.productNo }}</p>
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ (product.price || 0).toFixed(2) }}</span>
              </div>
              <div class="product-status">
                <el-tag :type="getStatusType(product.status)" class="status-tag">
                  {{ getStatusLabel(product.status) }}
                </el-tag>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">
              <el-icon><Info /></el-icon>
              基本信息
            </h4>
            <div class="info-grid">
              <div class="info-item">
                <label>商品分类</label>
                <span>{{ product.category || '-' }}</span>
              </div>
              <div class="info-item">
                <label>库存数量</label>
                <span>{{ product.sku || 0 }}</span>
              </div>
              <div class="info-item">
                <label>已售数量</label>
                <span>{{ product.salesCount || 0 }}</span>
              </div>
              <div class="info-item">
                <label>创建时间</label>
                <span>{{ formatTime(product.createTime) }}</span>
              </div>
              <div class="info-item">
                <label>更新时间</label>
                <span>{{ formatTime(product.updateTime) }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">
              <el-icon><FileText /></el-icon>
              商品描述
            </h4>
            <div class="description-box">
              {{ product.description || '暂无描述' }}
            </div>
          </div>

          <div class="detail-actions">
            <el-button type="primary" @click="handleEdit">
              <el-icon><Edit /></el-icon>
              编辑商品
            </el-button>
            <el-button @click="handleBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </template>
        <template v-else-if="!loading">
          <div class="empty-state">
            <el-icon class="empty-icon"><Package /></el-icon>
            <p class="empty-text">暂无商品信息</p>
            <el-button type="primary" @click="handleBack">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>
        </template>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Info, FileText, Edit, Package } from '@element-plus/icons-vue'
import { sellerGetProductDetailApi } from '@/api/sellerProduct'
import { PRODUCT_STATUS } from '@/utils/sellerConstants'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const product = ref({})
const isMounted = ref(false)

const hasProduct = computed(() => {
  return product.value && Object.keys(product.value).length > 0
})

const getStatusLabel = (status) => {
  const item = PRODUCT_STATUS.find(s => s.value === status)
  return item ? item.label : status
}

const getStatusType = (status) => {
  const typeMap = {
    onsale: 'success',
    offsale: 'danger',
    pending_review: 'warning',
    approved: 'info',
    rejected: 'danger',
    out_of_stock: 'info',
    suspend: 'danger'
  }
  return typeMap[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getProductDetail = async () => {
  loading.value = true
  try {
    const res = await sellerGetProductDetailApi(route.params.id)
    if (isMounted.value) {
      product.value = res.data || {}
    }
  } catch (error) {
    console.error(error)
    if (isMounted.value) {
      ElMessage.error('获取商品详情失败')
    }
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const handleBack = () => {
  router.back()
}

const handleEdit = () => {
  if (product.value.id) {
    router.push(`/seller/product/edit/${product.value.id}`)
  }
}

onMounted(() => {
  isMounted.value = true
  getProductDetail()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style scoped>
.main-card {
  border-radius: var(--border-radius-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.page-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 8px;
}

.detail-content {
  padding: 24px;
}

.detail-header {
  display: flex;
  gap: 28px;
  padding-bottom: 28px;
  border-bottom: 1px solid var(--border-color-light);
  margin-bottom: 28px;
}

.product-image {
  width: 220px;
  height: 220px;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid var(--border-color-light);
  flex-shrink: 0;
  box-shadow: var(--shadow-md);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.product-no {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 16px;
}

.product-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 16px;
}

.price-symbol {
  font-size: 20px;
  font-weight: 600;
  color: var(--danger-color);
}

.price-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--danger-color);
}

.product-status {
  margin-top: auto;
}

.status-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px;
  color: var(--text-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: var(--bg-color);
  border-radius: 8px;
}

.info-item label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.info-item span {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}

.description-box {
  padding: 16px;
  background: var(--bg-color);
  border-radius: 8px;
  min-height: 100px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-regular);
}

.detail-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.detail-actions .el-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 8px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  font-size: 64px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}
</style>
