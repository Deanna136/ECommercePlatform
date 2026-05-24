<template>
  <div class="product-page">
    <!-- 数据统计 -->
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-label">商品总数：</span>
        <span class="stat-value">{{ productList.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">销售中：</span>
        <span class="stat-value normal">{{ productList.filter(p => p.status === 'onsale').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">待审核：</span>
        <span class="stat-value pending">{{ productList.filter(p => p.status === 'pending_review').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已下架：</span>
        <span class="stat-value suspended">{{ productList.filter(p => p.status === 'suspend').length }}</span>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <input type="text" v-model="filters.id" placeholder="商品ID" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.productNo" placeholder="商品编号" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.name" placeholder="商品名称" />
        </div>
        <div class="filter-item">
          <select v-model="filters.category">
            <option value="">商品分类-全部</option>
            <option value="clothing">服饰</option>
            <option value="electronics">数码电子</option>
            <option value="food">食品</option>
            <option value="home_living">家居生活</option>
            <option value="sports">运动</option>
            <option value="mother_baby">母婴</option>
            <option value="books">图书</option>
            <option value="others">其他</option>
          </select>
        </div>
      </div>
      <div class="filter-row" style="margin-top: 15px;">
        <div class="filter-item">
          <input type="text" v-model="filters.sellerId" placeholder="卖家ID" />
        </div>
        <div class="filter-item">
          <select v-model="filters.status">
            <option value="">商品状态-全部</option>
            <option value="pending_review">待审核</option>
            <option value="onsale">销售中</option>
            <option value="rejected">审核拒绝</option>
            <option value="suspend">强制下架</option>
          </select>
        </div>
        <div class="filter-item price-range">
          <input type="number" v-model="filters.minPrice" placeholder="最低价格" />
          <span>-</span>
          <input type="number" v-model="filters.maxPrice" placeholder="最高价格" />
        </div>
        <div class="filter-actions">
          <button class="btn-search" @click="searchProducts">搜索</button>
          <button class="btn-reset" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th>商品ID</th>
            <th>商品图片</th>
            <th>商品编号</th>
            <th>商品名称</th>
            <th>店铺名称</th>
            <th>价格(元)</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in filteredProductList" :key="product.id">
            <td>{{ product.id }}</td>
            <td>
              <div class="product-image">
                <img v-if="product.image" :src="product.image" alt="商品图片" class="image-img" />
                <span v-else class="image-icon">📦</span>
              </div>
            </td>
            <td>{{ product.productNo }}</td>
            <td>{{ product.name }}</td>
            <td>{{ product.storeName }}</td>
            <td>¥{{ product.price.toFixed(2) }}</td>
            <td>{{ product.sku }}</td>
            <td>{{ product.salesCount }}</td>
            <td>
              <span class="status-tag" :class="product.status">
                {{ getStatusText(product.status) }}
              </span>
            </td>
            <td class="actions">
              <button class="btn-detail" @click="viewDetail(product)">查看详情</button>
              <button 
                v-if="product.status === 'pending_review'" 
                class="btn-approve" 
                @click="openApproveModal(product)"
              >
                审核通过
              </button>
              <button 
                v-if="product.status === 'pending_review'" 
                class="btn-reject" 
                @click="openRejectModal(product)"
              >
                审核拒绝
              </button>
              <button 
                v-if="product.status === 'onsale'" 
                class="btn-suspend" 
                @click="suspendProduct(product.id)"
              >
                强制下架
              </button>
            </td>
          </tr>
          <tr v-if="filteredProductList.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>商品详情</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        <div v-if="currentProduct" class="modal-body">
          <div class="detail-row">
            <span class="detail-label">商品ID</span>
            <span class="detail-value">{{ currentProduct.id }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品编号</span>
            <span class="detail-value">{{ currentProduct.productNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品名称</span>
            <span class="detail-value">{{ currentProduct.name }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品分类</span>
            <span class="detail-value">{{ getCategoryText(currentProduct.category) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">价格</span>
            <span class="detail-value">¥{{ currentProduct.price.toFixed(2) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">库存</span>
            <span class="detail-value">{{ currentProduct.sku }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品描述</span>
            <span class="detail-value">{{ currentProduct.description }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">卖家ID</span>
            <span class="detail-value">{{ currentProduct.sellerId }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">卖家姓名</span>
            <span class="detail-value">{{ currentProduct.sellerName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">店铺名称</span>
            <span class="detail-value">{{ currentProduct.storeName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品状态</span>
            <span class="detail-value status-tag" :class="currentProduct.status">
              {{ getStatusText(currentProduct.status) }}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">累计销量</span>
            <span class="detail-value">{{ currentProduct.salesCount }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ currentProduct.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">最后修改时间</span>
            <span class="detail-value">{{ currentProduct.updateTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 审核拒绝弹窗 -->
    <div v-if="showRejectModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>审核拒绝</h3>
          <button class="modal-close" @click="closeRejectModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>拒绝原因</label>
            <textarea 
              v-model="rejectForm.rejectReason" 
              placeholder="请输入拒绝原因（可选）"
              rows="3"
            ></textarea>
          </div>
          <div v-if="rejectError" class="error-message">{{ rejectError }}</div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeRejectModal">取消</button>
          <button class="btn-confirm" @click="submitReject" :disabled="loading">
            <span v-if="loading">提交中...</span>
            <span v-else>确认拒绝</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 强制下架弹窗 -->
    <div v-if="showSuspendModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>强制下架</h3>
          <button class="modal-close" @click="closeSuspendModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>下架原因</label>
            <textarea 
              v-model="suspendForm.reason" 
              placeholder="请输入下架原因（可选）"
              rows="3"
            ></textarea>
          </div>
          <div v-if="suspendError" class="error-message">{{ suspendError }}</div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeSuspendModal">取消</button>
          <button class="btn-confirm" @click="submitSuspend" :disabled="loading">
            <span v-if="loading">提交中...</span>
            <span v-else>确认下架</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import axios from '../utils/axios'

const filters = reactive({
  id: '',
  productNo: '',
  name: '',
  category: '',
  sellerId: '',
  status: '',
  minPrice: '',
  maxPrice: ''
})

const productList = ref([])
const showDetailModal = ref(false)
const showRejectModal = ref(false)
const showSuspendModal = ref(false)
const currentProduct = ref(null)
const loading = ref(false)
const rejectError = ref('')
const suspendError = ref('')

const rejectForm = ref({
  productId: null,
  rejectReason: ''
})

const suspendForm = ref({
  productId: null,
  reason: ''
})

const filteredProductList = computed(() => {
  return productList.value.filter(product => {
    if (filters.id && product.id !== parseInt(filters.id)) return false
    if (filters.productNo && product.productNo !== filters.productNo) return false
    if (filters.name && !product.name.toLowerCase().includes(filters.name.toLowerCase())) return false
    if (filters.category && product.category !== filters.category) return false
    if (filters.sellerId && product.sellerId !== parseInt(filters.sellerId)) return false
    if (filters.status && product.status !== filters.status) return false
    if (filters.minPrice && product.price < parseFloat(filters.minPrice)) return false
    if (filters.maxPrice && product.price > parseFloat(filters.maxPrice)) return false
    return true
  })
})

const fetchProductList = async () => {
  try {
    const response = await axios.get('/admin/product/list')
    if (response.code === 200) {
      productList.value = response.data || []
    } else {
      productList.value = []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    productList.value = []
  }
}

const searchProducts = async () => {
  try {
    const params = {}
    if (filters.id) params.id = filters.id
    if (filters.productNo) params.productNo = filters.productNo
    if (filters.name) params.name = filters.name
    if (filters.category) params.category = filters.category
    if (filters.sellerId) params.sellerId = filters.sellerId
    if (filters.status) params.status = filters.status
    if (filters.minPrice) params.minPrice = filters.minPrice
    if (filters.maxPrice) params.maxPrice = filters.maxPrice

    const response = await axios.get('/admin/product/query', { params })
    if (response.code === 200) {
      productList.value = response.data || []
    } else {
      alert(response.msg || '获取商品列表失败')
    }
  } catch (error) {
    console.error('搜索商品失败:', error)
    productList.value = []
  }
}

const resetFilters = () => {
  filters.id = ''
  filters.productNo = ''
  filters.name = ''
  filters.category = ''
  filters.sellerId = ''
  filters.status = ''
  filters.minPrice = ''
  filters.maxPrice = ''
  fetchProductList()
}

const viewDetail = async (product) => {
  try {
    const response = await axios.get(`/admin/product/${product.id}`)
    if (response.code === 200) {
      currentProduct.value = response.data
    } else {
      currentProduct.value = product
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    currentProduct.value = product
  }
  showDetailModal.value = true
}

const openApproveModal = (product) => {
  approveProduct(product.id)
}

const approveProduct = async (id) => {
  if (confirm('确定要审核通过该商品吗？审核通过后商品将自动上架销售。')) {
    try {
      const response = await axios.put(`/admin/product/${id}/approve`)
      if (response.code === 200) {
        alert('审核通过，商品已上架')
        fetchProductList()
      } else {
        alert(response.msg || '操作失败')
      }
    } catch (error) {
      console.error('审核通过失败:', error)
      alert(error.response?.data?.msg || '操作失败')
    }
  }
}

const openRejectModal = (product) => {
  rejectForm.value = {
    productId: product.id,
    rejectReason: ''
  }
  rejectError.value = ''
  showRejectModal.value = true
}

const closeRejectModal = () => {
  showRejectModal.value = false
  rejectForm.value = {
    productId: null,
    rejectReason: ''
  }
  rejectError.value = ''
}

const submitReject = async () => {
  rejectError.value = ''
  loading.value = true

  try {
    const response = await axios.put(`/admin/product/${rejectForm.value.productId}/reject`, {
      rejectReason: rejectForm.value.rejectReason
    })
    if (response.code === 200) {
      alert('审核拒绝成功')
      closeRejectModal()
      fetchProductList()
    } else {
      rejectError.value = response.msg || '操作失败'
    }
  } catch (error) {
    console.error('审核拒绝失败:', error)
    rejectError.value = error.response?.data?.msg || '操作失败'
  } finally {
    loading.value = false
  }
}

const openSuspendModal = (product) => {
  suspendForm.value = {
    productId: product.id,
    reason: ''
  }
  suspendError.value = ''
  showSuspendModal.value = true
}

const closeSuspendModal = () => {
  showSuspendModal.value = false
  suspendForm.value = {
    productId: null,
    reason: ''
  }
  suspendError.value = ''
}

const suspendProduct = (id) => {
  openSuspendModal({ id })
}

const submitSuspend = async () => {
  suspendError.value = ''
  loading.value = true

  try {
    const response = await axios.put(`/admin/product/${suspendForm.value.productId}/suspend`, {
      reason: suspendForm.value.reason
    })
    if (response.code === 200) {
      alert('商品已强制下架')
      closeSuspendModal()
      fetchProductList()
    } else {
      suspendError.value = response.msg || '操作失败'
    }
  } catch (error) {
    console.error('强制下架失败:', error)
    suspendError.value = error.response?.data?.msg || '操作失败'
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    pending_review: '待审核',
    onsale: '销售中',
    rejected: '审核拒绝',
    suspend: '强制下架',
    deleted: '已删除'
  }
  return map[status] || status
}

const getCategoryText = (category) => {
  const map = {
    clothing: '服饰',
    electronics: '数码电子',
    food: '食品',
    home_living: '家居生活',
    sports: '运动',
    mother_baby: '母婴',
    books: '图书',
    others: '其他'
  }
  return map[category] || category
}

fetchProductList()
</script>

<style scoped>
.product-page {
  padding: 20px;
}

.stats-row {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
  padding: 12px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-weight: 600;
  color: #333;
}

.stat-value.normal {
  color: #2e7d32;
}

.stat-value.pending {
  color: #f57c00;
}

.stat-value.suspended {
  color: #c62828;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 15px;
}

.filter-row {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-item input,
.filter-item select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 150px;
}

.filter-item input::placeholder {
  color: #999;
}

.filter-item.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item.price-range input {
  width: 100px;
}

.filter-item.price-range span {
  color: #999;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.btn-search {
  background: #1a237e;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-reset {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 40px !important;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-icon {
  font-size: 20px;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.status-tag.onsale {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-tag.pending_review {
  background: #fff3e0;
  color: #f57c00;
}

.status-tag.rejected {
  background: #ffebee;
  color: #c62828;
}

.status-tag.suspend {
  background: #fce4ec;
  color: #c2185b;
}

.status-tag.deleted {
  background: #f5f5f5;
  color: #999;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-detail,
.btn-approve,
.btn-reject,
.btn-suspend {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.btn-detail {
  background: #409eff;
  color: white;
}

.btn-approve {
  background: #67c23a;
  color: white;
}

.btn-reject {
  background: #f56c6c;
  color: white;
}

.btn-suspend {
  background: #e65100;
  color: white;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.modal-close {
  font-size: 24px;
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.form-item label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-item input,
.form-item select,
.form-item textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-item input:focus,
.form-item select:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #1a237e;
}

.form-item textarea {
  resize: vertical;
  min-height: 80px;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  margin-top: -10px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.btn-cancel,
.btn-confirm {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-confirm {
  background: #1a237e;
  color: white;
}

.btn-confirm:hover:not(:disabled) {
  background: #283593;
}

.btn-confirm:disabled {
  background: #9fa8da;
  cursor: not-allowed;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-label {
  color: #666;
}

.detail-value {
  font-weight: 500;
}
</style>