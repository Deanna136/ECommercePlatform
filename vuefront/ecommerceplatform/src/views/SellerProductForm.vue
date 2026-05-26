<template>
  <div class="product-form">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">{{ isEdit ? '编辑商品' : '新增商品' }}</h2>
            <p class="page-subtitle">{{ isEdit ? '修改商品信息' : '添加新商品' }}</p>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
        class="form-content"
      >
        <div class="form-row">
          <el-form-item label="商品编号" prop="productNo" class="form-item">
            <el-input
              v-model="form.productNo"
              :disabled="isEdit"
              placeholder="同一店铺下唯一，不可修改"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="商品名称" prop="name" class="form-item">
            <el-input v-model="form.name" placeholder="请输入商品名称" class="form-input" />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="商品类型" prop="category" class="form-item">
            <el-select v-model="form.category" placeholder="请选择商品类型" class="form-select">
              <el-option
                v-for="item in CATEGORY_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="库存" prop="sku" class="form-item">
            <el-input-number v-model="form.sku" :min="0" :step="1" class="form-input-number" />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="价格" prop="price" class="form-item">
            <div class="price-input-wrapper">
              <span class="price-prefix">¥</span>
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.01" class="price-input" />
            </div>
          </el-form-item>
        </div>

        <el-form-item label="商品图片" prop="image" class="form-item-full">
          <div class="image-upload-container">
            <el-upload
              class="image-uploader"
              action="/seller/file/upload"
              :show-file-list="false"
              :on-success="handleImageSuccess"
              :before-upload="beforeImageUpload"
              :disabled="submitting"
              :headers="uploadHeaders"
            >
              <img v-if="form.image" :src="form.image" class="image-preview" />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Upload /></el-icon>
                <span>点击上传商品图片</span>
              </div>
            </el-upload>
            <div v-if="form.image" class="image-actions">
              <el-button size="small" type="text" @click="removeImage">移除图片</el-button>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="商品描述" prop="description" class="form-item-full">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="5"
            placeholder="请输入商品描述"
            class="form-textarea"
          />
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" @click="submit" :loading="submitting" class="submit-btn">
            <el-icon><Send /></el-icon>
            {{ isEdit ? '保存修改' : '提交' }}
          </el-button>
          <el-button @click="$router.back()" class="cancel-btn">
            <el-icon><ArrowLeft /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Send, ArrowLeft, Upload } from '@element-plus/icons-vue'
import { useSellerUserStore } from '@/stores/sellerUserStore'
import {
  sellerAddProductApi,
  sellerGetProductDetailApi,
  sellerUpdateProductApi
} from '@/api/sellerProduct'
import { CATEGORY_OPTIONS } from '@/utils/sellerConstants'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)

const sellerUserStore = useSellerUserStore()

const uploadHeaders = computed(() => ({
  'seller_token': sellerUserStore.sellerToken || ''
}))

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  productNo: '',
  name: '',
  category: '',
  sku: 0,
  price: 0,
  image: '',
  description: ''
})

const rules = {
  productNo: [
    { required: true, message: '请输入商品编号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]{1,20}$/, message: '商品编号为字母数字组合，最长20位', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
  sku: [
    { required: true, message: '请输入库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ]
}

const loadProductDetail = async () => {
  if (!isEdit.value) return

  loading.value = true

  try {
    const res = await sellerGetProductDetailApi(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    router.push('/seller/product/list')
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  if (!formRef.value) return

  await formRef.value.validate()

  submitting.value = true

  try {
    if (isEdit.value) {
      const updateData = { ...form }
      delete updateData.productNo
      await sellerUpdateProductApi(route.params.id, updateData)
      ElMessage.success('修改成功')
    } else {
      await sellerAddProductApi(form)
      ElMessage.success('新增成功')
    }
    router.push('/seller/product/list')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleImageSuccess = (response) => {
  if (response.code === 200) {
    form.image = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.msg || '图片上传失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const removeImage = () => {
  form.image = ''
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.main-card {
  max-width: 800px;
  margin: 0 auto;
  border-radius: var(--border-radius-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.page-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

.form-content {
  padding: 24px;
}

.form-row {
  display: flex;
  gap: 24px;
}

.form-item {
  flex: 1;
}

.form-item-full {
  width: 100%;
}

.form-input {
  width: 100%;
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.form-input:focus {
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.form-select {
  width: 100%;
  border-radius: 8px;
}

.form-input-number {
  width: 100%;
  border-radius: 8px;
}

.price-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}

.price-prefix {
  font-size: 16px;
  color: var(--danger-color);
  font-weight: 600;
  margin-right: 8px;
}

.price-input {
  flex: 1;
  border-radius: 8px;
}

.image-preview {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.image-preview img {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.preview-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.image-upload-container {
  margin-top: 8px;
}

.image-uploader {
  display: inline-block;
  border: 2px dashed var(--border-color);
  border-radius: 8px;
  padding: 24px;
  cursor: pointer;
  transition: all var(--transition-normal);
  background-color: var(--bg-color);
}

.image-uploader:hover {
  border-color: var(--primary-color);
  background-color: rgba(64, 158, 255, 0.05);
}

.image-preview {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
}

.upload-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-placeholder span {
  font-size: 14px;
}

.image-actions {
  margin-top: 12px;
}

.form-textarea {
  width: 100%;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all var(--transition-normal);
}

.submit-btn:hover {
  transform: translateY(-1px);
}

.cancel-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 8px;
  transition: all var(--transition-normal);
}

.cancel-btn:hover {
  background: var(--bg-color);
}
</style>
