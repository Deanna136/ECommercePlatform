<template>
  <div class="buyer-container">
    <div class="buyer-title">售后申请</div>

    <el-form :model="form" label-width="100px" class="mt-20">
      <el-form-item label="订单编号">
        <el-input v-model="form.orderNo" class="buyer-input" disabled />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="form.goodsName" class="buyer-input" disabled />
      </el-form-item>
      <el-form-item label="售后类型">
        <el-radio-group v-model="form.type">
          <el-radio label="REFUND">仅退款</el-radio>
          <el-radio label="RETURN">退货退款</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="退款金额">
        <el-input v-model="form.amount" class="buyer-input" />
      </el-form-item>
      <el-form-item label="售后原因">
        <el-input v-model="form.reason" type="textarea" class="buyer-input" />
      </el-form-item>
      <el-form-item>
        <el-button class="buyer-btn-primary" @click="submit">提交售后申请</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { buyerApi } from '@/api/buyer'
import { useRoute } from 'vue-router'

const route = useRoute()
const form = ref({
  orderId: route.query.orderId,
  orderNo: route.query.orderNo,
  goodsName: route.query.goodsName,
  type: 'REFUND',
  amount: route.query.price || 0,
  reason: ''
})

const submit = async () => {
  await buyerApi.order.applyAfterSale(form.value)
  ElMessage.success('提交成功')
}
</script>

<style scoped>
.mt-20 { margin-top: 20px; }
</style>