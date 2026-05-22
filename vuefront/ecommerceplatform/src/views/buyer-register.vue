<template>
  <div class="register-wrapper">
    <div class="register-box">
      <div class="logo-area">
        <img src="@/assets/logo.png" alt="LOGO" class="logo" />
        <h2>欢迎注册</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="register-form">
        <el-form-item label="手机号码" prop="phone">
          <div class="phone-row">
            <el-input v-model="form.phone" placeholder="请输入手机号码" clearable />
            <el-button 
              :disabled="smsSending || smsCountdown > 0" 
              @click="sendSmsCode"
              class="sms-btn"
            >
              {{ smsCountdown > 0 ? `${smsCountdown}秒后重试` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="验证码" prop="smsCode">
          <el-input v-model="form.smsCode" placeholder="请输入验证码" />
        </el-form-item>

        <el-form-item label="登录密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码长度不少于6位" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item label="选择角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="buyer">买家</el-radio>
            <el-radio value="seller">卖家</el-radio>
            <el-radio value="admin">管理员</el-radio>
          </el-radio-group>
          <div class="role-tip">* 实际注册均为买家账户，卖家和管理员需通过专门渠道开通</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="register-btn" @click="handleRegister" :loading="registering">
            确认注册
          </el-button>
        </el-form-item>

        <div class="login-link">
          <el-link type="info" @click="$router.push('/buyer/login')">已有账号？立即登录</el-link>
        </div>
      </el-form>
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
const registering = ref(false)
const smsSending = ref(false)
const smsCountdown = ref(0)

const form = reactive({
  phone: '',
  smsCode: '',
  password: '',
  confirmPassword: '',
  role: 'buyer'
})

const validatePhone = (rule, value, callback) => {
  if (!value) callback(new Error('请输入手机号码'))
  else if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('手机号码格式不正确'))
  else callback()
}
const validatePassword = (rule, value, callback) => {
  if (!value) callback(new Error('请输入密码'))
  else if (value.length < 6) callback(new Error('密码长度不能少于6位'))
  else callback()
}
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) callback(new Error('请再次输入密码'))
  else if (value !== form.password) callback(new Error('两次输入密码不一致'))
  else callback()
}
const validateSmsCode = (rule, value, callback) => {
  if (!value) callback(new Error('请输入验证码'))
  else callback()
}

const rules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  smsCode: [{ validator: validateSmsCode, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

const sendSmsCode = async () => {
  if (!form.phone) {
    ElMessage.warning('请先输入手机号码')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.error('手机号码格式不正确')
    return
  }
  smsSending.value = true
  try {
    // 模拟发送验证码
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('验证码已发送（模拟：123456）')
    form.smsCode = '123456'
    smsCountdown.value = 60
    const timer = setInterval(() => {
      if (smsCountdown.value <= 1) {
        clearInterval(timer)
        smsCountdown.value = 0
      } else {
        smsCountdown.value--
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    smsSending.value = false
  }
}

const handleRegister = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (error) {
    // 校验失败，Element Plus 会自动显示错误信息
    return
  }

  registering.value = true
  try {
    const registerData = {
      userName: form.phone,
      password: form.password,
      phone: form.phone,
      name: '',
      idNumber: '',
      sex: 'male',
      address: '',
      image: ''
    }
    await userApi.register(registerData)
    ElMessage.success('注册成功，请登录')
    router.push('/buyer/login')
  } catch (error) {
    const errMsg = error?.message || error?.msg || (typeof error === 'string' ? error : '注册失败，请稍后重试')
    ElMessage.error(errMsg)
  } finally {
    registering.value = false
  }
}
</script>

<style scoped>
.register-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}
.register-box {
  width: 520px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  padding: 40px 36px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
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
.register-form :deep(.el-form-item__label) {
  font-weight: 500;
}
.phone-row {
  display: flex;
  gap: 12px;
  width: 100%;
}
.phone-row .el-input {
  flex: 1;
}
.sms-btn {
  min-width: 110px;
}
.role-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 24px;
}
.login-link {
  text-align: center;
  margin-top: 20px;
}
</style>