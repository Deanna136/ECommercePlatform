<template>
  <div class="login-wrapper">
    <div class="login-box">
      <div class="logo-area">
        <img src="@/assets/logo.png" alt="LOGO" class="logo" />
        <h2>欢迎登录</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="userName">
          <el-input
            v-model="form.userName"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="请输入密码"
            type="password"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
        <div class="extra-links">
          <el-link type="info" @click="goToRegister">还没有账号？立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { userApi } from '@/api/buyer-user'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  userName: '',
  password: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  loading.value = true
  try {
    // 根据接口文档，登录成功返回 { id, userName, name, phone, address, image, status, token }
    const userData = await userApi.login({
      userName: form.userName,
      password: form.password
    })

    // 存储 token
    localStorage.setItem('buyer_token', userData.token)
    // 存储用户信息（不含 token）
    const { token, ...userInfo } = userData
    userApi.saveUserInfo(userInfo)

    ElMessage.success('登录成功')
    // 跳转到 redirect 参数指向的页面，默认首页
    const redirect = route.query.redirect || '/buyer/home'
    router.push(redirect)
  } catch (error) {
    // 错误已由 request 拦截器统一处理（401等），此处只补充提示
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/buyer/register')
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 460px;
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}
.logo-area {
  text-align: center;
  margin-bottom: 32px;
}
.logo {
  width: 64px;
  height: 64px;
  margin-bottom: 12px;
}
.logo-area h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}
.login-form {
  margin-top: 20px;
}
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 24px;
}
.extra-links {
  text-align: center;
  margin-top: 20px;
}
:deep(.el-input__wrapper) {
  border-radius: 24px;
  padding: 8px 16px;
}
</style>