<template>
  <div class="buyer-container">
    <div class="breadcrumb">
      <el-breadcrumb separator=">">
        <el-breadcrumb-item @click="goHome">首页</el-breadcrumb-item>
        <el-breadcrumb-item>修改个人信息</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="info-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="info-form">
        <el-form-item class="avatar-item">
          <span class="avatar-label">头像</span>
          <div class="avatar-wrapper">
            <el-upload
              class="avatar-uploader"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleAvatarChange"
              action="#"
            >
              <img v-if="form.image" :src="form.image" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="tip">支持jpg/png，建议使用正方形图片</div>
          </div>
        </el-form-item>

        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" clearable />
        </el-form-item>

        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="18位身份证号" clearable />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex" class="sex-group">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="11位手机号码" clearable />
        </el-form-item>

        <el-form-item label="收货地址" prop="address">
          <el-input v-model="form.address" placeholder="省市区详细地址" clearable />
        </el-form-item>

        <el-form-item class="btn-item">
          <el-button class="btn-save" @click="save" :loading="saving">保存修改</el-button>
          <el-button class="btn-cancel" @click="$router.push('/buyer/user/center')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/buyer-user'

const router = useRouter()
const formRef = ref(null)
const saving = ref(false)

// 表单数据
const form = reactive({
  image: '',
  name: '',
  idNumber: '',
  sex: '',
  phone: '',
  address: ''
})

// 校验规则
const validateIdNumber = (rule, value, callback) => {
  if (value && !/^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(value)) {
    callback(new Error('请输入正确的18位身份证号'))
  } else {
    callback()
  }
}
const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的11位手机号码'))
  } else {
    callback()
  }
}

const rules = {
  idNumber: [{ validator: validateIdNumber, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }]
}

// 加载当前用户信息
const loadUserInfo = () => {
  const localUser = userApi.getLocalUserInfo()
  if (localUser) {
    form.image = localUser.image || ''
    form.name = localUser.name || ''
    form.idNumber = localUser.idNumber || ''
    form.sex = localUser.sex || ''
    form.phone = localUser.phone || ''
    form.address = localUser.address || ''
  }
}

const goHome = () => {
  router.push('/buyer/home')
}

// 头像处理（仅前端预览，实际上传需通过腾讯云COS，此处简化）
const handleAvatarChange = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    form.image = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 保存修改
const save = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  saving.value = true
  try {
    // 构建请求数据，只传非空字段
    const updateData = {}
    if (form.name) updateData.name = form.name
    if (form.idNumber) updateData.idNumber = form.idNumber
    if (form.sex) updateData.sex = form.sex
    if (form.phone) updateData.phone = form.phone
    if (form.address) updateData.address = form.address
    if (form.image && !form.image.startsWith('http')) {
      // 注意：实际应上传到COS，这里仅做演示，假设传Base64后端会处理
      updateData.image = form.image
    } else if (form.image) {
      updateData.image = form.image
    }

    await userApi.updateUserInfo(updateData)
    // 更新本地存储的用户信息
    const currentUser = userApi.getLocalUserInfo() || {}
    const newUserInfo = { ...currentUser, ...updateData }
    userApi.saveUserInfo(newUserInfo)

    ElMessage.success('个人信息更新成功')
    router.push('/buyer/user/center')
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.breadcrumb {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 24px;
}
.breadcrumb :deep(.el-breadcrumb__item) {
  cursor: pointer;
}
.breadcrumb :deep(.el-breadcrumb__item:last-child) {
  cursor: default;
}
.breadcrumb :deep(.el-breadcrumb__link) {
  color: #666;
}
.breadcrumb :deep(.el-breadcrumb__link:hover) {
  color: #333;
}
.buyer-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 0 20px;
}
.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  border: 1px solid #e8e8e8;
}
.info-form {
  font-size: 14px;
}
.info-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.info-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: #333;
}
.avatar-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}
.avatar-label {
  width: 100px;
  text-align: right;
  font-size: 14px;
  color: #333;
  flex-shrink: 0;
  line-height: 36px;
  padding-right: 12px;
}
.avatar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-left: 58px;
}
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #dcdfe6;
}
.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}
.tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  text-align: center;
}
.sex-group {
  display: flex;
  justify-content: center;
  gap: 80px;
}
.sex-group :deep(.el-radio) {
  line-height: 1;
}
.sex-group :deep(.el-radio__inner) {
  width: 16px;
  height: 16px;
}
.sex-group :deep(.el-radio__label) {
  font-size: 14px;
  color: #333;
}
.btn-item {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 24px;
  margin-left: -100px;
  padding-left: 100px;
}
.btn-save {
  background-color: #333;
  border-color: #333;
  color: #fff;
  border-radius: 8px;
  padding: 10px 32px;
  width: 140px;
}
.btn-save:hover {
  background-color: #222;
  border-color: #222;
}
.btn-cancel {
  background-color: #fff;
  border-color: #e8e8e8;
  color: #666;
  border-radius: 8px;
  padding: 10px 32px;
  width: 140px;
}
.btn-cancel:hover {
  background-color: #f5f5f5;
}
</style>
