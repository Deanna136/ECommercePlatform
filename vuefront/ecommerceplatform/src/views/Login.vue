<template>
  <div class="unified-login-page">
    <div class="card">
      <div class="top">
        <div class="title">综合电商平台</div>
        <div class="switches">
          <el-button :plain="route.path !== '/admin/login'" @click="goAdmin">管理员</el-button>
          <el-button :plain="route.path !== '/seller/login'" @click="go('/seller/login')">卖家</el-button>
          <el-button :plain="route.path !== '/buyer/login'" @click="go('/buyer/login')">买家</el-button>
        </div>
        <div class="subtitle">管理员登录</div>
      </div>

      <el-form :model="form" ref="formRef" label-width="0" class="login-form">
        <el-form-item prop="userName" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
          <el-input v-model="form.userName" placeholder="用户名" class="input">
            <template #prefix><User /></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
          <el-input v-model="form.password" type="password" placeholder="密码" class="input">
            <template #prefix><Lock /></template>
          </el-input>
        </el-form-item>

        <el-form-item class="actions">
          <el-button type="primary" :loading="loading" @click="handleLogin" class="submit">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ userName: '', password: '' })

const go = (path) => router.push(path)

// 明确使用 force=1 来强制展示登录页（避免路由守卫看到旧 token 后自动重定向）
const goAdmin = () => {
  // 清理旧 token（切换到管理员登录时希望进入干净的登录页）
  localStorage.removeItem('admin')
  localStorage.removeItem('admin_token')
  router.push({ path: '/admin/login', query: { force: 1 } })
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate().catch(() => { throw new Error('验证失败') })

  loading.value = true
  try {
    const result = await axios.post('/admin/login', {
      userName: form.userName,
      password: form.password
    })

    if (result && result.code === 200 && result.data) {
      const adminData = {
        id: result.data.id,
        userName: result.data.userName,
        token: result.data.token,
        avatar: result.data.avatar || ''
      }
      localStorage.setItem('admin', JSON.stringify(adminData))
      if (result.data.token) localStorage.setItem('admin_token', result.data.token)
      ElMessage.success('管理员登录成功')
      router.push('/admin')
    } else {
      ElMessage.error((result && result.msg) || '登录失败')
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || err.message || '登录出错')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.unified-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg,#eef2ff 0%, #e0f2ff 100%);
  padding: 24px;
}
.card {
  width: 420px;
  background: #fff;
  border-radius: 14px;
  padding: 28px;
  box-shadow: 0 12px 40px rgba(16,24,40,0.08);
}
.top {
  text-align: center;
  margin-bottom: 18px;
}
.title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2d5a;
}
.switches {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 8px;
}
.subtitle {
  margin-top: 10px;
  color: #6b7280;
  font-size: 13px;
}
.login-form { margin-top: 6px; }
.input { height: 44px; border-radius: 8px; }
.actions { text-align: center; margin-top: 8px; }
.submit { width: 100%; height: 44px; border-radius: 8px; }
</style>