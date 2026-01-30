<template>
  <div class="bg-white rounded-xl border border-slate-100 p-8">
    <h3 class="font-semibold text-slate-900 mb-6">评论 ({{ comments.length }})</h3>

    <div v-if="loading" class="text-center py-8">
      <div class="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto"></div>
    </div>

    <div v-else-if="comments.length > 0" class="space-y-4 mb-6">
      <div v-for="comment in comments" :key="comment.id" class="p-4 bg-slate-50 rounded-xl">
        <div class="flex items-start gap-3 mb-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-r from-primary-600 to-primary-500 flex items-center justify-center text-white font-medium text-sm flex-shrink-0">
            {{ comment.username?.charAt(0)?.toUpperCase() || 'U' }}
          </div>
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1">
              <span class="font-medium text-slate-900 text-sm">{{ comment.username || '匿名用户' }}</span>
              <span class="text-slate-500 text-xs">{{ formatDate(comment.createdAt) }}</span>
              <span v-if="comment.rating" class="flex items-center gap-1 text-amber-500 text-xs">
                <svg class="w-3 h-3" viewBox="0 0 24 24" fill="currentColor">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
                {{ comment.rating }}
              </span>
            </div>
            <p class="text-slate-600 text-sm leading-relaxed whitespace-pre-wrap">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="text-center py-6 text-slate-500 mb-6">
      <svg class="w-10 h-10 mx-auto text-slate-300 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
      </svg>
      暂无评论，快来发表第一条评论吧！
    </div>

    <slot name="form"></slot>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { commentApi, type Comment } from '@/api/comment.api'

interface Props {
  entityType: string
  entityId: number
}

interface Emits {
  (e: 'refresh'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const comments = ref<Comment[]>([])
const loading = ref(false)

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

// 加载评论
const loadComments = async () => {
  if (!props.entityId) return
  
  loading.value = true
  try {
    const data = await commentApi.getComments(props.entityType, props.entityId)
    comments.value = (data as unknown as Comment[])
  } catch (error: any) {
    console.error('加载评论失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听 entityId 变化，重新加载评论
watch(() => props.entityId, () => {
  loadComments()
}, { immediate: true })

onMounted(() => {
  loadComments()
})

// 暴露刷新方法供父组件调用
defineExpose({
  refresh: loadComments
})
</script>
