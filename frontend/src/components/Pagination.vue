<template>
  <div class="pagination-wrapper">
    <div class="pagination-controls">
      <button 
        class="pagination-btn"
        :disabled="currentPage === 0"
        @click="goToPage(0)"
      >
        首页
      </button>
      
      <button 
        class="pagination-btn"
        :disabled="currentPage === 0"
        @click="prevPage"
      >
        上一页
      </button>
      
      <div class="page-numbers">
        <button
          v-for="pageNum in visiblePages"
          :key="pageNum"
          class="pagination-btn"
          :class="{ active: pageNum === currentPage }"
          @click="goToPage(pageNum)"
        >
          {{ pageNum + 1 }}
        </button>
      </div>
      
      <button 
        class="pagination-btn"
        :disabled="currentPage === totalPages - 1"
        @click="nextPage"
      >
        下一页
      </button>
      
      <button 
        class="pagination-btn"
        :disabled="currentPage === totalPages - 1"
        @click="goToPage(totalPages - 1)"
      >
        末页
      </button>
    </div>
    
    <div class="pagination-info">
      <span>共 {{ total }} 条记录，第 {{ currentPage + 1 }} / {{ totalPages }} 页</span>
      <div class="page-size-selector">
        <span>每页显示：</span>
        <select v-model="localPageSize" @change="onPageSizeChange">
          <option v-for="size in pageSizeOptions" :key="size" :value="size">
            {{ size }}
          </option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface Props {
  total: number
  page: number
  size: number
  pageSizeOptions?: number[]
}

interface Emits {
  (e: 'page-change', page: number): void
  (e: 'size-change', size: number): void
}

const props = withDefaults(defineProps<Props>(), {
  pageSizeOptions: () => [10, 20, 50, 100]
})

const emit = defineEmits<Emits>()

const currentPage = ref(props.page)
const localPageSize = ref(props.size)

const totalPages = computed(() => Math.ceil(props.total / localPageSize.value))

// 计算可见的页码范围
const visiblePages = computed(() => {
  const delta = 2 // 当前页前后显示的页数
  const range = []
  const rangeWithDots = []
  let l

  for (let i = Math.max(2, currentPage.value - delta); i <= Math.min(totalPages.value - 2, currentPage.value + delta); i++) {
    range.push(i)
  }

  if (currentPage.value - delta > 2) {
    rangeWithDots.push(0, '...')
  } else {
    //rangeWithDots.push(...[...Array(currentPage.value - delta + 1).keys()])
  }

  rangeWithDots.push(...range)

  if (currentPage.value + delta < totalPages.value - 1) {
    rangeWithDots.push('...', totalPages.value - 1)
  } else {
    rangeWithDots.push(...Array.from({ length: totalPages.value - (range[range.length - 1] || 0) - 1 }, (_, i) => (range[range.length - 1] || 0) + i + 1))
  }

  return rangeWithDots.filter(page => page !== '...' || totalPages.value > 5)
})

const goToPage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    emit('page-change', page)
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    goToPage(currentPage.value + 1)
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    goToPage(currentPage.value - 1)
  }
}

const onPageSizeChange = () => {
  currentPage.value = 0 // 重置到第一页
  emit('size-change', localPageSize.value)
}

// 监听外部 props 变化
watch(() => props.page, (newPage) => {
  currentPage.value = newPage
})

watch(() => props.size, (newSize) => {
  localPageSize.value = newSize
})
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.pagination-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  font-size: 14px;
  min-width: 36px;
}

.pagination-btn:hover:not(:disabled) {
  background: #f0f0f0;
  border-color: #007bff;
}

.pagination-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 14px;
  color: #666;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-size-selector select {
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .pagination-wrapper {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pagination-controls {
    justify-content: center;
  }
  
  .pagination-info {
    justify-content: center;
    flex-direction: column;
    gap: 8px;
  }
}
</style>