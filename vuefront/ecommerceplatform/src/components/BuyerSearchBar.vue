<template>
  <div class="search-bar-wrapper">
    <el-input
      v-model="searchKey"
      placeholder="输入商品名称搜索"
      class="search-input"
      size="large"
      clearable
      @keyup.enter="handleSearch"
    >
      <template #append>
        <el-button :icon="Search" @click="handleSearch">搜索</el-button>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  }
})
const emit = defineEmits(['search'])

const searchKey = ref(props.keyword)

watch(() => props.keyword, (val) => {
  searchKey.value = val
})

const handleSearch = () => {
  emit('search', searchKey.value.trim())
}
</script>

<style scoped>
.search-bar-wrapper {
  max-width: 600px;
  margin: 0 auto 24px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 32px 0 0 32px;
  box-shadow: 0 0 0 1px #dcdfe6;
}
.search-input :deep(.el-input-group__append) {
  border-radius: 0 32px 32px 0;
  background-color: #409eff;
  border-color: #409eff;
}
.search-input :deep(.el-input-group__append button) {
  color: white;
  border: none;
}
</style>