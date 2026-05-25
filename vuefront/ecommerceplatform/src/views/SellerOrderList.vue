<template>
  <div class="order-list">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">订单管理</h2>
            <p class="page-subtitle">管理您的订单列表</p>
          </div>
        </div>
      </template>

      <!-- 查询 -->
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="订单编号" class="form-item">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
            clearable
            class="filter-input"
          />
        </el-form-item>

        <el-form-item label="买家ID" class="form-item">
          <el-input
            v-model="queryParams.buyerId"
            placeholder="请输入买家ID"
            clearable
            class="filter-input"
          />
        </el-form-item>

        <el-form-item label="订单状态" class="form-item">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择"
            clearable
            class="filter-select"
          >
            <el-option
              v-for="item in ORDER_STATUS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="下单时间" class="form-item">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="filter-picker"
          />
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" @click="handleQuery" class="query-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery" class="reset-btn">
            <el-icon><RefreshCw /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        :data="orderList"
        border
        stripe
        v-loading="loading"
        class="order-table"
        :empty-text="emptyText"
      >
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="buyerName" label="买家姓名" width="120" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="scope">
            <span class="price">¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="480" fixed="right">
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
              size="small"
              class="action-btn action-btn-secondary"
              v-if="['paid', 'processing', 'shipped', 'delivered'].includes(scope.row.status)"
              @click="handleEdit(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>

            <el-button
              v-if="scope.row.status === 'paid'"
              size="small"
              class="action-btn action-btn-success"
              @click="handleUpdateStatus(scope.row.id, 'processing', '接单')"
            >
              <el-icon><CheckCircle /></el-icon>
              接单
            </el-button>

            <el-button
              v-if="scope.row.status === 'processing'"
              size="small"
              class="action-btn action-btn-success"
              @click="handleUpdateStatus(scope.row.id, 'shipped', '发货')"
            >
              <el-icon><Truck /></el-icon>
              发货
            </el-button>

            <el-button
              v-if="scope.row.status === 'shipped'"
              size="small"
              class="action-btn action-btn-success"
              @click="handleUpdateStatus(scope.row.id, 'delivered', '确认送达')"
            >
              <el-icon><MapPin /></el-icon>
              送达
            </el-button>

            <el-button
              v-if="scope.row.status === 'delivered'"
              size="small"
              class="action-btn action-btn-success"
              @click="handleUpdateStatus(scope.row.id, 'completed', '完成')"
            >
              <el-icon><CircleCheck /></el-icon>
              完成
            </el-button>

            <el-button
              v-if="['paid', 'processing', 'shipped', 'delivered'].includes(scope.row.status)"
              size="small"
              class="action-btn action-btn-danger"
              @click="handleReportAbnormal(scope.row)"
            >
              <el-icon><AlertTriangle /></el-icon>
              异常
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="800px"
      class="detail-dialog"
    >
      <div v-loading="loadingDetail" class="dialog-content">
        <el-descriptions
          :column="2"
          border
          v-if="orderDetail"
          class="detail-descriptions"
        >
          <el-descriptions-item label="订单ID" class="desc-item">
            <span class="desc-value">{{ orderDetail.id }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单编号" class="desc-item">
            <span class="desc-value">{{ orderDetail.orderNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态" class="desc-item">
            <el-tag :type="getStatusType(orderDetail.status)" class="status-tag">
              {{ getStatusLabel(orderDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="买家姓名" class="desc-item">
            <span class="desc-value">{{ orderDetail.buyerName || '未知' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="联系电话" class="desc-item">
            <span class="desc-value">{{ orderDetail.buyerPhone || '无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2" class="desc-item">
            <span class="desc-value address">{{ orderDetail.buyerAddress || '无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额" class="desc-item">
            <span class="desc-value price">¥{{ (orderDetail.amount || 0).toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间" class="desc-item">
            <span class="desc-value">{{ orderDetail.createTime || '未知' }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 商品明细 -->
        <div v-if="orderDetail.orderItems && orderDetail.orderItems.length > 0" class="items-section">
          <h4 class="section-title">
            <el-icon><ShoppingCart /></el-icon>
            商品明细
          </h4>
          <el-table
            :data="orderDetail.orderItems"
            border
            size="small"
            class="items-table"
          >
            <el-table-column prop="productName" label="商品名称" min-width="150" />
            <el-table-column prop="productNo" label="商品编号" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="单价" width="100" align="right">
              <template #default="scope">
                <span class="price">¥{{ scope.row.unitPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100" align="right">
              <template #default="scope">
                <span class="price">¥{{ scope.row.totalPrice }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑订单信息弹窗 -->
    <el-dialog
      v-model="editVisible"
      title="修改订单信息"
      width="500px"
      class="edit-dialog"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        label-width="100px"
        class="edit-form"
      >
        <el-form-item label="收货电话" prop="buyerPhone">
          <el-input
            v-model="editForm.buyerPhone"
            placeholder="请输入收货联系电话"
            class="form-input"
          />
        </el-form-item>
        <el-form-item label="收货地址" prop="buyerAddress">
          <el-input
            v-model="editForm.buyerAddress"
            type="textarea"
            rows="4"
            placeholder="请输入收货地址"
            class="form-textarea"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false" class="cancel-btn">
          <el-icon><RefreshCw /></el-icon>
          取消
        </el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving" class="submit-btn">
          <el-icon><CheckCircle /></el-icon>
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshCw, Eye, CheckCircle, Truck, MapPin, CircleCheck, AlertTriangle, ShoppingCart, Edit } from '@element-plus/icons-vue'

import {
  sellerQueryOrderApi,
  sellerGetOrderDetailApi,
  sellerUpdateOrderStatusApi,
  sellerReportAbnormalApi,
  sellerUpdateOrderInfoApi
} from '@/api/sellerOrder'

import { ORDER_STATUS } from '@/utils/sellerConstants'

const loading = ref(false)
const loadingDetail = ref(false)
const saving = ref(false)

const orderList = ref([])
const detailVisible = ref(false)
const editVisible = ref(false)
const orderDetail = ref(null)
const editFormRef = ref()
const currentEditOrderId = ref(null)
const dateRange = ref([])

const editForm = reactive({
  buyerPhone: '',
  buyerAddress: ''
})

const queryParams = reactive({
  orderNo: '',
  buyerId: '',
  status: ''
})

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无订单'
})

const getStatusLabel = (status) => {
  return ORDER_STATUS.find(item => item.value === status)?.label || status
}

const getStatusType = (status) => {
  const map = {
    pending: 'info',
    paid: 'warning',
    processing: 'primary',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'danger',
    abnormal: 'danger'
  }
  return map[status] || 'info'
}

const getOrderList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (dateRange.value?.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await sellerQueryOrderApi(params)
    orderList.value = res.data || []
  } catch (error) {
    console.error(error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getOrderList()
}

const resetQuery = () => {
  queryParams.orderNo = ''
  queryParams.buyerId = ''
  queryParams.status = ''
  dateRange.value = []
  getOrderList()
}

const handleDetail = async (id) => {
  loadingDetail.value = true
  try {
    const res = await sellerGetOrderDetailApi(id)
    orderDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loadingDetail.value = false
  }
}

const handleUpdateStatus = (id, status, text) => {
  ElMessageBox.confirm(
    `确认${text}吗？`,
    '提示',
    {
      type: 'warning',
      confirmButtonClass: 'confirm-btn'
    }
  ).then(async () => {
    try {
      await sellerUpdateOrderStatusApi(id, { status })
      ElMessage.success(`${text}成功`)
      getOrderList()
    } catch (error) {
      console.error(error)
      ElMessage.error(`${text}失败`)
    }
  })
}

const handleEdit = (row) => {
  currentEditOrderId.value = row.id
  editForm.buyerPhone = row.buyerPhone || ''
  editForm.buyerAddress = row.buyerAddress || ''
  editVisible.value = true
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  saving.value = true
  try {
    await sellerUpdateOrderInfoApi(currentEditOrderId.value, {
      buyerPhone: editForm.buyerPhone,
      buyerAddress: editForm.buyerAddress
    })
    ElMessage.success('修改成功')
    editVisible.value = false
    getOrderList()
  } catch (error) {
    console.error(error)
    ElMessage.error('修改失败')
  } finally {
    saving.value = false
  }
}

const handleReportAbnormal = (row) => {
  ElMessageBox.prompt(
    `请输入订单"${row.orderNo}"的异常原因`,
    '异常申报',
    {
      confirmButtonText: '确认申报',
      cancelButtonText: '取消',
      type: 'warning',
      inputPattern: /\S+/,
      inputErrorMessage: '异常原因不能为空',
      confirmButtonClass: 'confirm-btn'
    }
  ).then(async ({ value }) => {
    try {
      await sellerReportAbnormalApi(row.id, { reason: value })
      ElMessage.success('异常申报成功，请等待管理员处理')
      getOrderList()
    } catch (error) {
      console.error(error)
      ElMessage.error('异常申报失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getOrderList()
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
  border-radius: 6px;
}

.filter-select {
  width: 140px;
  border-radius: 6px;
}

.filter-picker {
  border-radius: 6px;
}

.form-actions {
  display: flex;
  gap: 8px;
}

.query-btn, .reset-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 6px;
}

.order-table {
  border-radius: 8px;
}

.order-table .el-table__header th {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  font-weight: 600;
  color: var(--text-primary);
}

.price {
  font-weight: 600;
  color: var(--danger-color);
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
  display: inline-flex;
  align-items: center;
  gap: 5px;
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

.action-btn-success {
  background: var(--success-light);
  border: 1px solid rgba(103, 194, 58, 0.35);
  color: var(--success-color);
}

.action-btn-success:hover {
  background: rgba(103, 194, 58, 0.25);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.2);
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

.detail-dialog {
  border-radius: 12px;
}

.dialog-content {
  padding: 8px;
}

.detail-descriptions {
  margin-bottom: 20px;
}

.desc-item {
  padding: 12px;
}

.desc-value {
  font-weight: 500;
  color: var(--text-primary);
}

.desc-value.address {
  color: var(--text-regular);
}

.desc-value.price {
  color: var(--danger-color);
  font-size: 16px;
}

.items-section {
  margin-top: 20px;
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

.items-table {
  border-radius: 8px;
}

.items-table .price {
  font-weight: 500;
}

:deep(.confirm-btn) {
  background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
  border: none;
}
</style>
