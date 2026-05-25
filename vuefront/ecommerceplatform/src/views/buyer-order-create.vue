<template>
  <div class="buyer-container">
    <div class="create-card buyer-card">
      <div class="breadcrumb">
        <span class="breadcrumb-link" @click="$router.push('/buyer/home')">首页</span>
        <span class="breadcrumb-separator">></span>
        <span class="breadcrumb-current">确认订单</span>
      </div>

      <!-- 收货地址 -->
      <div class="section-title">收货地址</div>
      <el-form :model="addressForm" class="address-form buyer-card">
        <div class="address-row">
          <el-form-item label="收货人" prop="receiver" class="flex-1">
            <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" class="buyer-input" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone" class="flex-1">
            <el-input v-model="addressForm.phone" placeholder="请输入联系电话" class="buyer-input" />
          </el-form-item>
        </div>
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
          <el-input v-model="addressForm.detailAddress" placeholder="请输入详细地址" class="buyer-input" />
        </el-form-item>
      </el-form>

      <!-- 商品清单 -->
      <div class="section-title">商品清单</div>
      <div class="seller-info" v-if="currentSeller">
        <span>卖家：{{ currentSeller.sellerName }} ({{ currentSeller.storeName }})</span>
      </div>
      <div class="buyer-table">
        <el-table :data="orderItems" border class="goods-table">
          <el-table-column label="商品图片" width="80">
            <template #default="{ row }">
              <el-image :src="row.image" fit="cover" class="goods-thumb" />
            </template>
          </el-table-column>
          <el-table-column label="商品名称" prop="productName" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }"><span class="buyer-price">¥{{ row.price }}</span></template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }"><span class="buyer-price">¥{{ (row.price * row.quantity).toFixed(2) }}</span></template>
          </el-table-column>
        </el-table>
      </div>

      <div class="total-row">
        <span>共 {{ totalQuantity }} 件商品，合计：</span>
        <span class="buyer-price-large">¥{{ totalAmount.toFixed(2) }}</span>
      </div>

      <div class="action-bar">
        <el-button class="buyer-btn-secondary" @click="goBack">取消</el-button>
        <el-button class="buyer-btn-primary" @click="submitOrder" :loading="submitting">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api/buyer-order'
import { userApi } from '@/api/buyer-user'
import { cartApi } from '@/api/buyer-cart'
import pca from 'china-area-data'

const router = useRouter()
const submitting = ref(false)

const orderItems = ref([])
const currentSeller = ref(null)

// 省市区级联数据（使用china-area-data包）
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

const addressForm = reactive({
  receiver: '',
  phone: '',
  detailAddress: ''
})



// 从用户信息中加载默认收货地址
const loadDefaultAddress = () => {
  const user = userApi.getLocalUserInfo()
  if (user) {
    addressForm.receiver = user.name || user.userName || ''
    addressForm.phone = user.phone || ''
    // 如果用户地址包含省市区信息，尝试解析
    if (user.address) {
      parseAddress(user.address)
    }
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
    addressForm.detailAddress = ''
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
  addressForm.detailAddress = remaining || ''
}

const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
})
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
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

const canSubmit = computed(() => {
  return addressForm.receiver.trim() && 
         addressForm.phone.trim() && 
         addressCascaderValue.value.length >= 3 && 
         addressForm.detailAddress.trim()
})

const goBack = () => {
  const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
  if (isBuyNow) {
    router.push('/buyer/home')
  } else {
    router.push('/buyer/cart')
  }
}

const initOrderData = async () => {
  const buyNowProduct = sessionStorage.getItem('buy_now_product')
  const route = useRoute()
  
  if (buyNowProduct && route.query.productId) {
    try {
      const productInfo = JSON.parse(buyNowProduct)
      const quantity = parseInt(route.query.quantity) || 1
      
      orderItems.value = [{
        id: 'buy_now_' + Date.now(),
        productId: parseInt(route.query.productId),
        productName: productInfo.name,
        productNo: productInfo.productNo || '',
        price: productInfo.price,
        image: productInfo.image,
        quantity: quantity,
        sellerId: productInfo.sellerId,
        sellerName: productInfo.sellerName,
        storeName: productInfo.storeName
      }]
      
      currentSeller.value = {
        sellerId: productInfo.sellerId,
        sellerName: productInfo.sellerName,
        storeName: productInfo.storeName
      }
      
      sessionStorage.removeItem('buy_now_product')
      return
    } catch (e) {
      console.error('解析直接购买数据失败', e)
    }
  }
  
  const checkoutData = sessionStorage.getItem('checkout_items')
  if (!checkoutData) {
    ElMessage.error('请从购物车选择商品')
    router.push('/buyer/cart')
    return
  }
  try {
    const selected = JSON.parse(checkoutData)
    if (!selected || selected.length === 0) {
      ElMessage.error('未选择任何商品')
      router.push('/buyer/cart')
      return
    }
    orderItems.value = selected
    const first = selected[0]
    const isSameSeller = selected.every(item => item.sellerId === first.sellerId)
    if (!isSameSeller) {
      ElMessage.error('一个订单只能包含同一卖家的商品，请分开下单')
      router.push('/buyer/cart')
      return
    }
    currentSeller.value = {
      sellerId: first.sellerId,
      sellerName: first.sellerName,
      storeName: first.storeName
    }
  } catch (e) {
    console.error('解析购物车数据失败', e)
    ElMessage.error('商品数据异常，请重新选择')
    router.push('/buyer/cart')
  }
}

const submitOrder = async () => {
  // 验证收货人
  if (!addressForm.receiver || !addressForm.receiver.trim()) {
    ElMessage.error('请输入收货人姓名')
    return
  }
  // 验证联系电话
  if (!addressForm.phone || !addressForm.phone.trim()) {
    ElMessage.error('请输入联系电话')
    return
  }
  // 验证手机号格式
  if (!addressForm.phone.match(/^1[3-9]\d{9}$/)) {
    ElMessage.error('联系电话格式不正确，请输入有效的手机号码')
    return
  }
  // 验证省市区（必须选择完整的三级）
  if (addressCascaderValue.value.length < 3) {
    ElMessage.error('请选择完整的省/市/区')
    return
  }
  // 验证详细地址
  if (!addressForm.detailAddress || !addressForm.detailAddress.trim()) {
    ElMessage.error('请输入详细地址')
    return
  }

  // 从级联选择器获取省市区名称并拼接完整地址
  const { provinceLabel, cityLabel, districtLabel } = getSelectedAddressLabels()
  const fullAddress = `${provinceLabel}${cityLabel}${districtLabel}${addressForm.detailAddress}`

  const orderData = {
    phone: addressForm.phone,
    address: fullAddress,
    orderItems: orderItems.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  }

  submitting.value = true
  try {
    const result = await orderApi.createOrder(orderData)
    ElMessage.success('订单创建成功')

    const isBuyNow = orderItems.value.some(item => String(item.id).startsWith('buy_now_'))
    if (!isBuyNow) {
      for (const item of orderItems.value) {
        try {
          await cartApi.removeCartItem(item.id)
        } catch (err) {
          console.error('删除购物车商品失败', err)
        }
      }
    }

    sessionStorage.removeItem('checkout_items')
    router.push(`/buyer/pay/result?orderId=${result.id}&amount=${result.amount}`)
  } catch (error) {
    console.error('下单失败', error)
    const errMsg = error?.message || (typeof error === 'string' ? error : '下单失败，请稍后重试')
    
    // 根据错误信息提供更友好的提示
    let friendlyMsg = errMsg
    if (errMsg === '参数校验失败' || errMsg === '参数错误') {
      if (!addressForm.phone || !addressForm.address) {
        friendlyMsg = '请填写完整的收货信息（手机号和收货地址）'
      } else if (!addressForm.phone.match(/^1[3-9]\d{9}$/)) {
        friendlyMsg = '手机号格式不正确，请重新输入'
      } else {
        friendlyMsg = '下单失败，商品或数量可能存在问题，请检查后重试'
      }
    }
    ElMessage.error(friendlyMsg)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initAddressData()
  initOrderData()
  loadDefaultAddress()
})
</script>

<style scoped>
.create-card {
  padding: var(--buyer-spacing-lg);
}
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
.page-header {
  display: flex;
  align-items: center;
  gap: var(--buyer-spacing-sm);
  margin-bottom: var(--buyer-spacing-lg);
}
.back-btn {
  padding: 8px 16px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--buyer-text-primary);
  margin: var(--buyer-spacing-lg) 0 var(--buyer-spacing-sm);
}
.seller-info {
  background: var(--buyer-bg-input);
  padding: var(--buyer-spacing-sm);
  border-radius: var(--buyer-border-radius-sm);
  margin-bottom: var(--buyer-spacing-sm);
  font-size: var(--buyer-font-main);
  color: var(--buyer-text-regular);
}
.goods-table {
  margin-bottom: var(--buyer-spacing-md);
}
.goods-thumb {
  width: 50px;
  height: 50px;
  border-radius: var(--buyer-border-radius-sm);
  object-fit: cover;
}
.total-row {
  text-align: right;
  font-size: var(--buyer-font-main);
  color: var(--buyer-text-regular);
  margin: var(--buyer-spacing-md) 0;
}
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: var(--buyer-spacing-md);
  margin-top: var(--buyer-spacing-lg);
  padding-top: var(--buyer-spacing-md);
  border-top: 1px solid var(--buyer-border-color);
}
.action-bar :deep(.el-button) {
  width: 120px;
}
.address-form {
  padding: var(--buyer-spacing-md);
  border: none;
  box-shadow: none;
}
.address-row {
  display: flex;
  gap: var(--buyer-spacing-md);
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

@media (max-width: 768px) {
  .address-row {
    flex-wrap: wrap;
  }
  .flex-1 {
    width: 100%;
  }
}
</style>
