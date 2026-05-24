<template>
  <div class="buyer-container">
    <div class="buyer-title">我的商品收藏</div>
    <BuyerPagination
      :total="total"
      :page="page"
      :size="size"
      @page-change="page = $event"
      @size-change="size = $event"
    />

    <div class="goods-grid">
      <BuyerGoodsCard
        v-for="item in list"
        :key="item.id"
        :goods-info="item"
        @click="toDetail(item.id)"
      />
    </div>

    <el-empty v-if="list.length === 0" description="暂无收藏商品" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { buyerApi } from '@/api/buyer'
import BuyerGoodsCard from '@/components/BuyerGoodsCard.vue'
import BuyerPagination from '@/components/BuyerPagination.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const getList = async () => {
  const res = await buyerApi.goods.getCollectList({ page: page.value, size: size.value })
  list.value = res.data.data.records
  total.value = res.data.data.total
}

const toDetail = (id) => router.push(`/buyer/goods/detail/${id}`)

watch([page, size], () => getList())
onMounted(() => getList())
</script>

<style scoped>
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 20px;
}
</style>