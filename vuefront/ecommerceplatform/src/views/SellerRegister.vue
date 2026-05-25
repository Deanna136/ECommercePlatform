<template>
  <div class="register-container">
    <div class="register-bg"></div>
    <div class="register-wrapper">
      <div class="register-card">
        <div class="register-header">
          <div class="logo">
            <div class="logo-icon">
              <img src="@/assets/logo.png" alt="logo" />
            </div>
            <div class="logo-text">
              <span class="title">卖家注册</span>
              <span class="subtitle">开启您的电商之旅</span>
            </div>
          </div>
        </div>
        
        <el-form :model="form" ref="formRef" label-width="0" class="register-form" @submit.prevent="handleSubmit">
          <el-form-item prop="userName" :rules="[{ required: true, message: '请输入用户名' }]">
            <el-input 
              v-model="form.userName" 
              placeholder="用户名"
              class="register-input"
            >
              <template #prefix>
                <User />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password" :rules="[{ required: true, message: '请输入密码' }]">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="密码（至少6位）"
              class="register-input"
            >
              <template #prefix>
                <Lock />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="storeName" :rules="[{ required: true, message: '请输入店铺名称' }]">
            <el-input 
              v-model="form.storeName" 
              placeholder="店铺名称"
              class="register-input"
            >
              <template #prefix>
                <Shop />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="storeCategory" :rules="[{ required: true, message: '请选择店铺类型' }]">
            <el-select 
              v-model="form.storeCategory" 
              placeholder="店铺类型"
              class="register-select"
            >
              <el-option
                v-for="item in CATEGORY_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="name">
            <el-input 
              v-model="form.name" 
              placeholder="真实姓名（选填）"
              class="register-input"
            >
              <template #prefix>
                <User />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="sex">
            <el-select 
              v-model="form.sex" 
              placeholder="性别（选填）"
              class="register-select"
            >
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="phone" :rules="[{ required: true, message: '请输入联系电话' }]">
            <el-input 
              v-model="form.phone" 
              placeholder="联系电话"
              class="register-input"
            >
              <template #prefix>
                <Phone />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="idNumber">
            <el-input 
              v-model="form.idNumber" 
              placeholder="身份证号（选填）"
              class="register-input"
            />
          </el-form-item>
          
          <el-form-item prop="address">
            <el-input 
              v-model="form.address" 
              placeholder="地址（选填）"
              class="register-input"
            />
          </el-form-item>
          
          <el-form-item class="form-actions">
            <el-button
              type="primary"
              native-type="button"
              class="register-btn"
              :loading="loading"
              @click="handleSubmit"
            >  <el-icon><UserPlus /></el-icon>
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/seller/login" class="login-link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Shop, Phone, UserPlus } from '@element-plus/icons-vue'
import { sellerRegisterApi } from '@/api/sellerUser'
import { CATEGORY_OPTIONS } from '@/utils/sellerConstants'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  userName: '',
  password: '',
  storeName: '',
  storeCategory: '',
  name: '',
  sex: '',
  phone: '',
  idNumber: '',
  address: ''
})

const handleSubmit = async () => {
  console.log('handleSubmit 被调用了')
  if (!formRef.value) return
  await formRef.value.validate()
  
  loading.value = true
  try {
    await sellerRegisterApi(form)
    ElMessage.success('注册成功')
    router.push('/seller/login')
  } catch (error) {
    ElMessage.error(error.msg || '注册失败')
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
}

.register-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.register-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 12px 32px rgba(16, 185, 129, 0.5);
  padding: 8px;
}

.logo-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.register-form {
  margin-bottom: 24px;
}

.register-input {
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
  transition: all var(--transition-normal);
}

.register-input:focus {
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.register-select {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
}

.register-select:focus {
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.form-actions {
  margin-bottom: 0;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  transition: all var(--transition-normal);
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.login-link {
  color: #10b981;
  margin-left: 8px;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.login-link:hover {
  color: #34d399;
}
</style>