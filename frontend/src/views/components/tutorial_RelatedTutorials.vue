<template>
  <div v-if="tutorials.length > 0" class="bg-white rounded-xl border border-slate-100 p-6">
    <h3 class="font-semibold text-slate-900 mb-4">相关教程</h3>
    <div v-if="loading" class="text-center py-4">
      <div class="w-6 h-6 border-2 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto"></div>
    </div>
    <div v-else class="space-y-4">
      <div 
        v-for="tutorial in tutorials" 
        :key="tutorial.id" 
        @click="viewTutorial(tutorial.id)" 
        class="group cursor-pointer"
      >
        <h4 class="font-medium text-slate-900 text-sm mb-1 group-hover:text-primary-600 transition-colors">
          {{ tutorial.title }}
        </h4>
        <div class="flex items-center gap-3 text-xs text-slate-500">
          <span>{{ tutorial.category }}</span>
          <span class="flex items-center gap-1">
            <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            {{ tutorial.viewCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { tutorialsApi } from '@/api/tutorial.api'
import type { Tutorial } from '@/types'

interface BackendTutorial {
  id: number
  title: string
  description: string
  content: string
  difficulty: string
  readTime?: number
  authorName?: string
  authorId?: number
  likeCount?: number
  createdAt: string
  tags?: string[]
  toolId?: number
  category?: string
  viewCount?: number
}

interface Props {
  toolId?: number
}

const props = defineProps<Props>()
const router = useRouter()

const tutorials = ref<Tutorial[]>([])
const loading = ref(false)

// 转换后端数据格式到前端格式
const transformTutorial = (backendTutorial: BackendTutorial): Tutorial => {
  const difficultyMap: Record<string, 'beginner' | 'intermediate' | 'advanced'> = {
    'BEGINNER': 'beginner',
    'INTERMEDIATE': 'intermediate',
    'ADVANCED': 'advanced'
  }
  
  const difficulty = difficultyMap[backendTutorial.difficulty?.toUpperCase()] || 'beginner'
  
  const formatDate = (dateStr: string) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
  }
  
  const readTime = backendTutorial.readTime || Math.ceil((backendTutorial.content?.length || 0) / 500)
  const duration = `${readTime} 分钟`
  
  return {
    id: backendTutorial.id,
    title: backendTutorial.title,
    description: backendTutorial.description,
    content: backendTutorial.content || '',
    difficulty,
    duration,
    tags: backendTutorial.tags || [],
    viewCount: backendTutorial.viewCount || 0,
    rating: 0,
    author: backendTutorial.authorName ? {
      id: backendTutorial.authorId || 0,
      username: backendTutorial.authorName,
      email: '',
      role: 'user',
      createdAt: ''
    } : undefined,
    readTime,
    createdAt: formatDate(backendTutorial.createdAt),
    updatedAt: '',
    category: backendTutorial.category
  }
}

// 加载相关教程
const loadRelatedTutorials = async () => {
  if (!props.toolId) {
    tutorials.value = []
    return
  }
  
  loading.value = true
  try {
    const related = (await tutorialsApi.getRelatedTutorials(props.toolId)) as unknown as BackendTutorial[]
    tutorials.value = related.slice(0, 5).map(t => transformTutorial(t))
  } catch (e) {
    console.error('加载相关教程失败:', e)
    tutorials.value = []
  } finally {
    loading.value = false
  }
}

// 监听 toolId 变化
watch(() => props.toolId, () => {
  loadRelatedTutorials()
}, { immediate: true })

onMounted(() => {
  loadRelatedTutorials()
})

const viewTutorial = (id: number) => {
  router.push(`/tutorials/${id}`)
}
</script>
