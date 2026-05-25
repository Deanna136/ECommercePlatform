<template>
  <div class="buyer-container">
    <div class="breadcrumb">
      <span class="breadcrumb-link" @click="$router.push('/buyer/home')">首页</span>
      <span class="breadcrumb-separator">></span>
      <span class="breadcrumb-current">修改个人信息</span>
    </div>
    
    <div class="info-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="info-form">
        <div class="avatar-item">
          <div class="avatar-wrapper">
            <div class="avatar-container">
              <el-upload
                class="avatar-uploader"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                action="#"
              >
                <img v-if="tempAvatar || form.image" :src="tempAvatar || form.image" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </div>
            <div class="tip">支持jpg/png，建议使用正方形图片</div>
          </div>
        </div>

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

        <el-form-item label="省市区" prop="addressCascader">
          <el-cascader
            v-if="addressLoaded"
            v-model="addressCascaderValue"
            :options="addressOptions"
            :props="{ expandTrigger: 'hover' }"
            placeholder="请选择省/市/区"
            clearable
            class="address-cascader"
            style="width: 100%; min-width: 0;"
          />
          <el-input v-else placeholder="加载中..." disabled class="address-cascader" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" placeholder="街道/门牌号等" clearable />
        </el-form-item>

        <el-form-item class="btn-item">
          <el-button class="btn-save" @click="save" :loading="saving">保存修改</el-button>
          <el-button class="btn-cancel" @click="handleCancel" v-if="hasModified">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/buyer-user'
import pca from 'china-area-data'

const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const hasModified = ref(false)

// 临时头像变量，用于本地预览
const tempAvatar = ref('')

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

// 级联选择值
const addressCascaderValue = ref([])

// 记录原始数据用于比较
const originalForm = reactive({
  image: '',
  name: '',
  idNumber: '',
  sex: '',
  phone: '',
  addressCascader: [],
  detailAddress: ''
})

// 表单数据
const form = reactive({
  image: '',
  name: '',
  idNumber: '',
  sex: '',
  phone: '',
  detailAddress: ''
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
    // 解析地址
    const address = localUser.address || ''
    // 尝试从完整地址中解析省市区，否则留空让用户填写
    parseAddress(address)
    // 记录原始数据
    originalForm.image = form.image
    originalForm.name = form.name
    originalForm.idNumber = form.idNumber
    originalForm.sex = form.sex
    originalForm.phone = form.phone
    originalForm.addressCascader = [...addressCascaderValue.value]
    originalForm.detailAddress = form.detailAddress
  }
}

// 根据省市区名称获取对应的code
const getProvinceCode = (provinceName) => {
  const province = addressOptions.value.find(p => p.label === provinceName)
  return province ? province.value : ''
}

const getCityCode = (provinceCode, cityName) => {
  const province = addressOptions.value.find(p => p.value === provinceCode)
  if (!province) return ''
  const city = province.children.find(c => c.label === cityName)
  return city ? city.value : ''
}

const getDistrictCode = (provinceCode, cityCode, districtName) => {
  const province = addressOptions.value.find(p => p.value === provinceCode)
  if (!province) return ''
  const city = province.children.find(c => c.value === cityCode)
  if (!city) return ''
  const district = city.children.find(d => d.label === districtName)
  return district ? district.value : ''
}

// 获取所有省份名称用于匹配
const getAllProvinceNames = () => {
  return addressOptions.value.map(p => p.label).join('|')
}

// 解析地址（从完整地址中分离省市区和详细地址）
const parseAddress = (fullAddress) => {
  if (!fullAddress) {
    addressCascaderValue.value = []
    form.detailAddress = ''
    return
  }
  
  let remaining = fullAddress
  let provinceCode = ''
  let cityCode = ''
  let districtCode = ''
  
  // 使用所有省份名称进行匹配
  const provinceNames = getAllProvinceNames()
  const provincePattern = new RegExp(`^(${provinceNames})`)
  const provinceMatch = fullAddress.match(provincePattern)
  
  if (provinceMatch) {
    const provinceName = provinceMatch[1]
    provinceCode = getProvinceCode(provinceName)
    
    if (provinceCode) {
      remaining = remaining.substring(provinceName.length).trim()
      
      // 尝试匹配城市
      const province = addressOptions.value.find(p => p.value === provinceCode)
      if (province && province.children.length > 0) {
        const cityNames = province.children.map(c => c.label).join('|')
        const cityPattern = new RegExp(`^(${cityNames})`)
        const cityMatch = remaining.match(cityPattern)
        
        if (cityMatch) {
          const cityName = cityMatch[1]
          cityCode = getCityCode(provinceCode, cityName)
          
          if (cityCode) {
            remaining = remaining.substring(cityName.length).trim()
            
            // 尝试匹配区县
            const city = province.children.find(c => c.value === cityCode)
            if (city && city.children && city.children.length > 0) {
              const districtNames = city.children.map(d => d.label).join('|')
              const districtPattern = new RegExp(`^(${districtNames})`)
              const districtMatch = remaining.match(districtPattern)
              
              if (districtMatch) {
                const districtName = districtMatch[1]
                districtCode = getDistrictCode(provinceCode, cityCode, districtName)
                
                if (districtCode) {
                  remaining = remaining.substring(districtName.length).trim()
                }
              }
            }
          }
        }
      }
    }
  }
  
  // 设置级联选择值（只在有有效选择时设置）
  const cascaderValues = []
  if (provinceCode) cascaderValues.push(provinceCode)
  if (cityCode) cascaderValues.push(cityCode)
  if (districtCode) cascaderValues.push(districtCode)
  
  addressCascaderValue.value = cascaderValues
  // 详细地址只保存门牌号等用户手动输入的内容
  form.detailAddress = remaining || ''
}

// 监听表单变化，检测是否有修改（包括头像变化）
watch(() => ({ 
  image: form.image,
  name: form.name,
  idNumber: form.idNumber,
  sex: form.sex,
  phone: form.phone,
  addressCascader: addressCascaderValue.value,
  detailAddress: form.detailAddress,
  tempAvatar: tempAvatar.value 
}), () => {
  const currentState = {
    image: form.image,
    name: form.name,
    idNumber: form.idNumber,
    sex: form.sex,
    phone: form.phone,
    addressCascader: addressCascaderValue.value,
    detailAddress: form.detailAddress
  }
  hasModified.value = JSON.stringify(currentState) !== JSON.stringify(originalForm) || !!tempAvatar.value
}, { deep: true })

const goHome = () => {
  router.push('/buyer/home')
}

const handleCancel = () => {
  // 取消后重新加载原始数据
  loadUserInfo()
  hasModified.value = false
  tempAvatar.value = ''
}

// 头像处理（仅本地预览）
const handleAvatarChange = (file) => {
  // 限制文件大小 5MB
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }
  // 限制文件类型
  if (!file.raw.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    tempAvatar.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

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

// 保存修改
const save = async () => {
  if (!formRef.value) return

  saving.value = true
  try {
    // 验证表单（Element Plus会自动显示字段级错误提示）
    await formRef.value.validate()

    // 如果选择了新头像，先上传到腾讯云
    if (tempAvatar.value && tempAvatar.value.startsWith('data:')) {
      // 将 Base64 转为 File 对象
      const blob = await fetch(tempAvatar.value).then(r => r.blob())
      const file = new File([blob], 'avatar.jpg', { type: blob.type })
      const imageUrl = await userApi.uploadImage(file)
      form.image = imageUrl
    }

    // 从级联选择器获取省市区名称并拼接完整地址
    const { provinceLabel, cityLabel, districtLabel } = getSelectedAddressLabels()
    const fullAddress = `${provinceLabel}${cityLabel}${districtLabel}${form.detailAddress}`

    // 构建请求数据，只传非空字段
    const updateData = {}
    if (form.name) updateData.name = form.name
    if (form.idNumber) updateData.idNumber = form.idNumber
    if (form.sex) updateData.sex = form.sex
    if (form.phone) updateData.phone = form.phone
    if (fullAddress) updateData.address = fullAddress
    if (form.image) updateData.image = form.image

    await userApi.updateUserInfo(updateData)
    // 更新本地存储的用户信息
    const currentUser = userApi.getLocalUserInfo() || {}
    const newUserInfo = { ...currentUser, ...updateData }
    userApi.saveUserInfo(newUserInfo)

    ElMessage.success('个人信息更新成功')
    // 清空临时头像，显示已保存的头像URL
    tempAvatar.value = ''
    hasModified.value = false
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  initAddressData()
  loadUserInfo()
})
</script>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: var(--buyer-spacing-lg);
  font-size: 14px;
}
.breadcrumb-link {
  color: #606266;
  cursor: pointer;
}
.breadcrumb-link:hover {
  color: var(--buyer-color-primary, #409EFF);
  text-decoration: underline;
}
.breadcrumb-separator {
  color: #909399;
}
.breadcrumb-current {
  color: #606266;
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
.info-form :deep(.el-form-item__content) {
  width: calc(100% - 112px);
  min-width: 0;
}
.avatar-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
}
.avatar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-container {
  display: flex;
  justify-content: center;
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
  gap: 60px !important;
}
.sex-group :deep(.el-radio) {
  line-height: 1;
  margin-right: 0 !important;
  padding-right: 0 !important;
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
.address-row {
  display: flex;
  gap: 12px;
}
.flex-1 {
  flex: 1;
  min-width: 120px;
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
</style>
