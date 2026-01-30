<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <div class="bg-white border-b border-slate-100 py-8">
      <div class="container-custom">
        <!-- Back Navigation -->
        <router-link to="/prompts" class="inline-flex items-center gap-2 text-slate-600 hover:text-primary-600 mb-4 transition-colors text-sm">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
          返回提示词列表
        </router-link>

        <!-- Breadcrumb -->
        <div class="flex items-center gap-2 text-sm text-slate-500 mb-3">
          <span class="text-primary-600 font-medium">{{ getCategoryLabel(prompt?.category || '') }}</span>
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          <span>提示词详情</span>
        </div>

        <h1 v-if="prompt" class="text-3xl md:text-4xl font-bold text-slate-900 mb-4 leading-tight">{{ prompt.title }}</h1>
        <p v-if="prompt" class="text-slate-600 text-lg mb-6">{{ prompt.description }}</p>

        <!-- Meta Info -->
        <div v-if="prompt" class="flex flex-wrap items-center gap-6 text-sm">
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-full bg-gradient-to-r from-primary-600 to-primary-500 flex items-center justify-center text-white font-medium text-sm">
              {{ prompt.creator.username.charAt(0) }}
            </div>
            <span class="text-slate-700 font-medium">{{ prompt.creator.username }}</span>
          </div>

          <div class="flex items-center gap-2 text-slate-500">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
            <span>{{ prompt.likes }} 点赞</span>
          </div>

          <div class="flex items-center gap-2 text-slate-500">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            <span>{{ prompt.views.toLocaleString() }} 浏览</span>
          </div>

          <!-- Favorite Button (likes 表，entityType=prompt) -->
          <button
            @click="toggleFavorite"
            :disabled="favoriteLoading"
            class="inline-flex items-center gap-2 px-4 py-1.5 rounded-lg font-medium transition-colors"
            :class="isFavorited ? 'bg-primary-100 text-primary-700 hover:bg-primary-200' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'"
          >
            <svg
              v-if="!favoriteLoading"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              :fill="isFavorited ? 'currentColor' : 'none'"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <svg
              v-else
              class="w-4 h-4 animate-spin"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"/>
            </svg>
            {{ favoriteLoading ? '处理中...' : (isFavorited ? '已收藏' : '收藏') }}
          </button>

          <span class="text-slate-400">|</span>
          <span class="text-slate-500">{{ prompt.createdAt }}</span>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div class="container-custom py-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        <span class="ml-4 text-slate-600">加载中...</span>
      </div>

      <div v-else class="grid lg:grid-cols-3 gap-8">
        <!-- Main Content -->
        <div class="lg:col-span-2 space-y-6">
          <!-- 生成效果图（在提示词上方） -->
          <div v-if="prompt?.effectImage" class="bg-white rounded-xl border border-slate-100 p-4 overflow-hidden">
            <h2 class="text-lg font-semibold text-slate-900 mb-3">生成效果图</h2>
            <img
              :src="apiBaseUrl + prompt.effectImage"
              class="w-full rounded-lg object-contain max-h-[420px] bg-slate-50"
              alt="效果图"
            />
          </div>

          <!-- Prompt Content -->
          <div class="bg-white rounded-xl border border-slate-100 p-8">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-semibold text-slate-900">提示词内容</h2>
              <button
                @click="copyPrompt"
                class="inline-flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors"
              >
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/></svg>
                {{ copied ? '已复制' : '复制提示词' }}
              </button>
            </div>

            <div v-if="prompt" class="bg-slate-900 rounded-lg p-6 text-slate-100 font-mono text-sm leading-relaxed overflow-x-auto">
              <pre class="whitespace-pre-wrap">{{ prompt.content }}</pre>
            </div>
          </div>

          <!-- 使用说明（Markdown） -->
          <div v-if="prompt?.usageGuide" class="bg-white rounded-xl border border-slate-100 p-8">
            <h2 class="text-xl font-semibold text-slate-900 mb-4">使用说明</h2>
            <div class="prose prose-slate max-w-none">
              <v-md-preview :text="prompt.usageGuide" />
            </div>
          </div>

          <!-- Usage Tips -->
          <div class="bg-white rounded-xl border border-slate-100 p-8">
            <h2 class="text-xl font-semibold text-slate-900 mb-4">使用建议</h2>
            <div class="space-y-4">
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 rounded-full bg-primary-100 text-primary-600 flex items-center justify-center flex-shrink-0 font-semibold text-sm">1</div>
                <div>
                  <h3 class="font-medium text-slate-900 mb-1">替换占位符</h3>
                  <p class="text-slate-600 text-sm">将提示词中的 [主题]、[受众] 等占位符替换为你的实际需求</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 rounded-full bg-primary-100 text-primary-600 flex items-center justify-center flex-shrink-0 font-semibold text-sm">2</div>
                <div>
                  <h3 class="font-medium text-slate-900 mb-1">调整参数</h3>
                  <p class="text-slate-600 text-sm">根据需要调整提示词中的参数，如长度、风格、格式等</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 rounded-full bg-primary-100 text-primary-600 flex items-center justify-center flex-shrink-0 font-semibold text-sm">3</div>
                <div>
                  <h3 class="font-medium text-slate-900 mb-1">迭代优化</h3>
                  <p class="text-slate-600 text-sm">根据输出结果调整提示词，逐步优化效果</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Examples -->
          <div v-if="prompt?.examples && prompt.examples.length > 0" class="bg-white rounded-xl border border-slate-100 p-8">
            <h2 class="text-xl font-semibold text-slate-900 mb-4">使用示例</h2>
            <div class="space-y-4">
              <div v-for="(example, index) in prompt.examples" :key="index" class="bg-slate-50 rounded-lg p-4">
                <p class="text-slate-600 text-sm">{{ example }}</p>
              </div>
            </div>
          </div>

          <!-- Tags -->
          <div v-if="prompt" class="bg-white rounded-xl border border-slate-100 p-6">
            <h3 class="font-semibold text-slate-900 mb-4">标签</h3>
            <div class="flex flex-wrap gap-2">
              <span v-for="tag in prompt.tags" :key="tag" class="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-sm">{{ tag }}</span>
            </div>
          </div>

          <!-- Comments Section -->
          <CommCommentList 
            v-if="prompt" 
            :entity-type="'prompt'" 
            :entity-id="prompt.id"
            ref="commentListRef"
          >
            <template #form>
              <CommCommentForm 
                v-if="prompt"
                :entity-type="'prompt'" 
                :entity-id="prompt.id"
                @success="handleCommentSuccess"
                @error="handleCommentError"
              />
            </template>
          </CommCommentList>
        </div>

        <!-- Sidebar -->
        <div class="lg:col-span-1">
          <!-- Related Prompts -->
          <div v-if="relatedPrompts.length > 0" class="bg-white rounded-xl border border-slate-100 p-6 sticky top-24">
            <h3 class="font-semibold text-slate-900 mb-4">相关提示词</h3>
            <div class="space-y-4">
              <div v-for="related in relatedPrompts" :key="related.id" @click="viewPrompt(related.id)" class="group cursor-pointer p-3 -mx-3 rounded-lg hover:bg-slate-50 transition-colors">
                <h4 class="font-medium text-slate-900 text-sm mb-1 group-hover:text-primary-600 transition-colors">{{ related.title }}</h4>
                <div class="flex items-center gap-2 text-xs text-slate-500">
                  <span class="px-2 py-0.5 bg-slate-100 rounded">{{ getCategoryLabel(related.category) }}</span>
                  <span class="flex items-center gap-1">
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
                    {{ related.likes }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <Transition name="fade">
      <div v-if="toast.show" class="fixed bottom-6 right-6 px-4 py-3 rounded-lg shadow-lg text-white text-sm font-medium z-50" :class="toast.type === 'success' ? 'bg-emerald-600' : 'bg-slate-600'">
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { promptsApi } from '@/api/prompt.api'
import { likeApi } from '@/api/like.api'
import { redirectToLogin } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'
import type { Prompt } from '@/types'
import CommCommentList from './components/comm_CommentList.vue'
import CommCommentForm from './components/comm_CommentForm.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const apiBaseUrl = (typeof window !== 'undefined' && (window as any).__APP_ENV__?.apiBaseUrl) || ''
const isLoggedIn = computed(() => userStore.isLoggedIn)
const prompt = ref<Prompt | null>(null)
const relatedPrompts = ref<Prompt[]>([])
const copied = ref(false)
const loading = ref(false)
const commentListRef = ref<InstanceType<typeof CommCommentList> | null>(null)
const isFavorited = ref(false)
const favoriteLoading = ref(false)

const toast = reactive({
  show: false,
  message: '',
  type: 'success' as 'success' | 'error'
})

const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const categories = [
  { value: 'text', label: '文本生成' },
  { value: 'image', label: '图像生成' },
  { value: 'video', label: '视频生成' },
  { value: 'code', label: '代码编程' }
]

const getCategoryLabel = (category: string) => {
  const cat = categories.find(c => c.value === category)
  return cat?.label || category
}


// 转换后端数据格式到前端格式
const transformPrompt = (backendPrompt: any): Prompt => {
  return {
    id: backendPrompt.id,
    title: backendPrompt.title,
    content: backendPrompt.content,
    description: backendPrompt.description || '',
    category: backendPrompt.category,
    creator: backendPrompt.creator ? {
      id: backendPrompt.creator.id,
      username: backendPrompt.creator.username,
      avatar: backendPrompt.creator.avatarUrl
    } : { id: 0, username: '未知用户' },
    tags: backendPrompt.tags || [],
    likes: backendPrompt.likes || 0,
    views: backendPrompt.views || 0,
    commentCount: backendPrompt.commentCount || 0,
    usageGuide: backendPrompt.usageGuide ?? (backendPrompt as any).usage_guide,
    effectImage: backendPrompt.effectImage ?? (backendPrompt as any).effect_image,
    createdAt: backendPrompt.createdAt ? new Date(backendPrompt.createdAt).toISOString().split('T')[0] : '',
    updatedAt: backendPrompt.updatedAt || ''
  }
}

// 加载提示词详情
const loadPrompt = async () => {
  const id = Number(route.params.id)
  if (!id) {
    showToast('提示词ID无效', 'error')
    router.push('/prompts')
    return
  }

  loading.value = true
  try {
    // 增加浏览量
    try {
      await promptsApi.incrementViewCount(id)
    } catch (error) {
      console.error('Failed to increment view count:', error)
    }

    // 加载提示词详情
    const data = await promptsApi.getPrompt(id) as unknown as any
    prompt.value = transformPrompt(data)

    // 加载收藏状态
    if (isLoggedIn.value && prompt.value?.id) {
      try {
        isFavorited.value = await likeApi.isLiked('prompt', prompt.value.id)
      } catch (error) {
        console.error('Failed to load favorite status:', error)
      }
    } else {
      isFavorited.value = false
    }


    // 加载相关提示词
    try {
      const relatedData = await promptsApi.getRelatedPrompts(id, 5) as unknown as any[]
      relatedPrompts.value = relatedData.map(transformPrompt)
    } catch (error) {
      console.error('Failed to load related prompts:', error)
      relatedPrompts.value = []
    }
  } catch (error: any) {
    console.error('加载提示词失败:', error)
    showToast(error?.message || '加载提示词失败', 'error')
    router.push('/prompts')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPrompt()
})

const viewPrompt = (id: number) => router.push(`/prompts/${id}`)

const toggleFavorite = async () => {
  if (!prompt.value) return
  if (!isLoggedIn.value) {
    redirectToLogin()
    return
  }
  favoriteLoading.value = true
  try {
    const liked = await likeApi.toggleLike('prompt', prompt.value.id)
    // toggleLike 返回 true=liked / false=unliked
    if (liked) {
      isFavorited.value = true
      prompt.value.likes = (prompt.value.likes || 0) + 1
      ElMessage.success('已收藏')
    } else {
      isFavorited.value = false
      prompt.value.likes = Math.max(0, (prompt.value.likes || 0) - 1)
      ElMessage.success('已取消收藏')
    }
  } catch (error) {
    console.error('Failed to toggle favorite:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    favoriteLoading.value = false
  }
}

const copyPrompt = async () => {
  if (!prompt.value) return
  try {
    await navigator.clipboard.writeText(prompt.value.content)
    copied.value = true
    showToast('提示词已复制到剪贴板', 'success')
    setTimeout(() => { copied.value = false }, 3000)
  } catch {
    showToast('复制失败', 'error')
  }
}

// 处理评论成功
const handleCommentSuccess = () => {
  showToast('评论发布成功', 'success')
  // 刷新评论列表
  commentListRef.value?.refresh()
}

// 处理评论错误
const handleCommentError = (message: string) => {
  showToast(message, 'error')
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
