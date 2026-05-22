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
        <el-badge :value="cartCount" :hidden="cartCount === 0">
          <el-icon :size="24" @click="$router.push('/buyer/cart')"><ShoppingCart /></el-icon>
        </el-badge>
        <el-dropdown @command="handleUserCommand">
          <span class="user-name">{{ userName || '个人中心' }}</span>
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

    <!-- 商品分类导航（添加 index 属性） -->
    <div class="category-nav">
      <el-menu mode="horizontal" :ellipsis="false">
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
    <div v-else-if="productList.length === 0" class="empty-tip">暂无商品</div>
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
import { ShoppingCart } from '@element-plus/icons-vue'
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
    // 确保 data 是数组
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
    router.push('/buyer/user/center')
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
  padding: 16px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--buyer-border-color);
}
.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo {
  width: 48px;
  height: 48px;
}
.brand {
  font-size: 20px;
  font-weight: bold;
  color: var(--buyer-primary);
}
.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
.user-name {
  cursor: pointer;
  color: var(--buyer-text-regular);
}
.category-nav {
  margin-bottom: 24px;
  border-bottom: 1px solid var(--buyer-border-color);
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 24px 0 16px;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.loading-wrapper, .empty-tip {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 12px;
}
@media (max-width: 992px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>