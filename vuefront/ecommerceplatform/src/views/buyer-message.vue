<template>
  <div class="buyer-container">
    <div class="buyer-title">消息通知</div>
    <div class="msg-list">
      <div class="msg-item" v-for="item in list" :key="item.id">
        <div class="title">{{ item.title }}</div>
        <div class="content">{{ item.content }}</div>
        <div class="time">{{ item.createTime }}</div>
      </div>
    </div>
    <el-empty v-if="list.length === 0" description="暂无消息" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const list = ref([])

onMounted(() => {
  // 消息接口按实际补充
})
</script>

<style scoped>
.msg-list { margin-top: 20px; }
.msg-item { padding: 16px; border-bottom: 1px solid #eee; }
.msg-item .title { font-weight: bold; margin-bottom: 8px; }
.msg-item .content { color: #666; margin-bottom: 4px; }
.msg-item .time { font-size: 12px; color: #999; }
</style>