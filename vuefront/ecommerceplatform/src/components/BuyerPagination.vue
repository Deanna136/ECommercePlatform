<template>
  <div class="pagination-wrap" align="right">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  page: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['page-change', 'size-change'])

const currentPage = ref(props.page)
const pageSize = ref(props.size)

watch(() => props.page, (val) => {
  currentPage.value = val
})
watch(() => props.size, (val) => {
  pageSize.value = val
})

const handlePageChange = (val) => {
  emit('page-change', val)
}
const handleSizeChange = (val) => {
  emit('size-change', val)
}
</script>

<style scoped>
.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>