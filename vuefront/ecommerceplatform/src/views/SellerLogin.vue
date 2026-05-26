<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <div class="logo-icon">
              <img src="@/assets/logo.png" alt="logo" />
            </div>
            <div class="logo-text">
              <span class="title">卖家后台</span>
              <span class="subtitle">欢迎回来</span>
            </div>
          </div>
        </div>
        
        <el-form :model="form" ref="formRef" label-width="0" class="login-form">
          <el-form-item prop="userName" :rules="[{ required: true, message: '请输入用户名' }]">
            <el-input 
              v-model="form.userName" 
              placeholder="用户名"
              class="login-input"
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
              placeholder="密码"
              class="login-input"
            >
              <template #prefix>
                <Lock />
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item class="form-actions">
            <el-button 
              type="primary" 
              class="login-btn" 
              :loading="loading"
              @click="handleSubmit"
            >
              <el-icon><Login /></el-icon>
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/seller/register" class="register-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Login } from '@element-plus/icons-vue'
import { sellerLoginApi } from '@/api/sellerUser'
import { useSellerUserStore } from '@/stores/sellerUserStore'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const sellerUserStore = useSellerUserStore()

const form = reactive({
  userName: '',
  password: ''
})

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  loading.value = true
  try {
    const res = await sellerLoginApi(form)
    sellerUserStore.sellerLogin(res.data)
    ElMessage.success('登录成功')
    router.push('/seller/product/list')
  } catch (error) {
    ElMessage.error(error.msg || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
}

.login-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.login-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
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
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.5);
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

.login-form {
  margin-bottom: 24px;
}

.login-input {
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
  transition: all var(--transition-normal);
}

.login-input:focus {
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.form-actions {
  margin-bottom: 0;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
  border: none;
  transition: all var(--transition-normal);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.register-link {
  color: #409eff;
  margin-left: 8px;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.register-link:hover {
  color: #66b1ff;
}
</style>
