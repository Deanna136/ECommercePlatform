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
        <el-form-item label="手机号码" prop="phone" class="buyer-form-item">
          <el-input v-model="form.phone" placeholder="请输入11位手机号（选填）" class="buyer-input" />
        </el-form-item>
        <el-form-item label="省市区" prop="addressCascader" class="buyer-form-item">
          <el-cascader
            v-if="addressLoaded"
            v-model="addressCascaderValue"
            :options="addressOptions"
            :props="{ expandTrigger: 'hover' }"
            placeholder="请选择省/市/区（选填）"
            clearable
            class="address-cascader"
            style="width: 100%; min-width: 0;"
          />
          <el-input v-else placeholder="加载中..." disabled class="address-cascader" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress" class="buyer-form-item">
          <el-input v-model="form.detailAddress" placeholder="请输入详细地址" class="buyer-input" />
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
import pca from 'china-area-data'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

// 省市区级联数据
const addressOptions = ref([])
const addressLoaded = ref(false)

// 初始化地址数据
const initAddressData = () => {
  const provinces = []
  const provinceData = pca['86'] || {}
  
  for (let provinceCode in provinceData) {
    const province = {
      value: provinceCode,
      label: provinceData[provinceCode],
      children: []
    }
    
    const cityData = pca[provinceCode] || {}
    for (let cityCode in cityData) {
      const cityLabel = cityData[cityCode]
      const city = {
        value: cityCode,
        label: cityLabel,
        children: []
      }
      
      const districtData = pca[cityCode] || {}
      const districtKeys = Object.keys(districtData)
      
      // 如果城市下只有一个子项且是"市辖区"，则跳过市辖区层级
      if (districtKeys.length === 1 && districtData[districtKeys[0]] === '市辖区') {
        const subDistrictData = pca[districtKeys[0]] || {}
        for (let subDistrictCode in subDistrictData) {
          city.children.push({
            value: subDistrictCode,
            label: subDistrictData[subDistrictCode]
          })
        }
      } else {
        // 检查是否需要进一步展开（处理市辖区嵌套）
        let hasDistrict = false
        for (let districtCode of districtKeys) {
          const districtLabel = districtData[districtCode]
          
          // 如果当前项是"市辖区"且有子项，则展开它
          if (districtLabel === '市辖区') {
            const subDistrictData = pca[districtCode] || {}
            if (Object.keys(subDistrictData).length > 0) {
              // 展开市辖区的子项
              for (let subDistrictCode in subDistrictData) {
                city.children.push({
                  value: subDistrictCode,
                  label: subDistrictData[subDistrictCode]
                })
              }
              hasDistrict = true
            }
          } else {
            // 正常区县，直接添加
            city.children.push({
              value: districtCode,
              label: districtLabel
            })
            hasDistrict = true
          }
        }
        
        // 如果没有找到实际区县，保持原样
        if (!hasDistrict) {
          for (let districtCode in districtData) {
            city.children.push({
              value: districtCode,
              label: districtData[districtCode]
            })
          }
        }
      }
      
      province.children.push(city)
    }
    provinces.push(province)
  }
  addressOptions.value = provinces
  addressLoaded.value = true
}

// 页面加载时初始化地址数据
import { onMounted } from 'vue'

onMounted(() => {
  initAddressData()
})

// 级联选择值
const addressCascaderValue = ref([])

const form = reactive({
  userName: '',
  password: '',
  name: '',
  phone: '',
  detailAddress: ''
})

// 从级联选择器获取选中的省市区名称
const getSelectedAddressLabels = () => {
  let provinceLabel = ''
  let cityLabel = ''
  let districtLabel = ''
  
  if (addressCascaderValue.value.length >= 1) {
    const province = addressOptions.value.find(p => p.value === addressCascaderValue.value[0])
    provinceLabel = province ? province.label : ''
  }
  
  if (addressCascaderValue.value.length >= 2) {
    const province = addressOptions.value.find(p => p.value === addressCascaderValue.value[0])
    if (province) {
      const city = province.children.find(c => c.value === addressCascaderValue.value[1])
      cityLabel = city ? city.label : ''
    }
  }
  
  if (addressCascaderValue.value.length >= 3) {
    const province = addressOptions.value.find(p => p.value === addressCascaderValue.value[0])
    if (province) {
      const city = province.children.find(c => c.value === addressCascaderValue.value[1])
      if (city) {
        const district = city.children.find(d => d.value === addressCascaderValue.value[2])
        districtLabel = district ? district.label : ''
      }
    }
  }
  
  return { provinceLabel, cityLabel, districtLabel }
}

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  loading.value = true
  try {
    // 验证表单（Element Plus会自动显示字段级错误提示）
    await formRef.value.validate()

    // 从级联选择器获取省市区名称并拼接完整地址
    const { provinceLabel, cityLabel, districtLabel } = getSelectedAddressLabels()
    const address = `${provinceLabel}${cityLabel}${districtLabel}${form.detailAddress}`

    // 构建注册数据
    const registerData = {
      userName: form.userName,
      password: form.password,
      name: form.name,
      phone: form.phone,
      address: address
    }

    await userApi.register(registerData)
    ElMessage.success('注册成功')
    router.push('/buyer/login')
  } catch (error) {
    // 表单验证错误由Element Plus自动显示，这里只处理业务逻辑错误
    if (error.message && typeof error.message === 'string' && !error.message.includes('Error')) {
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
.address-row {
  display: flex;
  gap: 12px;
}
.flex-1 {
  flex: 1;
  min-width: 100px;
}
.address-cascader {
  width: 100% !important;
  min-width: 0 !important;
}
.address-cascader :deep(.el-cascader) {
  width: 100% !important;
  min-width: 0 !important;
}
.address-cascader :deep(.el-cascader__input) {
  width: 100% !important;
  min-width: 0 !important;
  max-width: none !important;
}
.address-cascader :deep(.el-input) {
  width: 100% !important;
  min-width: 0 !important;
}
.address-cascader :deep(.el-input__wrapper) {
  width: 100% !important;
  min-width: 0 !important;
}
.address-cascader :deep(.el-input__inner) {
  width: 100% !important;
  min-width: 0 !important;
}
.address-cascader :deep(.el-cascader-menu) {
  min-width: 200px;
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
