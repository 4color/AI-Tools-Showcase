<template>
  <div v-if="items.length > 0" class="bg-white rounded-xl border border-slate-100 p-6 mb-6 sticky top-24 max-h-[calc(100vh-8rem)] flex flex-col">
    <h3 class="font-semibold text-slate-900 mb-4 flex-shrink-0">目录</h3>
    <nav class="space-y-1 overflow-y-auto flex-1 min-h-0">
      <a 
        v-for="(item, index) in items" 
        :key="index" 
        :href="'#' + item.id" 
        class="block text-sm text-slate-600 hover:text-primary-600 py-1.5 transition-colors border-l-2 border-transparent hover:border-primary-300"
        :style="{ paddingLeft: `${(item.level - 1) * 16 + 12}px` }"
        @click.prevent="scrollToHeading(item.id)"
      >
        {{ item.title }}
      </a>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { extractTocFromMarkdown } from '@/utils/markdown'

interface TocItem {
  id: string
  title: string
  level: number
}

interface Props {
  content?: string
}

const props = defineProps<Props>()

// 从内容中提取目录
const items = computed<TocItem[]>(() => {
  if (!props.content) return []
  return extractTocFromMarkdown(props.content)
})

// 滚动到指定标题
const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    const offset = 100 // 顶部偏移量
    const elementPosition = element.getBoundingClientRect().top
    const offsetPosition = elementPosition + window.pageYOffset - offset
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}
</script>
