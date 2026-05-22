<template>
  <div class="buyer-container">
    <div class="page-header">
      <el-button @click="$router.push('/buyer/user/center')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="buyer-title">收货地址管理</h2>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">新增地址</el-button>
    </div>

    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else-if="addressList.length === 0" class="empty-wrapper">
      <el-empty description="暂无收货地址，点击上方按钮添加" />
    </div>
    <div v-else class="address-grid">
      <div
        v-for="item in addressList"
        :key="item.id"
        class="address-card"
        :class="{ 'is-default': item.isDefault }"
      >
        <div class="card-top">
          <div class="user-info">
            <span class="name">{{ item.receiver }}</span>
            <span class="phone">{{ item.phone }}</span>
          </div>
          <el-tag v-if="item.isDefault" type="success" size="small">默认地址</el-tag>
        </div>
        <div class="address-detail">
          {{ item.province }} {{ item.city }} {{ item.area }} {{ item.detail }}
        </div>
        <div class="card-actions">
          <el-button link type="primary" @click="openEditDialog(item)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(item.id)">删除</el-button>
          <el-button
            v-if="!item.isDefault"
            link
            type="info"
            @click="handleSetDefault(item.id)"
          >
            设为默认
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        label-position="right"
      >
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="form.receiver" placeholder="请填写收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="11位手机号" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-row :gutter="12">
            <el-col :span="8">
              <el-input v-model="form.province" placeholder="省" />
            </el-col>
            <el-col :span="8">
              <el-input v-model="form.city" placeholder="市" />
            </el-col>
            <el-col :span="8">
              <el-input v-model="form.area" placeholder="区/县" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="form.detail"
            type="textarea"
            rows="2"
            placeholder="街道、小区、门牌号等"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
          <span class="switch-tip">开启后将作为默认收货地址</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { addressApi } from '@/api/buyer-address'

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const form = reactive({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  area: '',
  detail: '',
  isDefault: false
})

const rules = {
  receiver: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请填写省', trigger: 'blur' }],
  city: [{ required: true, message: '请填写市', trigger: 'blur' }],
  area: [{ required: true, message: '请填写区/县', trigger: 'blur' }],
  detail: [{ required: true, message: '请填写详细地址', trigger: 'blur' }]
}

const dialogTitle = ref('新增地址')

const fetchList = async () => {
  loading.value = true
  try {
    const data = await addressApi.getList()
    addressList.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载地址失败', error)
    ElMessage.error(error?.message || '加载地址失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.receiver = ''
  form.phone = ''
  form.province = ''
  form.city = ''
  form.area = ''
  form.detail = ''
  form.isDefault = false
  if (formRef.value) formRef.value.clearValidate()
}

const openAddDialog = () => {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = '新增地址'
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (item) => {
  isEdit.value = true
  editId.value = item.id
  dialogTitle.value = '编辑地址'
  Object.assign(form, {
    receiver: item.receiver,
    phone: item.phone,
    province: item.province,
    city: item.city,
    area: item.area,
    detail: item.detail,
    isDefault: !!item.isDefault
  })
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  submitting.value = true
  try {
    const submitData = { ...form }
    if (isEdit.value) {
      submitData.id = editId.value
      await addressApi.update(submitData)
      ElMessage.success('地址修改成功')
    } else {
      await addressApi.add(submitData)
      ElMessage.success('地址添加成功')
    }
    dialogVisible.value = false
    await fetchList()
  } catch (error) {
    console.error('保存地址失败', error)
    ElMessage.error(error?.message || '保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
    .then(async () => {
      await addressApi.delete(id)
      ElMessage.success('删除成功')
      fetchList()
    })
    .catch(() => {})
}

const handleSetDefault = async (id) => {
  try {
    await addressApi.setDefault(id)
    ElMessage.success('已设为默认地址')
    fetchList()
  } catch (error) {
    ElMessage.error(error?.message || '操作失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.loading-wrapper,
.empty-wrapper {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
}
.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}
.address-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
  border: 1px solid #ebeef5;
}
.address-card.is-default {
  border-color: #67c23a;
  background: #f6ffed;
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.user-info {
  display: flex;
  gap: 16px;
  align-items: baseline;
}
.name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}
.phone {
  font-size: 14px;
  color: #7f8c8d;
}
.address-detail {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #ebeef5;
}
.card-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}
.switch-tip {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}
</style>