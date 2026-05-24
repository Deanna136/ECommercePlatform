<template>
  <div class="order-page">
    <!-- 数据统计 -->
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-label">订单总数：</span>
        <span class="stat-value">{{ orderList.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已完成：</span>
        <span class="stat-value normal">{{ orderList.filter(o => o.status === 'completed').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">异常订单：</span>
        <span class="stat-value abnormal">{{ orderList.filter(o => o.status === 'abnormal').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已取消：</span>
        <span class="stat-value cancelled">{{ orderList.filter(o => o.status === 'cancelled').length }}</span>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <input type="text" v-model="filters.id" placeholder="订单ID" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.orderNo" placeholder="订单编号" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.buyerId" placeholder="买家ID" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.sellerId" placeholder="卖家ID" />
        </div>
      </div>
      <div class="filter-row" style="margin-top: 15px;">
        <div class="filter-item">
          <select v-model="filters.status">
            <option value="">订单状态-全部</option>
            <option value="pending_payment">待付款</option>
            <option value="paid">已付款</option>
            <option value="shipped">已发货</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
            <option value="refunded">已退款</option>
            <option value="abnormal">异常</option>
            <option value="failed">失败</option>
          </select>
        </div>
        <div class="filter-item">
          <input type="date" v-model="filters.startTime" placeholder="开始日期" />
        </div>
        <div class="filter-item">
          <input type="date" v-model="filters.endTime" placeholder="结束日期" />
        </div>
        <div class="filter-actions">
          <button class="btn-search" @click="searchOrders">搜索</button>
          <button class="btn-reset" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单ID</th>
            <th>订单编号</th>
            <th>买家姓名</th>
            <th>卖家姓名</th>
            <th>订单金额</th>
            <th>订单状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in filteredOrderList" :key="order.id">
            <td>{{ order.id }}</td>
            <td>{{ order.orderNo }}</td>
            <td>{{ order.buyerName }}</td>
            <td>{{ order.sellerName }}</td>
            <td>¥{{ order.amount.toFixed(2) }}</td>
            <td>
              <span class="status-tag" :class="order.status">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td>{{ order.createTime }}</td>
            <td class="actions">
              <button class="btn-detail" @click="viewDetail(order)">查看详情</button>
              <button 
                v-if="order.status === 'abnormal'" 
                class="btn-handle" 
                @click="openHandleModal(order)"
              >
                处理异常
              </button>
              <button 
                v-if="canClose(order.status)" 
                class="btn-close" 
                @click="openCloseModal(order)"
              >
                强制关闭
              </button>
            </td>
          </tr>
          <tr v-if="filteredOrderList.length === 0">
            <td colspan="8" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        <div v-if="currentOrder" class="modal-body">
          <div class="detail-row">
            <span class="detail-label">订单ID</span>
            <span class="detail-value">{{ currentOrder.id }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单编号</span>
            <span class="detail-value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">买家ID</span>
            <span class="detail-value">{{ currentOrder.buyerId }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">买家姓名</span>
            <span class="detail-value">{{ currentOrder.buyerName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">卖家ID</span>
            <span class="detail-value">{{ currentOrder.sellerId }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">卖家姓名</span>
            <span class="detail-value">{{ currentOrder.sellerName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单金额</span>
            <span class="detail-value">¥{{ currentOrder.amount.toFixed(2) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单状态</span>
            <span class="detail-value status-tag" :class="currentOrder.status">
              {{ getStatusText(currentOrder.status) }}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">下单时间</span>
            <span class="detail-value">{{ currentOrder.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">最后更新时间</span>
            <span class="detail-value">{{ currentOrder.updateTime }}</span>
          </div>
          <div v-if="currentOrder.orderItems && currentOrder.orderItems.length > 0" class="order-items-section">
            <div class="items-title">订单商品</div>
            <div v-for="item in currentOrder.orderItems" :key="item.id" class="order-item">
              <div class="item-info">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-no">编号：{{ item.productNo }}</span>
              </div>
              <div class="item-price">
                ¥{{ item.unitPrice.toFixed(2) }} × {{ item.quantity }} = ¥{{ item.totalPrice.toFixed(2) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 异常处理弹窗 -->
    <div v-if="showHandleModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单异常处理</h3>
          <button class="modal-close" @click="closeHandleModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>处理结果 <span class="required">*</span></label>
            <select v-model="handleForm.handleResult">
              <option value="">请选择处理结果</option>
              <option value="refunded">支持退款 - 订单状态改为已退款</option>
              <option value="completed">驳回退款 - 订单状态改为已完成</option>
              <option value="cancelled">判定无效 - 订单状态改为已取消</option>
            </select>
          </div>
          <div class="form-item">
            <label>处理备注</label>
            <textarea 
              v-model="handleForm.remark" 
              placeholder="请输入处理备注说明（可选）"
              rows="3"
            ></textarea>
          </div>
          <div v-if="handleError" class="error-message">{{ handleError }}</div>
          <div class="warning-text">
            <span class="warning-icon">⚠️</span>
            <span>管理员保持中立立场处理，处理结果不可撤销，请谨慎操作</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeHandleModal">取消</button>
          <button class="btn-confirm" @click="submitHandle" :disabled="loading">
            <span v-if="loading">处理中...</span>
            <span v-else>确认提交</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 强制关闭弹窗 -->
    <div v-if="showCloseModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>强制关闭订单</h3>
          <button class="modal-close" @click="closeCloseModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>关闭原因备注</label>
            <textarea 
              v-model="closeForm.remark" 
              placeholder="请输入关闭原因备注（可选）"
              rows="3"
            ></textarea>
          </div>
          <div v-if="closeError" class="error-message">{{ closeError }}</div>
          <div class="warning-text">
            <span class="warning-icon">⚠️</span>
            <span>已处于终态（已完成/已取消/已退款/失败）的订单不可关闭</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeCloseModal">取消</button>
          <button class="btn-confirm" @click="submitClose" :disabled="loading">
            <span v-if="loading">提交中...</span>
            <span v-else>确认关闭</span>
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
  orderNo: '',
  buyerId: '',
  sellerId: '',
  status: '',
  startTime: '',
  endTime: ''
})

const orderList = ref([])
const showDetailModal = ref(false)
const showHandleModal = ref(false)
const showCloseModal = ref(false)
const currentOrder = ref(null)
const loading = ref(false)
const handleError = ref('')
const closeError = ref('')

const handleForm = ref({
  orderId: null,
  handleResult: '',
  remark: ''
})

const closeForm = ref({
  orderId: null,
  remark: ''
})

const filteredOrderList = computed(() => {
  return orderList.value.filter(order => {
    if (filters.id && order.id !== parseInt(filters.id)) return false
    if (filters.orderNo && order.orderNo !== filters.orderNo) return false
    if (filters.buyerId && order.buyerId !== parseInt(filters.buyerId)) return false
    if (filters.sellerId && order.sellerId !== parseInt(filters.sellerId)) return false
    if (filters.status && order.status !== filters.status) return false
    if (filters.startTime && order.createTime < filters.startTime) return false
    if (filters.endTime && order.createTime > filters.endTime + ' 23:59:59') return false
    return true
  })
})

const fetchOrderList = async () => {
  try {
    const response = await axios.get('/admin/order/list')
    if (response.code === 200) {
      orderList.value = response.data || []
    } else {
      orderList.value = []
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orderList.value = []
  }
}

const searchOrders = async () => {
  try {
    const params = {}
    if (filters.id) params.id = filters.id
    if (filters.orderNo) params.orderNo = filters.orderNo
    if (filters.buyerId) params.buyerId = filters.buyerId
    if (filters.sellerId) params.sellerId = filters.sellerId
    if (filters.status) params.status = filters.status
    if (filters.startTime) params.startTime = filters.startTime
    if (filters.endTime) params.endTime = filters.endTime

    const response = await axios.get('/admin/order/query', { params })
    if (response.code === 200) {
      orderList.value = response.data || []
    } else {
      alert(response.msg || '获取订单列表失败')
    }
  } catch (error) {
    console.error('搜索订单失败:', error)
    orderList.value = []
  }
}

const resetFilters = () => {
  filters.id = ''
  filters.orderNo = ''
  filters.buyerId = ''
  filters.sellerId = ''
  filters.status = ''
  filters.startTime = ''
  filters.endTime = ''
  fetchOrderList()
}

const viewDetail = async (order) => {
  try {
    const response = await axios.get(`/admin/order/${order.id}`)
    if (response.code === 200) {
      currentOrder.value = response.data
    } else {
      currentOrder.value = order
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    currentOrder.value = order
  }
  showDetailModal.value = true
}

const canClose = (status) => {
  const terminalStatuses = ['completed', 'cancelled', 'refunded', 'failed']
  return !terminalStatuses.includes(status)
}

const openHandleModal = (order) => {
  handleForm.value = {
    orderId: order.id,
    handleResult: '',
    remark: ''
  }
  handleError.value = ''
  showHandleModal.value = true
}

const closeHandleModal = () => {
  showHandleModal.value = false
  handleForm.value = {
    orderId: null,
    handleResult: '',
    remark: ''
  }
  handleError.value = ''
}

const submitHandle = async () => {
  handleError.value = ''
  
  if (!handleForm.value.handleResult) {
    handleError.value = '请选择处理结果'
    return
  }
  
  const validResults = ['refunded', 'completed', 'cancelled']
  if (!validResults.includes(handleForm.value.handleResult)) {
    handleError.value = '处理结果参数无效'
    return
  }
  
  loading.value = true
  
  try {
    const response = await axios.put(`/admin/order/${handleForm.value.orderId}/handleAbnormal`, {
      handleResult: handleForm.value.handleResult,
      remark: handleForm.value.remark
    })
    if (response.code === 200) {
      alert('订单异常处理完成')
      closeHandleModal()
      fetchOrderList()
    } else {
      handleError.value = response.msg || '操作失败'
    }
  } catch (error) {
    console.error('订单异常处理失败:', error)
    handleError.value = error.response?.data?.msg || '操作失败'
  } finally {
    loading.value = false
  }
}

const openCloseModal = (order) => {
  closeForm.value = {
    orderId: order.id,
    remark: ''
  }
  closeError.value = ''
  showCloseModal.value = true
}

const closeCloseModal = () => {
  showCloseModal.value = false
  closeForm.value = {
    orderId: null,
    remark: ''
  }
  closeError.value = ''
}

const submitClose = async () => {
  closeError.value = ''
  loading.value = true
  
  try {
    const response = await axios.put(`/admin/order/${closeForm.value.orderId}/forceClose`, {
      remark: closeForm.value.remark
    })
    if (response.code === 200) {
      alert('订单已强制关闭')
      closeCloseModal()
      fetchOrderList()
    } else {
      closeError.value = response.msg || '操作失败'
    }
  } catch (error) {
    console.error('强制关闭订单失败:', error)
    closeError.value = error.response?.data?.msg || '操作失败'
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    pending_payment: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款',
    abnormal: '异常',
    failed: '失败'
  }
  return map[status] || status
}

fetchOrderList()
</script>

<style scoped>
.order-page {
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

.stat-value.abnormal {
  color: #f57c00;
}

.stat-value.cancelled {
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

.status-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.status-tag.pending_payment {
  background: #fff3e0;
  color: #f57c00;
}

.status-tag.paid {
  background: #e3f2fd;
  color: #1565c0;
}

.status-tag.shipped {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-tag.completed {
  background: #c8e6c9;
  color: #1b5e20;
}

.status-tag.cancelled {
  background: #f5f5f5;
  color: #757575;
}

.status-tag.refunded {
  background: #fce4ec;
  color: #c2185b;
}

.status-tag.abnormal {
  background: #ffebee;
  color: #c62828;
}

.status-tag.failed {
  background: #eceff1;
  color: #546e7a;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-detail,
.btn-handle,
.btn-close {
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

.btn-handle {
  background: #f56c6c;
  color: white;
}

.btn-close {
  background: #ff9800;
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
  width: 550px;
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

.required {
  color: #f56c6c;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  margin-top: -10px;
}

.warning-text {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #fff3e0;
  border-radius: 4px;
  font-size: 13px;
  color: #e65100;
}

.warning-icon {
  font-size: 16px;
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

.order-items-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid #eee;
}

.items-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-size: 13px;
  color: #333;
}

.item-no {
  font-size: 11px;
  color: #999;
}

.item-price {
  font-size: 13px;
  color: #666;
}
</style>