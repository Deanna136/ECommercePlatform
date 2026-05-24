<template>
  <div class="login-wrapper">
    <div class="login-box buyer-card">
      <h2 class="title">欢迎登录！</h2>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form" label-position="right" label-width="64px">
        <el-form-item label="用户名" prop="userName" class="buyer-form-item">
          <el-input
            v-model="form.userName"
            placeholder="请输入用户名"
            :prefix-icon="User"
            class="buyer-input"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" class="buyer-form-item">
          <el-input
            v-model="form.password"
            placeholder="请输入密码"
            type="password"
            :prefix-icon="Lock"
            show-password
            class="buyer-input"
          />
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button class="buyer-btn-primary btn-submit" @click="handleLogin" :loading="loading">
          立即登录
        </el-button>
        <div class="extra-links">
          <el-link type="info" @click="goToRegister">还没有账号？立即注册</el-link>
        </div>
      </div>
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
    const userData = await userApi.login({
      userName: form.userName,
      password: form.password
    })

    localStorage.setItem('buyer_token', userData.token)
    const { token, ...userInfo } = userData
    userApi.saveUserInfo(userInfo)

    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/buyer/home'
    router.push(redirect)
  } catch (error) {
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
  background: var(--buyer-bg-page);
  padding: var(--buyer-spacing-lg);
}
.login-box {
  width: 380px;
  padding: 32px 40px;
  border: 1px solid var(--buyer-border-color);
}
.title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--buyer-text-primary);
  margin: 0 0 28px 0;
}
.login-form {
  font-size: 14px;
}
.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.login-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: var(--buyer-text-regular);
  line-height: 36px;
  padding-right: 12px;
}
.login-form :deep(.el-form-item__content) {
  line-height: 36px;
}
.login-form :deep(.el-input__wrapper) {
  height: 36px !important;
  border-radius: 6px !important;
  border: 1px solid var(--buyer-border-color) !important;
}
.login-form :deep(.el-input__inner) {
  height: 34px !important;
  line-height: 34px !important;
}
.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--buyer-border-color-hover) !important;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--buyer-primary) !important;
  box-shadow: none !important;
}
.form-actions {
  margin-top: 24px;
  text-align: center;
}
.btn-submit {
  width: 120px;
}
.extra-links {
  margin-top: 16px;
}
.extra-links .el-link {
  font-size: 13px;
  color: var(--buyer-text-secondary);
}

@media (max-width: 480px) {
  .login-box {
    width: 100%;
    padding: 24px 20px;
  }
  .btn-submit {
    width: 100%;
  }
}
</style>
