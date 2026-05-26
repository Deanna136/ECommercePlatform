<template>
  <div class="profile">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">个人中心</h2>
            <p class="page-subtitle">管理您的个人信息</p>
          </div>
        </div>
      </template>

      <el-form 
        :model="form" 
        ref="formRef" 
        label-width="120px" 
        v-loading="loading"
        class="form-content"
      >
        <div class="form-section">
          <h4 class="section-title">
            <el-icon><User /></el-icon>
            基本信息
          </h4>
          
          <el-form-item class="form-item-disabled">
            <label class="form-label">用户名</label>
            <div class="form-value">{{ sellerUserStore.userName }}</div>
          </el-form-item>
          
          <el-form-item label="真实姓名" prop="name" class="form-item">
            <el-input v-model="form.name" placeholder="请输入真实姓名" class="form-input" />
          </el-form-item>
          
          <el-form-item label="性别" prop="sex" class="form-item">
            <el-select v-model="form.sex" placeholder="请选择性别" class="form-input">
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="身份证号" prop="idNumber" class="form-item">
            <el-input v-model="form.idNumber" placeholder="请输入18位身份证号" class="form-input" />
          </el-form-item>
          
          <el-form-item label="联系电话" prop="phone" class="form-item">
            <el-input v-model="form.phone" placeholder="请输入联系电话" class="form-input" />
          </el-form-item>
          
          <el-form-item label="地址" prop="address" class="form-item">
            <el-input v-model="form.address" placeholder="请输入地址" class="form-input" />
          </el-form-item>
        </div>

        <div class="form-section">
          <h4 class="section-title">
            <el-icon><Shop /></el-icon>
            店铺信息
          </h4>
          
          <el-form-item label="店铺名称" prop="storeName" class="form-item">
            <el-input v-model="form.storeName" placeholder="请输入店铺名称" class="form-input" />
          </el-form-item>
          
          <el-form-item label="店铺类型" prop="storeCategory" class="form-item">
            <el-select v-model="form.storeCategory" placeholder="请选择店铺类型" class="form-input">
              <el-option label="服装" value="clothing" />
              <el-option label="数码" value="digital" />
              <el-option label="食品" value="food" />
              <el-option label="家居" value="home" />
              <el-option label="美妆" value="beauty" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          

        </div>

        <el-form-item class="form-actions">
          <el-button type="primary" @click="handleSubmit" class="submit-btn">
            <el-icon><Save /></el-icon>
            保存修改
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            <el-icon><RefreshCw /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Save, RefreshCw, Shop } from '@element-plus/icons-vue'
import { sellerUpdateInfoApi } from '@/api/sellerUser'
import { useSellerUserStore } from '@/stores/sellerUserStore'

const formRef = ref(null)
const loading = ref(false)
const sellerUserStore = useSellerUserStore()

const form = ref({
  name: '',
  idNumber: '',
  sex: '',
  phone: '',
  address: '',
  storeName: '',
  storeCategory: ''
})

const handleSubmit = async () => {
  loading.value = true
  try {
    await sellerUpdateInfoApi(form.value)
    sellerUserStore.updateSellerInfo(form.value)
    ElMessage.success('修改成功')
  } catch (error) {
    console.error(error)
    ElMessage.error(error.msg || '修改失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  initForm()
}

const initForm = () => {
  form.value = {
    name: sellerUserStore.sellerInfo.name || '',
    idNumber: sellerUserStore.sellerInfo.idNumber || '',
    sex: sellerUserStore.sellerInfo.sex || '',
    phone: sellerUserStore.phone || '',
    address: sellerUserStore.address || '',
    storeName: sellerUserStore.storeName || '',
    storeCategory: sellerUserStore.sellerInfo.storeCategory || ''
  }
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.main-card {
  max-width: 600px;
  margin: 0 auto;
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

.form-content {
  padding: 24px;
}

.form-section {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 24px;
  color: var(--text-primary);
  padding-bottom: 16px;
  border-bottom: 3px solid var(--primary-color);
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 2px;
}

.form-item-disabled {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.form-label {
  width: 120px;
  font-weight: 500;
  color: var(--text-regular);
}

.form-value {
  flex: 1;
  padding: 10px 12px;
  background: var(--bg-color);
  border-radius: 8px;
  color: var(--text-primary);
}

.form-item {
  margin-bottom: 16px;
}

.form-input {
  width: 100%;
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.form-input:focus {
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all var(--transition-normal);
}

.submit-btn:hover {
  transform: translateY(-1px);
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 8px;
  transition: all var(--transition-normal);
}

.reset-btn:hover {
  background: var(--bg-color);
}
</style>
