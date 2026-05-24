<template>
  <div class="buyer-container">
    <div class="buyer-title">我的店铺收藏</div>

    <div class="shop-list">
      <div class="shop-item buyer-goods-card" v-for="item in list" :key="item.id">
        <img :src="item.avatar" class="avatar" />
        <div class="info">
          <div class="name">{{ item.shopName }}</div>
          <div class="desc">商品：{{ item.goodsCount }} 件</div>
        </div>
        <el-button class="buyer-btn-secondary" @click="toShop(item.id)">进入店铺</el-button>
      </div>
    </div>

    <el-empty v-if="list.length === 0" description="暂无收藏店铺" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buyerApi } from '@/api/buyer'
import { useRouter } from 'vue-router'

const router = useRouter()
const list = ref([])

// 接口按你实际文档补充即可，结构通用
const getList = async () => {
  // const res = await buyerApi.shop.favoriteList()
  // list.value = res.data.data
}

const toShop = (id) => router.push(`/buyer/shop/${id}`)
onMounted(() => getList())
</script>

<style scoped>
.shop-list { display: flex; flex-direction: column; gap: 12px; margin-top: 20px; }
.shop-item { display: flex; align-items: center; padding: 12px 16px; gap: 12px; }
.avatar { width: 60px; height: 60px; border-radius: 50%; object-fit: cover; }
.info { flex: 1; }
.name { font-size: 16px; font-weight: bold; margin-bottom: 4px; }
.desc { color: #999; }
</style>