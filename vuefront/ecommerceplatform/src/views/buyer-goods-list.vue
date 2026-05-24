<template>
  <div class="buyer-container">
    <div class="page-header">
      <h2 class="buyer-title">全部商品</h2>
    </div>
    <BuyerSearchBar :keyword="keyword" @search="handleSearch" />

    <div class="goods-grid">
      <BuyerGoodsCard
        v-for="item in list"
        :key="item.id"
        :goods-info="item"
        @click="toDetail(item.id)"
        @add-cart="refreshCartCount"
      />
    </div>

    <div v-if="list.length === 0" class="empty-tip">暂无商品</div>

    <!-- 分页组件（如果后端支持分页，否则前端分页） -->
    <!-- 接口文档未提供分页参数，暂不启用分页，全量展示 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BuyerGoodsCard from '@/components/BuyerGoodsCard.vue'
import BuyerSearchBar from '@/components/BuyerSearchBar.vue'
import { goodsApi } from '@/api/buyer-goods'

const router = useRouter()
const list = ref([])
const keyword = ref('')

// 加载所有在售商品（不分页）
const loadProducts = async () => {
  try {
    const data = await goodsApi.getProductList()
    list.value = data || []
  } catch (error) {
    ElMessage.error(error.message || '加载商品失败')
  }
}

// 搜索商品（调用条件筛选接口，仅传递 name）
const searchProducts = async (searchKeyword) => {
  if (!searchKeyword) {
    // 无关键词时重新加载全部
    loadProducts()
    return
  }
  try {
    const data = await goodsApi.queryProducts({ name: searchKeyword })
    list.value = data || []
  } catch (error) {
    ElMessage.error(error.message || '搜索失败')
  }
}

const handleSearch = (kw) => {
  keyword.value = kw
  if (kw && kw.trim()) {
    searchProducts(kw.trim())
  } else {
    loadProducts()
  }
}

const toDetail = (id) => {
  router.push(`/buyer/goods/detail/${id}`)
}

const refreshCartCount = () => {
  // 可以触发购物车数量更新（通过事件总线或全局状态）
  // 简单起见，此处留空
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}
.empty-tip {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 14px;
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