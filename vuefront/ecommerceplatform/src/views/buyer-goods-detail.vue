<template>
  <div class="buyer-container">
    <div class="page-header">
      <el-button @click="$router.push('/buyer/home')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="buyer-title">商品详情</h2>
    </div>
    <div class="detail-wrapper">
      <!-- 左侧图片 -->
      <div class="img-area">
        <el-image :src="product.image" fit="contain" class="main-img" />
      </div>
      <!-- 右侧信息 -->
      <div class="info-area">
        <h1 class="title">{{ product.name }}</h1>
        <div class="price-row">
          <span class="label">售价：</span>
          <span class="buyer-price">¥{{ product.price }}</span>
        </div>
        <div class="meta-row">
          <span>销量：{{ product.salesCount || 0 }}</span>
          <span>库存：{{ product.sku || 0 }}</span>
        </div>
        <div class="store-info">
          <span>店铺：{{ product.storeName }}</span>
          <span>卖家：{{ product.sellerName }}</span>
        </div>
        <div class="category-info">
          分类：{{ categoryMap[product.category] || product.category }}
        </div>

        <div class="num-row">
          <span>购买数量：</span>
          <el-input-number v-model="buyNum" :min="1" :max="product.sku" size="large" />
        </div>

        <div class="btn-group">
          <el-button type="primary" size="large" @click="addCart" :loading="cartLoading">
            加入购物车
          </el-button>
          <el-button type="success" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </div>

    <el-divider />
    <div class="buyer-title">商品详情</div>
    <div class="description">{{ product.description || '暂无详细描述' }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { goodsApi } from '@/api/buyer-goods'
import { cartApi } from '@/api/buyer-cart'

const route = useRoute()
const router = useRouter()
const productId = route.params.id
const product = ref({})
const buyNum = ref(1)
const cartLoading = ref(false)

// 商品类型映射
const categoryMap = {
  clothing: '服饰',
  food: '食品',
  electronics: '数码电子',
  home_living: '家居生活',
  mother_baby: '母婴',
  sports: '运动',
  books: '图书',
  others: '其他'
}

const loadDetail = async () => {
  try {
    const data = await goodsApi.getProductDetail(productId)
    product.value = data
  } catch (error) {
    ElMessage.error(error.message || '加载商品详情失败')
    // 如果商品不存在或已下架，返回列表页
    setTimeout(() => router.replace('/buyer/home'), 1500)
  }
}

const addCart = async () => {
  if (!product.value.id) return
  cartLoading.value = true
  try {
    await cartApi.addToCart({
      productId: product.value.id,
      quantity: buyNum.value
    })
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  } finally {
    cartLoading.value = false
  }
}

const buyNow = () => {
  if (!product.value.id) {
    ElMessage.warning('商品信息加载中，请稍候')
    return
  }
  const productData = {
    id: product.value.id,
    productNo: product.value.productNo || '',
    price: product.value.price,
    name: product.value.name,
    image: product.value.image,
    sellerId: product.value.sellerId,
    sellerName: product.value.sellerName || product.value.storeName + '店主',
    storeName: product.value.storeName
  }
  sessionStorage.setItem('buy_now_product', JSON.stringify(productData))
  router.push(`/buyer/order/create?productId=${product.value.id}&quantity=${buyNum.value}`)
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.detail-wrapper {
  display: flex;
  gap: 48px;
  margin: 20px 0 40px;
  flex-wrap: wrap;
}
.img-area {
  flex: 0 0 400px;
}
.main-img {
  width: 100%;
  border-radius: 12px;
  background: #f5f5f5;
}
.info-area {
  flex: 1;
}
.title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #2c3e50;
}
.price-row {
  margin: 16px 0;
}
.buyer-price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}
.meta-row {
  display: flex;
  gap: 24px;
  color: #7f8c8d;
  margin-bottom: 16px;
}
.store-info, .category-info {
  color: #7f8c8d;
  margin-bottom: 8px;
}
.num-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 24px 0;
}
.btn-group {
  display: flex;
  gap: 16px;
}
.description {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  line-height: 1.8;
  min-height: 200px;
}
@media (max-width: 768px) {
  .img-area {
    flex: 0 0 100%;
  }
}
</style>