<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <TutorialHeader 
      :tutorial="tutorial" 
      :is-favorited="isFavorited"
      :favorite-loading="favoriteLoading"
      @toggle-favorite="toggleFavorite"
      @share="shareTutorial"
    />

    <!-- Loading State -->
    <div v-if="loading" class="container-custom py-16">
      <div class="flex items-center justify-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        <span class="ml-4 text-slate-600">加载中...</span>
      </div>
    </div>

    <!-- Content -->
    <div v-else class="container-custom py-8">
      <div class="grid lg:grid-cols-3 gap-8">
        <!-- Main Content -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Tutorial Content -->
          <TutorialContent :tutorial="tutorial" />

          <!-- Comments Section -->
          <CommCommentList 
            v-if="tutorial" 
            :entity-type="'tutorial'" 
            :entity-id="tutorial.id"
            ref="commentListRef"
          >
            <template #form>
              <CommCommentForm 
                v-if="tutorial"
                :entity-type="'tutorial'" 
                :entity-id="tutorial.id"
                @success="handleCommentSuccess"
                @error="handleCommentError"
              />
            </template>
          </CommCommentList>
        </div>

        <!-- Sidebar -->
        <div class="lg:col-span-1">
          <!-- Table of Contents -->
          <TutorialTableOfContents 
            :content="tutorial?.content"
          />

          <!-- Related Tutorials -->
          <TutorialRelatedTutorials 
            :tool-id="toolId"
          />
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <Transition name="fade">
      <div v-if="toast.show" class="fixed bottom-6 right-6 px-4 py-3 rounded-lg shadow-lg text-white text-sm font-medium z-50" :class="toast.type === 'success' ? 'bg-emerald-600' : toast.type === 'error' ? 'bg-red-600' : 'bg-slate-600'">
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { tutorialsApi } from '@/api/tutorial.api'
import { likeApi } from '@/api/like.api'
import { ElMessage } from 'element-plus'
import { redirectToLogin } from '@/utils/auth'
import type { Tutorial } from '@/types'

// 导入组件
import TutorialHeader from './components/tutorial_Header.vue'
import TutorialContent from './components/tutorial_Content.vue'
import TutorialTableOfContents from './components/tutorial_TableOfContents.vue'
import TutorialRelatedTutorials from './components/tutorial_RelatedTutorials.vue'
import CommCommentList from './components/comm_CommentList.vue'
import CommCommentForm from './components/comm_CommentForm.vue'

interface BackendTutorial {
  id: number
  title: string
  description: string
  content: string
  coverImage?: string
  difficulty: string
  readTime?: number
  viewCount?: number
  authorName?: string
  authorId?: number
  likeCount?: number
  createdAt: string
  tags?: string[]
  toolId?: number
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const tutorial = ref<Tutorial | null>(null)
const toolId = ref<number | undefined>(undefined)
const loading = ref(false)
const commentListRef = ref<InstanceType<typeof CommCommentList> | null>(null)
const isFavorited = ref(false)
const favoriteLoading = ref(false)

// Toast notification state
const toast = reactive({
  show: false,
  message: '',
  type: 'success' as 'success' | 'error' | 'warning'
})

const showToast = (message: string, type: 'success' | 'error' | 'warning' = 'success') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

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
    coverImage: backendTutorial.coverImage,
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
    updatedAt: ''
  }
}

// 加载教程详情
const loadTutorial = async () => {
  const id = Number(route.params.id)
  if (!id) {
    showToast('教程ID无效', 'error')
    return
  }
  
  loading.value = true
  try {
    // 增加浏览量
    try {
      await tutorialsApi.incrementViewCount(id)
    } catch (error) {
      console.error('Failed to increment view count:', error)
      // 浏览量增加失败不影响页面加载，只记录错误
    }
    
    // 加载教程详情
    const data = (await tutorialsApi.getTutorial(id)) as unknown as BackendTutorial
    tutorial.value = transformTutorial(data)
    toolId.value = data.toolId
    
    // 检查收藏状态（如果已登录）
    if (isLoggedIn.value) {
      try {
        const favorited = await likeApi.isLiked('tutorial', id) as unknown as boolean
        isFavorited.value = favorited
      } catch (error) {
        console.error('Failed to check favorite status:', error)
        // 收藏状态检查失败不影响页面，只记录错误
      }
    }
  } catch (error: any) {
    console.error('加载教程失败:', error)
    showToast(error?.message || '加载教程失败', 'error')
    router.push('/tutorials')
  } finally {
    loading.value = false
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

// 切换收藏状态
const toggleFavorite = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    redirectToLogin()
    return
  }
  
  if (!tutorial.value) {
    return
  }
  
  favoriteLoading.value = true
  try {
    const result = await likeApi.toggleLike('tutorial', tutorial.value.id) as unknown as boolean
    isFavorited.value = result
    ElMessage.success(result ? '收藏成功' : '已取消收藏')
  } catch (error: any) {
    console.error('Failed to toggle favorite:', error)
    if (error?.response?.status === 401) {
      ElMessage.error('请先登录')
      redirectToLogin()
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    favoriteLoading.value = false
  }
}

// 分享教程
const shareTutorial = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    showToast('链接已复制', 'success')
  } catch {
    showToast('复制失败', 'error')
  }
}

onMounted(() => {
  loadTutorial()
})
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
