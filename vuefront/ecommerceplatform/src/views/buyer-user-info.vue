<template>
  <div class="buyer-container">
    <div class="info-card">
      <h3 class="section-title">编辑个人信息</h3>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="info-form">
        <el-form-item label="头像">
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
        </el-form-item>

        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" clearable />
        </el-form-item>

        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="18位身份证号" clearable />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
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

        <el-form-item>
          <el-button type="primary" @click="save" :loading="saving">保存修改</el-button>
          <el-button @click="$router.push('/buyer/user/center')">取消</el-button>
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
  sex: 'male',
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
    form.sex = localUser.sex || 'male'
    form.phone = localUser.phone || ''
    form.address = localUser.address || ''
  }
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
.buyer-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}
.info-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}
.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
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
}
.info-form {
  margin-top: 20px;
}
</style>