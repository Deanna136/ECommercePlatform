<template>
  <div class="product-list">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">商品管理</h2>
            <p class="page-subtitle">管理您的商品列表</p>
          </div>
          <el-button type="primary" @click="handleAdd" class="add-btn">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>

      <!-- 筛选表单 -->
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="商品名称" class="form-item">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入商品名称"
            clearable
            class="filter-input"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="商品分类" class="form-item">
          <el-select
            v-model="queryParams.category"
            placeholder="请选择"
            clearable
            class="filter-select"
          >
            <el-option
              v-for="item in CATEGORY_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="商品状态" class="form-item">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择"
            clearable
            class="filter-select"
          >
            <el-option
              v-for="item in PRODUCT_STATUS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" @click="handleQuery" class="query-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery" class="reset-btn">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table 
        :data="filteredProductList" 
        border 
        stripe 
        v-loading="loading"
        class="product-table"
        :empty-text="emptyText"
      >
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="productNo" label="商品编号" width="140" />
        
        <el-table-column label="商品图片" width="100">
          <template #default="scope">
            <div class="image-wrapper" v-if="scope.row.image">
              <el-image
                :src="scope.row.image"
                fit="cover"
                :preview-src-list="[scope.row.image]"
              />
            </div>
            <span class="no-image" v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名称" min-width="180" />
        
        <el-table-column prop="price" label="价格(元)" width="120">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="sku" label="库存" width="80" />
        
        <el-table-column prop="salesCount" label="销量" width="80" />
        
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              class="action-btn action-btn-primary"
              @click="handleDetail(scope.row.id)"
            >
              <el-icon><Eye /></el-icon>
              详情
            </el-button>
            <el-button 
              v-if="['offsale', 'rejected', 'out_of_stock', 'approved', 'onsale'].includes(scope.row.status)"
              size="small" 
              class="action-btn action-btn-secondary"
              @click="handleEdit(scope.row.id)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="scope.row.status === 'onsale'"
              size="small"
              class="action-btn action-btn-danger"
              @click="handleStopSale(scope.row)"
            >
              <el-icon><Pause /></el-icon>
              停售
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Eye, Edit, Pause, Search, RefreshRight } from '@element-plus/icons-vue'
import { sellerGetProductListApi, sellerStopSaleApi } from '@/api/sellerProduct'
import { PRODUCT_STATUS, CATEGORY_OPTIONS } from '@/utils/sellerConstants'

const router = useRouter()
const productList = ref([])
const loading = ref(false)

const queryParams = ref({
  name: '',
  category: '',
  status: ''
})

const appliedQuery = ref({
  name: '',
  category: '',
  status: ''
})

const filteredProductList = computed(() => {
  let list = productList.value
  
  // 按名称筛选
  if (appliedQuery.value.name) {
    const name = appliedQuery.value.name.toLowerCase()
    list = list.filter(item => 
      item.name && item.name.toLowerCase().includes(name)
    )
  }
  
  // 按分类筛选
  if (appliedQuery.value.category) {
    list = list.filter(item => item.category === appliedQuery.value.category)
  }
  
  // 按状态筛选
  if (appliedQuery.value.status) {
    list = list.filter(item => item.status === appliedQuery.value.status)
  }
  
  return list
})

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无商品，点击上方按钮添加'
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

const getProductList = async () => {
  loading.value = true
  try {
    const res = await sellerGetProductListApi()
    productList.value = res.data || []
  } catch (error) {
    console.error(error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push('/seller/product/add')
}

const handleEdit = (id) => {
  router.push(`/seller/product/edit/${id}`)
}

const handleDetail = (id) => {
  router.push(`/seller/product/detail/${id}`)
}

const handleStopSale = (row) => {
  ElMessageBox.confirm(
    `确定停售商品"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'confirm-btn'
    }
  ).then(async () => {
    try {
      await sellerStopSaleApi(row.id)
      ElMessage.success('停售成功')
      getProductList()
    } catch (error) {
      console.error(error)
      ElMessage.error('停售失败')
    }
  })
}

const handleQuery = () => {
  appliedQuery.value = {
    name: queryParams.value.name,
    category: queryParams.value.category,
    status: queryParams.value.status
  }
}

const resetQuery = () => {
  queryParams.value = {
    name: '',
    category: '',
    status: ''
  }
  appliedQuery.value = {
    name: '',
    category: '',
    status: ''
  }
}

onMounted(() => {
  getProductList()
})
</script>

<style scoped>
.main-card {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.filter-form {
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #fafbfc 0%, #f3f4f6 100%);
  border-radius: 12px;
  border: 1px solid var(--border-color-light);
}

.form-item {
  margin-right: 16px;
}

.filter-input {
  width: 200px;
  border-radius: 8px;
}

.filter-select {
  width: 150px;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  gap: 8px;
}

.query-btn, .reset-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #fafbfc 0%, #f3f4f6 100%);
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: var(--text-primary);
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 6px 0 0;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  transition: all var(--transition-normal);
}

.add-btn:hover {
  transform: translateY(-2px);
}

.product-table {
  margin-top: 16px;
}

.product-table .el-table__header th {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  font-weight: 600;
  color: var(--text-primary);
}

.product-table .el-table__body tr:hover {
  background-color: #f8fafc;
}

.image-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: var(--text-placeholder);
  font-size: 12px;
}

.price {
  font-weight: 600;
  color: var(--danger-color);
  font-size: 15px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.action-btn {
  margin-right: 8px;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  transition: all var(--transition-normal);
}

.action-btn-primary {
  background: var(--primary-light);
  border: 1px solid rgba(64, 158, 255, 0.35);
  color: var(--primary-color);
}

.action-btn-primary:hover {
  background: rgba(64, 158, 255, 0.25);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.action-btn-secondary {
  background: var(--info-light);
  border: 1px solid rgba(144, 147, 153, 0.35);
  color: var(--text-regular);
}

.action-btn-secondary:hover {
  background: rgba(144, 147, 153, 0.25);
  transform: translateY(-1px);
}

.action-btn-danger {
  background: var(--danger-light);
  border: 1px solid rgba(245, 108, 108, 0.35);
  color: var(--danger-color);
}

.action-btn-danger:hover {
  background: rgba(245, 108, 108, 0.25);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.2);
}

:deep(.confirm-btn) {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  border: none;
}
</style>
