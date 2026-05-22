<template>
  <div class="order-item-card">
    <div class="order-header">
      <div class="order-no">订单号：{{ orderInfo.orderNo }}</div>
      <div class="order-status">
        <el-tag :type="statusType">{{ statusText }}</el-tag>
      </div>
    </div>
    <div class="order-info">
      <span>卖家：{{ orderInfo.storeName || orderInfo.sellerName }}</span>
      <span>下单时间：{{ orderInfo.createTime }}</span>
      <span>金额：¥{{ orderInfo.amount }}</span>
    </div>
    <div class="order-actions">
      <el-button size="small" type="primary" @click="$emit('detail', orderInfo.id)">订单详情</el-button>
      <el-button v-if="canRefund" size="small" type="warning" @click="$emit('refund', orderInfo)">申请退单</el-button>
      <el-button v-if="canUpdateInfo" size="small" @click="$emit('update-info', orderInfo)">修改信息</el-button>
      <el-button v-if="canReportAbnormal" size="small" type="danger" @click="$emit('report-abnormal', orderInfo)">异常申报</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['detail', 'refund', 'update-info', 'report-abnormal'])

const statusMap = {
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

const statusTypeMap = {
  pending: 'info',
  paid: 'warning',
  processing: 'primary',
  shipped: 'success',
  delivered: 'success',
  completed: 'success',
  cancelled: 'danger',
  refunded: 'info',
  failed: 'danger',
  abnormal: 'danger'
}

const statusText = computed(() => statusMap[props.orderInfo.status] || props.orderInfo.status)
const statusType = computed(() => statusTypeMap[props.orderInfo.status] || 'info')

// 操作权限（根据接口文档）
const canRefund = computed(() => ['paid'].includes(props.orderInfo.status))
const canUpdateInfo = computed(() => ['paid', 'processing'].includes(props.orderInfo.status))
const canReportAbnormal = computed(() => {
  const allowed = ['paid', 'processing', 'shipped', 'delivered']
  return allowed.includes(props.orderInfo.status) && props.orderInfo.status !== 'abnormal'
})
</script>

<style scoped>
.order-item-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}
.order-item-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.order-no {
  font-weight: 500;
  color: #303133;
}
.order-info {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #606266;
}
.order-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>