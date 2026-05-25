<template>
  <div class="buyer-container">
    <!-- 顶部搜索栏 -->
    <div class="home-header">
      <div class="logo-area">
        <img src="@/assets/logo.png" alt="LOGO" class="logo" />
        <span class="brand">综合电商平台</span>
      </div>
      <BuyerSearchBar :keyword="keyword" @search="handleSearch" />
      <div class="user-actions">
        <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
          <el-button class="cart-btn" @click="$router.push('/buyer/cart')">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-button>
        </el-badge>
        <el-dropdown @command="handleUserCommand">
          <div class="user-info">
            <el-icon class="user-avatar"><User /></el-icon>
            <span class="user-name">{{ userName || '游客' }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="center">个人中心</el-dropdown-item>
              <el-dropdown-item command="orders">我的订单</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 商品分类导航 -->
    <div class="category-nav">
      <el-menu mode="horizontal" :ellipsis="false" class="category-menu">
        <el-menu-item index="all" @click="filterByCategory('')">全部</el-menu-item>
        <el-menu-item v-for="cat in categories" :key="cat.value" :index="cat.value" @click="filterByCategory(cat.value)">
          {{ cat.label }}
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 商品列表 -->
    <div class="section-title">热门商品</div>
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="2" animated />
    </div>
    <div v-else-if="productList.length === 0" class="buyer-empty">暂无商品</div>
    <div v-else class="goods-grid">
      <BuyerGoodsCard
        v-for="item in productList"
        :key="item.id"
        :goods-info="item"
        @click="toDetail(item.id)"
        @add-cart="refreshCart"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, User } from '@element-plus/icons-vue'
import BuyerGoodsCard from '@/components/BuyerGoodsCard.vue'
import BuyerSearchBar from '@/components/BuyerSearchBar.vue'
import { goodsApi } from '@/api/buyer-goods'
import { cartApi } from '@/api/buyer-cart'
import { userApi } from '@/api/buyer-user'

const router = useRouter()
const loading = ref(false)
const productList = ref([])
const keyword = ref('')
const currentCategory = ref('')
const cartCount = ref(0)

const userName = computed(() => {
  const user = userApi.getLocalUserInfo()
  return user?.userName || '游客'
})

const categories = [
  { label: '服饰', value: 'clothing' },
  { label: '食品', value: 'food' },
  { label: '数码电子', value: 'electronics' },
  { label: '家居生活', value: 'home_living' },
  { label: '母婴', value: 'mother_baby' },
  { label: '运动', value: 'sports' },
  { label: '图书', value: 'books' },
  { label: '其他', value: 'others' }
]

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    let data
    if (currentCategory.value) {
      data = await goodsApi.queryProducts({ category: currentCategory.value })
    } else if (keyword.value) {
      data = await goodsApi.queryProducts({ name: keyword.value })
    } else {
      data = await goodsApi.getProductList()
    }
    productList.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载商品失败', error)
    ElMessage.error(error.message || '加载商品失败')
    productList.value = []
  } finally {
    loading.value = false
  }
}

// 获取购物车数量
const fetchCartCount = async () => {
  try {
    const cart = await cartApi.getCart()
    cartCount.value = cart?.totalCount || 0
  } catch (error) {
    // 静默失败
  }
}

const handleSearch = (kw) => {
  keyword.value = kw
  currentCategory.value = ''
  loadProducts()
}

const filterByCategory = (cat) => {
  currentCategory.value = cat
  keyword.value = ''
  loadProducts()
}

const toDetail = (id) => {
  router.push(`/buyer/goods/detail/${id}`)
}

const refreshCart = () => {
  fetchCartCount()
}

const handleUserCommand = (cmd) => {
  if (cmd === 'center') {
    router.push('/buyer/user/info')
  } else if (cmd === 'orders') {
    router.push('/buyer/order/list')
  } else if (cmd === 'logout') {
    localStorage.removeItem('buyer_token')
    userApi.clearUserInfo()
    ElMessage.success('已退出登录')
    router.push('/buyer/login')
  }
}

onMounted(() => {
  loadProducts()
  fetchCartCount()
})
</script>

<style scoped>
.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--buyer-spacing-md) 0;
  margin-bottom: var(--buyer-spacing-lg);
  border-bottom: 1px solid var(--buyer-border-color);
}
.logo-area {
  display: flex;
  align-items: center;
  gap: var(--buyer-spacing-sm);
}
.logo {
  width: 44px;
  height: 44px;
}
.brand {
  font-size: 26px;
  font-weight: 1000;
  color: var(--buyer-text-primary);
}
.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cart-badge :deep(.el-badge__content) {
  top: -8px;
  right: -8px;
  transform: none;
}
.cart-btn {
  background-color: #e8e8e8;
  border-color: #e8e8e8;
  color: #333;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  position: relative;
}
.cart-btn:hover {
  background-color: #d0d0d0;
  border-color: #d0d0d0;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--buyer-text-regular);
  font-size: var(--buyer-font-main);
}
.user-avatar {
  font-size: 20px;
}
.user-name {
  color: var(--buyer-text-regular);
  font-size: var(--buyer-font-main);
}
.category-nav {
  margin-bottom: var(--buyer-spacing-lg);
}
.category-menu :deep(.el-menu) {
  border-bottom: 1px solid var(--buyer-border-color);
  display: flex;
  justify-content: space-around;
}
.category-menu :deep(.el-menu-item) {
  color: var(--buyer-text-regular);
  font-size: var(--buyer-font-main);
  flex: 1;
  text-align: center;
  justify-content: center;
}
.category-menu :deep(.el-menu-item.is-active) {
  color: var(--buyer-text-primary);
  border-bottom: 2px solid var(--buyer-price);
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--buyer-text-primary);
  margin: var(--buyer-spacing-lg) 0 var(--buyer-spacing-md);
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--buyer-spacing-lg);
}
.loading-wrapper {
  text-align: center;
  padding: 60px 0;
  background: var(--buyer-bg-card);
  border-radius: var(--buyer-border-radius);
  border: 1px solid var(--buyer-border-color);
}

/* 响应式 */
@media (max-width: 992px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .home-header {
    flex-wrap: wrap;
    gap: var(--buyer-spacing-md);
  }
  .logo-area {
    width: 100%;
    justify-content: center;
  }
}
</style>
