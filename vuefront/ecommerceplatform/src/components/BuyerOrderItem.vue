<template>
  <div class="order-item-card buyer-card">
    <div class="order-header">
      <div class="order-no">订单号：{{ orderInfo.orderNo }}</div>
      <div class="order-status">
        <el-tag :type="statusType">{{ statusText }}</el-tag>
      </div>
    </div>
    <div class="order-info">
      <span>卖家：{{ orderInfo.storeName || orderInfo.sellerName }}</span>
      <span>下单时间：{{ orderInfo.createTime }}</span>
      <span class="order-amount">金额：<span class="buyer-price">¥{{ orderInfo.amount }}</span></span>
    </div>
    <div class="order-actions">
      <el-button size="small" class="btn-light-gray" @click="$emit('detail', orderInfo.id)">订单详情</el-button>
      <el-button v-if="canRefund" size="small" class="btn-dark-gray" @click="$emit('refund', orderInfo)">申请退单</el-button>
      <el-button v-if="canUpdateInfo" size="small" class="btn-light-gray" @click="$emit('update-info', orderInfo)">修改信息</el-button>
      <el-button v-if="canReportAbnormal" size="small" class="btn-dark-gray" @click="$emit('report-abnormal', orderInfo)">异常申报</el-button>
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

const canRefund = computed(() => ['paid'].includes(props.orderInfo.status))
const canUpdateInfo = computed(() => ['paid', 'processing'].includes(props.orderInfo.status))
const canReportAbnormal = computed(() => {
  const allowed = ['paid', 'processing', 'shipped', 'delivered']
  return allowed.includes(props.orderInfo.status) && props.orderInfo.status !== 'abnormal'
})
</script>

<style scoped>
.order-item-card {
  padding: var(--buyer-spacing-md);
  margin-bottom: var(--buyer-spacing-md);
  border: 1px solid var(--buyer-border-color);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--buyer-spacing-sm);
  padding-bottom: var(--buyer-spacing-sm);
  border-bottom: 1px solid var(--buyer-border-color);
}
.order-no {
  font-weight: 500;
  color: var(--buyer-text-primary);
}
.order-info {
  display: flex;
  gap: var(--buyer-spacing-lg);
  margin-bottom: var(--buyer-spacing-md);
  font-size: var(--buyer-font-aux);
  color: var(--buyer-text-regular);
  flex-wrap: wrap;
}
.order-amount {
  color: var(--buyer-text-regular);
}
.order-actions {
  display: flex;
  gap: var(--buyer-spacing-sm);
  justify-content: flex-end;
}
/* 浅灰色按钮 - 订单详情、修改信息 */
.btn-light-gray {
  background-color: #f4f4f5;
  border-color: #e4e4e7;
  color: #909399;
}
.btn-light-gray:hover {
  background-color: #e9e9eb;
  border-color: #d3d4d6;
  color: #808396;
}
/* 深灰色按钮 - 申请退单、异常申报 */
.btn-dark-gray {
  background-color: #606266;
  border-color: #606266;
  color: #fff;
}
.btn-dark-gray:hover {
  background-color: #4a4a4d;
  border-color: #4a4a4d;
  color: #fff;
}

@media (max-width: 768px) {
  .order-info {
    flex-direction: column;
    gap: var(--buyer-spacing-xs);
  }
}
</style>
