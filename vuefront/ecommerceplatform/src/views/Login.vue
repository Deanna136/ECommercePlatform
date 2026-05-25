<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1 class="logo">综合电商平台</h1>
        <span class="subtitle">管理员后台登录</span>
      </div>
      
      <div class="login-form">
        <div class="form-item">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="loginForm.userName" 
            placeholder="请输入用户名"
          />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input 
            type="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码"
          />
        </div>
        <button class="login-btn" @click="handleLogin" :disabled="loading">
          <span v-if="loading">登录中...</span>
          <span v-else>登录</span>
        </button>
        <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>
      </div>
      
      <div class="login-footer">
        <span>© 2026 综合电商平台</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'

const router = useRouter()

const loginForm = reactive({
  userName: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

const handleLogin = async () => {
  if (!loginForm.userName || !loginForm.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await axios.post('/admin/login', {
      userName: loginForm.userName,
      password: loginForm.password
    })

    // axios拦截器已经返回了 res（成功时为后端的业务数据）
    console.debug('[login] result:', result)

    if (result && result.code === 200 && result.data) {
      // 保存登录信息到localStorage
      const adminData = {
        id: result.data.id,
        userName: result.data.userName,
        token: result.data.token,
        avatar: result.data.avatar || ''
      }
      localStorage.setItem('admin', JSON.stringify(adminData))
      // 同步设置一个单独的 admin_token，方便路由守卫快速读取
      if (result.data.token) {
        localStorage.setItem('admin_token', result.data.token)
      }

      // 小的安全缓冲：先确保 localStorage 写入完成，再跳转
      console.debug('[login] stored admin data, redirecting to /admin')
      router.push('/admin')
    } else {
      errorMsg.value = (result && result.msg) || '登录失败'
    }
  } catch (error) {
    console.error('登录失败:', error)
    // 如果是我们axios抛出的Error，可能没有 error.response
    errorMsg.value = error.response?.data?.msg || error.message || '登录失败，请稍后重试'

  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a237e 0%, #3949ab 100%);
}

.login-container {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  font-size: 32px;
  font-weight: bold;
  color: #1a237e;
  margin: 0;
}

.subtitle {
  font-size: 14px;
  color: #666;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-item input {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-item input:focus {
  outline: none;
  border-color: #1a237e;
}

.form-item input::placeholder {
  color: #999;
}

.login-btn {
  background: #1a237e;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: #283593;
}

.login-btn:disabled {
  background: #9fa8da;
  cursor: not-allowed;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  text-align: center;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #999;
}
</style>