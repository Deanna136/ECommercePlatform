<template>
  <div class="buyer-container">
    <div class="detail-card" v-loading="loading">
      <div class="card-header">
        <h2 class="buyer-title">订单详情</h2>
        <el-tag :type="orderStatusType[order.status]">{{ orderStatusText[order.status] }}</el-tag>
      </div>

      <!-- 订单基本信息 -->
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ order.sellerName }} ({{ order.storeName }})</el-descriptions-item>
        <el-descriptions-item label="订单总金额">¥{{ order.amount }}</el-descriptions-item>
      </el-descriptions>

      <!-- 收货信息 -->
      <el-descriptions title="收货信息" :column="2" border class="mt-4">
        <el-descriptions-item label="收货人">{{ order.phone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ order.address }}</el-descriptions-item>
      </el-descriptions>

      <!-- 商品清单 -->
      <div class="section-title">商品清单</div>
      <el-table :data="order.orderItems" border class="goods-table">
        <el-table-column label="商品图片" width="80">
          <template #default="{ row }">
            <el-image :src="row.image" fit="cover" style="width: 60px; height: 60px;" />
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="productName" />
        <el-table-column label="商品编号" prop="productNo" width="150" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" width="80" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
      </el-table>

      <!-- 操作按钮组 -->
      <div class="action-buttons">
        <el-button v-if="canRefund" type="warning" @click="showRefundDialog = true">申请退单</el-button>
        <el-button v-if="canUpdateInfo" type="primary" @click="showUpdateDialog = true">修改收货信息</el-button>
        <el-button v-if="canReportAbnormal" type="danger" @click="showAbnormalDialog = true">异常申报</el-button>
        <el-button @click="$router.push('/buyer/order/list')">返回订单列表</el-button>
      </div>
    </div>

    <!-- 退单对话框 -->
    <el-dialog title="申请退单" v-model="showRefundDialog" width="500px">
      <el-form :model="refundForm">
        <el-form-item label="退单原因" required>
          <el-input type="textarea" v-model="refundForm.reason" :rows="3" placeholder="请描述退单原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="primary" @click="submitRefund" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 修改收货信息对话框 -->
    <el-dialog title="修改收货信息" v-model="showUpdateDialog" width="500px">
      <el-form :model="updateForm">
        <el-form-item label="联系电话">
          <el-input v-model="updateForm.phone" placeholder="新手机号" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="updateForm.address" placeholder="新地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpdateInfo" :loading="submitting">保存修改</el-button>
      </template>
    </el-dialog>

    <!-- 异常申报对话框 -->
    <el-dialog title="订单异常申报" v-model="showAbnormalDialog" width="500px">
      <el-form :model="abnormalForm">
        <el-form-item label="异常原因" required>
          <el-input type="textarea" v-model="abnormalForm.reason" :rows="3" placeholder="请详细描述异常情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAbnormalDialog = false">取消</el-button>
        <el-button type="danger" @click="submitAbnormal" :loading="submitting">确认申报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/buyer-order'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id
const loading = ref(false)
const submitting = ref(false)
const order = ref({})

// 对话框显示控制
const showRefundDialog = ref(false)
const showUpdateDialog = ref(false)
const showAbnormalDialog = ref(false)

const refundForm = ref({ reason: '' })
const updateForm = ref({ phone: '', address: '' })
const abnormalForm = ref({ reason: '' })

// 订单状态映射
const orderStatusText = {
  pending: '待付款', paid: '已付款', processing: '处理中',
  shipped: '已发货', delivered: '已送达', completed: '已完成',
  cancelled: '已取消', refunded: '已退款', failed: '交易失败', abnormal: '异常'
}
const orderStatusType = {
  pending: 'info', paid: 'warning', processing: 'primary',
  shipped: 'success', delivered: 'success', completed: 'success',
  cancelled: 'danger', refunded: 'info', failed: 'danger', abnormal: 'danger'
}

// 操作权限判断（根据文档）
const canRefund = computed(() => ['paid'].includes(order.value.status))
const canUpdateInfo = computed(() => ['paid', 'processing'].includes(order.value.status))
const canReportAbnormal = computed(() => {
  const allowed = ['paid', 'processing', 'shipped', 'delivered']
  return allowed.includes(order.value.status) && order.value.status !== 'abnormal'
})

const loadDetail = async () => {
  loading.value = true
  try {
    const data = await orderApi.getOrderDetail(orderId)
    order.value = data
    // 预填修改表单
    updateForm.value = { phone: data.phone || '', address: data.address || '' }
  } catch (error) {
    ElMessage.error(error.message || '加载订单详情失败')
    router.push('/buyer/order/list')
  } finally {
    loading.value = false
  }
}

// 退单提交
const submitRefund = async () => {
  if (!refundForm.value.reason.trim()) {
    ElMessage.warning('请输入退单原因')
    return
  }
  submitting.value = true
  try {
    await orderApi.refundOrder(orderId, { reason: refundForm.value.reason })
    ElMessage.success('退单申请已提交，等待卖家审核')
    showRefundDialog.value = false
    loadDetail() // 刷新状态
  } catch (error) {
    ElMessage.error(error.message || '退单失败')
  } finally {
    submitting.value = false
  }
}

// 修改收货信息
const submitUpdateInfo = async () => {
  const data = {}
  if (updateForm.value.phone) data.phone = updateForm.value.phone
  if (updateForm.value.address) data.address = updateForm.value.address
  if (Object.keys(data).length === 0) {
    ElMessage.warning('请至少填写一项信息')
    return
  }
  submitting.value = true
  try {
    await orderApi.updateOrderInfo(orderId, data)
    ElMessage.success('订单信息已更新')
    showUpdateDialog.value = false
    loadDetail()
  } catch (error) {
    ElMessage.error(error.message || '修改失败')
  } finally {
    submitting.value = false
  }
}

// 异常申报
const submitAbnormal = async () => {
  if (!abnormalForm.value.reason.trim()) {
    ElMessage.warning('请填写异常原因')
    return
  }
  submitting.value = true
  try {
    await orderApi.reportAbnormal(orderId, { reason: abnormalForm.value.reason })
    ElMessage.success('异常已申报，请等待管理员处理')
    showAbnormalDialog.value = false
    loadDetail()
  } catch (error) {
    ElMessage.error(error.message || '申报失败')
  } finally {
    submitting.value = false
  }
}

// 如果路由带有action参数，自动打开对应弹窗
onMounted(() => {
  loadDetail()
  const action = route.query.action
  if (action === 'refund') showRefundDialog.value = true
  else if (action === 'updateInfo') showUpdateDialog.value = true
  else if (action === 'reportAbnormal') showAbnormalDialog.value = true
})
</script>

<style scoped>
.detail-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.mt-4 {
  margin-top: 20px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 24px 0 12px;
}
.goods-table {
  margin-bottom: 24px;
}
.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>