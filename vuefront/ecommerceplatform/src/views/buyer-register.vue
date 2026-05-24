<template>
  <div class="register-wrapper">
    <div class="register-box buyer-card">
      <h2 class="title">用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" class="register-form" label-position="right" label-width="80px">
        <el-form-item label="用户名" prop="userName" class="buyer-form-item">
          <el-input v-model="form.userName" placeholder="请输入用户名" class="buyer-input" />
        </el-form-item>
        <el-form-item label="登录密码" prop="password" class="buyer-form-item">
          <el-input v-model="form.password" type="password" placeholder="请输入密码（至少6位）" class="buyer-input" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="name" class="buyer-form-item">
          <el-input v-model="form.name" placeholder="请输入真实姓名（选填）" class="buyer-input" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber" class="buyer-form-item">
          <el-input v-model="form.idNumber" placeholder="请输入18位身份证号（选填）" class="buyer-input" />
        </el-form-item>
        <el-form-item label="性别" prop="sex" class="buyer-form-item">
          <el-radio-group v-model="form.sex" class="buyer-radio sex-radio">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone" class="buyer-form-item">
          <el-input v-model="form.phone" placeholder="请输入11位手机号（选填）" class="buyer-input" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address" class="buyer-form-item">
          <el-input v-model="form.address" placeholder="请输入详细收货地址（选填）" class="buyer-input" />
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button class="buyer-btn-primary btn-submit" @click="handleRegister" :loading="loading">
          立即注册
        </el-button>
        <div class="extra-links">
          <el-link type="info" @click="goToLogin">已有账号？立即登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/buyer-user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  userName: '',
  password: '',
  name: '',
  idNumber: '',
  sex: '',
  phone: '',
  address: ''
})

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  idNumber: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的18位身份证号', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  loading.value = true
  try {
    await userApi.register(form)
    ElMessage.success('注册成功')
    router.push('/buyer/login')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/buyer/login')
}
</script>

<style scoped>
.register-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--buyer-bg-page);
  padding: var(--buyer-spacing-lg);
}
.register-box {
  width: 420px;
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
.register-form {
  font-size: 14px;
}
.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.register-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: var(--buyer-text-regular);
  line-height: 36px;
  padding-right: 12px;
}
.register-form :deep(.el-form-item__content) {
  line-height: 36px;
}
.register-form :deep(.el-input__wrapper) {
  height: 36px !important;
  border-radius: 6px !important;
  border: 1px solid var(--buyer-border-color) !important;
}
.register-form :deep(.el-input__inner) {
  height: 34px !important;
  line-height: 34px !important;
}
.register-form :deep(.el-input__wrapper:hover) {
  border-color: var(--buyer-border-color-hover) !important;
}
.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--buyer-primary) !important;
  box-shadow: none !important;
}
.sex-radio {
  display: flex;
  align-items: center;
}
.sex-radio :deep(.el-radio) {
  margin-right: 24px;
  line-height: 36px;
}
.sex-radio :deep(.el-radio__inner) {
  width: 16px;
  height: 16px;
}
.sex-radio :deep(.el-radio__label) {
  font-size: 14px;
  color: var(--buyer-text-regular);
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
  .register-box {
    width: 100%;
    padding: 24px 20px;
  }
  .btn-submit {
    width: 100%;
  }
}
</style>
