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
      <div class="goods-price buyer-price">¥{{ goodsInfo.price }}</div>
      <div class="goods-sales">销量 {{ goodsInfo.salesCount || 0 }}</div>
      <div class="card-actions">
        <el-button size="small" class="btn-add-cart" @click.stop="handleAddCart">
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
  background: var(--buyer-bg-card);
  border-radius: var(--buyer-border-radius);
  overflow: hidden;
  border: 1px solid var(--buyer-border-color);
  transition: var(--buyer-transition);
  cursor: pointer;
}
.goods-card:hover {
  box-shadow: var(--buyer-shadow-md);
  border-color: var(--buyer-border-color-hover);
}
.card-img {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: var(--buyer-bg-input);
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
  color: var(--buyer-text-secondary);
  font-size: var(--buyer-font-aux);
}
.card-body {
  padding: var(--buyer-spacing-md);
}
.goods-name {
  font-size: var(--buyer-font-main);
  font-weight: 500;
  color: var(--buyer-text-primary);
  margin-bottom: var(--buyer-spacing-xs);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.goods-price {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: var(--buyer-spacing-xs);
}
.goods-sales {
  font-size: var(--buyer-font-aux);
  color: var(--buyer-text-secondary);
  margin-bottom: var(--buyer-spacing-md);
}
.card-actions {
  text-align: right;
}
.btn-add-cart {
  background-color: #e8e8e8;
  border-color: #e8e8e8;
  color: #333;
  border-radius: 8px;
}
.btn-add-cart:hover {
  background-color: #d0d0d0;
  border-color: #d0d0d0;
}
</style>
