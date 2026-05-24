<template>
  <div class="buyer-container">
    <div class="page-header">
      <el-button @click="$router.push('/buyer/home')" class="back-btn buyer-btn-light">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="buyer-title">我的订单</h2>
    </div>

    <el-form :model="queryForm" inline class="filter-form buyer-card" size="default">
      <el-form-item label="订单号" class="form-item">
        <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable class="buyer-input" />
      </el-form-item>
      <el-form-item label="卖家ID" class="form-item">
        <el-input v-model="queryForm.sellerId" placeholder="卖家ID" clearable class="buyer-input" />
      </el-form-item>
      <el-form-item label="订单状态" class="form-item">
        <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px;" class="buyer-select">
          <el-option v-for="(label, value) in orderStatusMap" :key="value" :label="label" :value="value" />
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
          :shortcuts="dateShortcuts"
        />
      </el-form-item>
      <el-form-item>
        <el-button class="buyer-btn-primary" @click="handleSearch">查询</el-button>
        <el-button class="buyer-btn-secondary" @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="loading" class="loading-wrapper buyer-card">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else-if="orderList.length === 0" class="buyer-empty">
      <el-empty description="暂无订单" />
    </div>
    <div v-else class="order-list">
      <BuyerOrderItem
        v-for="order in orderList"
        :key="order.id"
        :order-info="order"
        @detail="goToDetail(order.id)"
        @refund="handleRefund(order)"
        @update-info="handleUpdateInfo(order)"
        @report-abnormal="handleReportAbnormal(order)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api/buyer-order'
import BuyerOrderItem from '@/components/BuyerOrderItem.vue'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const dateRange = ref([])

const orderStatusMap = {
  pending: '待付款',
  paid: '已付款',
  processing: '处理中',
  shipped: '已发货',
  delivered: '已送达',
  completed: '已完成',
  cancelled: '已取消',
  refunded: '已退款',
  failed: '交易失败',
  abnormal: '异常'
}

const dateShortcuts = [
  { text: '最近一周', value: () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
    return [start, end]
  }},
  { text: '最近一个月', value: () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
    return [start, end]
  }},
  { text: '最近三个月', value: () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
    return [start, end]
  }}
]

const queryForm = reactive({
  orderNo: '',
  sellerId: '',
  status: '',
  startTime: '',
  endTime: ''
})

const loadOrders = async () => {
  loading.value = true
  try {
    const data = await orderApi.getOrderList()
    orderList.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载订单失败', error)
    ElMessage.error(error?.message || '加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  const params = {}
  if (queryForm.orderNo) params.orderNo = queryForm.orderNo
  if (queryForm.sellerId) params.sellerId = queryForm.sellerId
  if (queryForm.status) params.status = queryForm.status
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  if (Object.keys(params).length === 0) {
    loadOrders()
    return
  }
  loading.value = true
  try {
    const data = await orderApi.queryOrders(params)
    orderList.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error(error?.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  queryForm.orderNo = ''
  queryForm.sellerId = ''
  queryForm.status = ''
  dateRange.value = []
  loadOrders()
}

const goToDetail = (id) => {
  router.push(`/buyer/order/detail/${id}`)
}

const handleRefund = (order) => {
  router.push({ path: `/buyer/order/detail/${order.id}`, query: { action: 'refund' } })
}
const handleUpdateInfo = (order) => {
  router.push({ path: `/buyer/order/detail/${order.id}`, query: { action: 'updateInfo' } })
}
const handleReportAbnormal = (order) => {
  router.push({ path: `/buyer/order/detail/${order.id}`, query: { action: 'reportAbnormal' } })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: var(--buyer-spacing-md);
  margin-bottom: var(--buyer-spacing-lg);
}
.back-btn {
  padding: 8px 16px;
}
.filter-form {
  padding: var(--buyer-spacing-md) var(--buyer-spacing-lg);
  margin-bottom: var(--buyer-spacing-lg);
  border: 1px solid var(--buyer-border-color);
}
.form-item {
  margin-right: var(--buyer-spacing-md);
}
.loading-wrapper {
  padding: 40px;
  text-align: center;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--buyer-spacing-md);
}

@media (max-width: 768px) {
  .filter-form {
    display: flex;
    flex-wrap: wrap;
    gap: var(--buyer-spacing-md);
  }
  .form-item {
    width: 100%;
    margin-right: 0;
  }
}
</style>
