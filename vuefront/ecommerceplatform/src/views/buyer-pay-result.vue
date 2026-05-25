<template>
  <div class="buyer-container">
    <div class="result-card">
      <div class="icon-wrapper" :class="success ? 'success' : 'fail'">
        <el-icon><CircleCheckFilled /></el-icon>
      </div>
      <h2>{{ success ? '订单创建成功' : '订单创建失败' }}</h2>
      <p v-if="orderId">订单号：{{ orderId }}</p>
      <p v-if="amount">订单金额：¥{{ amount }}</p>
      <div class="btn-group">
        <el-button class="btn-order buyer-btn-dark" @click="$router.push('/buyer/order/list')">查看订单</el-button>
        <el-button @click="$router.push('/buyer/home')">继续购物</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { CircleCheckFilled } from '@element-plus/icons-vue'

const route = useRoute()
const orderId = ref(route.query.orderId || '')
const amount = ref(route.query.amount || '')
const success = ref(!!orderId.value)
</script>

<style scoped>
.result-card {
  max-width: 500px;
  margin: 80px auto;
  background: #fff;
  border-radius: 24px;
  padding: 48px 32px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.icon-wrapper {
  font-size: 64px;
  margin-bottom: 24px;
}
.icon-wrapper.success {
  color: #67c23a;
}
.icon-wrapper.fail {
  color: #f56c6c;
}
.btn-group {
  margin-top: 32px;
  display: flex;
  gap: 16px;
  justify-content: center;
}
.btn-order {
  min-width: 120px;
}
.buyer-btn-dark {
  background-color: #909399;
  border-color: #909399;
  color: #fff;
}
.buyer-btn-dark:hover {
  background-color: #82848a;
  border-color: #82848a;
  color: #fff;
}
</style>