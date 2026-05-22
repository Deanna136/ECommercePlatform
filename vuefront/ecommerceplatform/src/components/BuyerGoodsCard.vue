<template>
  <div class="goods-card" @click="handleClick">
    <div class="card-img">
      <el-image :src="goodsInfo.image" fit="cover" lazy>
        <template #error>
          <div class="image-error">暂无图片</div>
        </template>
      </el-image>
    </div>
    <div class="card-body">
      <div class="goods-name">{{ goodsInfo.name }}</div>
      <div class="goods-price">¥{{ goodsInfo.price }}</div>
      <div class="goods-sales">销量 {{ goodsInfo.salesCount || 0 }}</div>
      <div class="card-actions">
        <el-button size="small" text type="primary" @click.stop="handleAddCart">
          加入购物车
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { cartApi } from '@/api/buyer-cart'

const props = defineProps({
  goodsInfo: {
    type: Object,
    required: true,
    default: () => ({
      id: null,
      name: '',
      price: 0,
      image: '',
      salesCount: 0
    })
  }
})

const emit = defineEmits(['click', 'add-cart'])

const handleClick = () => {
  emit('click', props.goodsInfo.id)
}

const handleAddCart = async () => {
  try {
    await cartApi.addToCart({
      productId: props.goodsInfo.id,
      quantity: 1
    })
    ElMessage.success('已加入购物车')
    emit('add-cart')
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  }
}
</script>

<style scoped>
.goods-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}
.goods-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}
.card-img {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}
.card-img .el-image {
  width: 100%;
  height: 100%;
}
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 14px;
}
.card-body {
  padding: 12px;
}
.goods-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.goods-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 6px;
}
.goods-sales {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}
.card-actions {
  text-align: right;
}
</style>