<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <div class="bg-white border-b border-slate-100 py-10">
      <div class="container-custom">
        <h1 class="text-3xl font-bold text-slate-900 mb-2">AI 提示词</h1>
        <p class="text-slate-600">发现和分享各类高效的 AI 提示词，提升你的 AI 使用效率</p>
      </div>
    </div>

    <div class="container-custom py-8">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- Sidebar Filters -->
        <aside class="lg:w-64 flex-shrink-0">
          <div class="bg-white rounded-xl border border-slate-100 p-6 sticky top-24">
            <h3 class="font-semibold text-slate-900 mb-4">分类</h3>
            <div class="space-y-2">
              <button
                @click="selectedCategory = ''"
                class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                :class="!selectedCategory ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span>全部分类</span>
                <span class="text-xs text-slate-400">{{ totalCount }}</span>
              </button>
              <button
                v-for="cat in categories"
                :key="cat.id"
                @click="selectedCategory = cat.name"
                class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                :class="selectedCategory === cat.name ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span class="flex items-center gap-2">
                  <span v-html="cat.icon" class="w-4 h-4"></span>
                  {{ cat.description || cat.name }}
                </span>
                <span class="text-xs text-slate-400">{{ getCategoryCount(cat.name) }}</span>
              </button>
            </div>

            <div class="border-t border-slate-100 mt-6 pt-6">
              <h3 class="font-semibold text-slate-900 mb-4">热门标签</h3>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="tag in popularTags"
                  :key="tag"
                  @click="toggleTag(tag)"
                  class="px-2 py-1 text-xs rounded-full transition-colors"
                  :class="selectedTags.includes(tag) ? 'bg-primary-100 text-primary-700' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
                >
                  {{ tag }}
                </button>
              </div>
            </div>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1">
          <!-- Loading State -->
          <div v-if="loading" class="flex items-center justify-center py-16">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
            <span class="ml-4 text-slate-600">加载中...</span>
          </div>

          <!-- Search & Sort -->
          <div v-else class="flex flex-col sm:flex-row gap-4 mb-6">
            <div class="flex-1 relative">
              <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索提示词..."
                class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
            </div>
            <select v-model="sortBy" class="px-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
              <option value="latest">最新发布</option>
              <option value="popular">最受欢迎</option>
              <option value="views">浏览最多</option>
            </select>
          </div>

          <!-- Prompts Grid -->
          <div v-if="!loading" class="grid md:grid-cols-2 gap-6">
            <div
              v-for="prompt in filteredPrompts"
              :key="prompt.id"
              class="bg-white rounded-xl border border-slate-100 overflow-hidden hover:shadow-lg hover:border-slate-200 transition-all duration-300 cursor-pointer group"
              @click="router.push(`/prompts/${prompt.id}`)"
            >
              <!-- Thumbnail / Banner -->
              <div class="h-40 bg-gradient-to-br relative overflow-hidden" :class="prompt.coverImage ? '' : getCategoryGradient(prompt.category)">
                <template v-if="prompt.coverImage">
                  <img :src="apiBaseUrl + prompt.coverImage" class="h-full w-full object-cover" alt="">
                  <div class="absolute inset-0 bg-black/20" />
                </template>
                <div v-else class="h-full flex items-center justify-center">
                  <div v-html="getCategoryIcon(prompt.category)" class="w-16 h-16 text-white/40"></div>
                </div>
                <!-- Category Badge -->
                <div class="absolute top-3 left-3 px-2 py-1 bg-white/90 backdrop-blur-sm rounded-lg text-xs font-medium text-slate-700">
                  {{ getCategoryLabel(prompt.category) }}
                </div>
                <!-- Copy Button -->
                <button
                  @click.stop="copyPrompt(prompt)"
                  class="absolute top-3 right-3 p-2 bg-white/90 backdrop-blur-sm rounded-lg text-slate-600 hover:text-primary-600 transition-colors"
                  title="复制提示词"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/></svg>
                </button>
              </div>

              <div class="p-5">
                <!-- Title -->
                <h3 class="font-semibold text-slate-900 mb-2 group-hover:text-primary-600 transition-colors">{{ prompt.title }}</h3>
                <!-- Description -->
                <p class="text-slate-600 text-sm line-clamp-2 mb-4">{{ prompt.description }}</p>

                <!-- Prompt Content Preview -->
                <div class="bg-slate-50 rounded-lg p-3 mb-4 text-xs text-slate-600 font-mono line-clamp-3">
                  {{ prompt.content }}
                </div>

                <!-- Tags -->
                <div class="flex flex-wrap gap-1 mb-4">
                  <span v-for="tag in prompt.tags?.slice(0, 3)" :key="tag" class="px-2 py-0.5 bg-slate-100 rounded text-xs text-slate-600">
                    {{ tag }}
                  </span>
                </div>

                <!-- Footer -->
                <div class="flex items-center justify-between pt-4 border-t border-slate-100">
                  <div class="flex items-center gap-2">
                    <div class="w-6 h-6 rounded-full bg-gradient-to-r from-primary-600 to-primary-500 flex items-center justify-center text-white text-xs font-medium">
                      {{ prompt.creator.username.charAt(0) }}
                    </div>
                    <span class="text-sm text-slate-500">{{ prompt.creator.username }}</span>
                  </div>
                  <div class="flex items-center gap-4 text-sm text-slate-500">
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
                      {{ prompt.likes }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      {{ prompt.views }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-if="!loading && filteredPrompts.length === 0" class="text-center py-16">
            <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            <h3 class="text-lg font-semibold text-slate-900 mb-2">暂无相关提示词</h3>
            <p class="text-slate-600">试试其他搜索条件</p>
          </div>
        </main>
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
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { promptsApi } from '@/api/prompt.api'
import type { Prompt } from '@/types'

const router = useRouter()
const apiBaseUrl = (typeof window !== 'undefined' && (window as any).__APP_ENV__?.apiBaseUrl) || ''
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedTags = ref<string[]>([])
const sortBy = ref('latest')
const loading = ref(false)

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

interface CategoryItem {
  id: number
  name: string
  type?: string
  description?: string
  icon?: string
}

const categories = ref<CategoryItem[]>([])
const popularTags = ref<string[]>([])

const prompts = ref<Prompt[]>([])
/** 全部分类时的总数（无分类筛选时的 total） */
const totalCount = ref(0)

const categoryIcons: Record<string, string> = {
  text: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>',
  image: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>',
  video: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2" ry="2"/></svg>',
  code: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>'
}

// 加载分类（按 type=prompt 过滤）
const loadCategories = async () => {
  try {
    const data = await promptsApi.getCategories() as unknown as CategoryItem[]
    categories.value = (data || [])
      .filter(c => c.type === 'prompt')
      .map(c => ({
        ...c,
        icon: categoryIcons[c.name] || categoryIcons.text
      }))
  } catch (error) {
    console.error('加载分类失败:', error)
    categories.value = []
  }
}

// 加载热门标签
const loadPopularTags = async () => {
  try {
    const data = await promptsApi.getTags({ type: 'prompt', namesOnly: true }) as unknown as string[]
    popularTags.value = (data || []).slice(0, 10)
  } catch (error) {
    console.error('加载标签失败:', error)
    popularTags.value = []
  }
}

// 加载提示词列表（与后台同一后端接口）
const loadPrompts = async () => {
  loading.value = true
  try {
    const pageRes = await promptsApi.getPromptsPage({
      page: 0,
      size: 200,
      query: searchQuery.value.trim() || undefined,
      category: selectedCategory.value || undefined,
      tags: selectedTags.value.length > 0 ? selectedTags.value.join(',') : undefined,
      sortBy: sortBy.value
    }) as unknown as { content: Prompt[]; total: number }
    const data = pageRes?.content ?? []
    prompts.value = data.map(transformPrompt)
    if (!selectedCategory.value) totalCount.value = pageRes?.total ?? 0
  } catch (error) {
    console.error('加载提示词失败:', error)
    showToast('加载提示词失败', 'error')
  } finally {
    loading.value = false
  }
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
    coverImage: backendPrompt.coverImage || (backendPrompt as any).cover_image,
    usageGuide: backendPrompt.usageGuide ?? (backendPrompt as any).usage_guide,
    effectImage: backendPrompt.effectImage ?? (backendPrompt as any).effect_image,
    createdAt: backendPrompt.createdAt ? new Date(backendPrompt.createdAt).toISOString().split('T')[0] : '',
    updatedAt: backendPrompt.updatedAt || ''
  }
}

// 监听搜索/分类/标签变化，重新加载数据（避免重复 watch 导致多次请求）
watch([searchQuery, selectedCategory, selectedTags], () => {
  loadPrompts()
})

onMounted(() => {
  loadCategories()
  loadPopularTags()
  loadPrompts()
})

const filteredPrompts = computed(() => {
  let result = prompts.value

  // Sort
  switch (sortBy.value) {
    case 'popular':
      result = [...result].sort((a, b) => b.likes - a.likes)
      break
    case 'views':
      result = [...result].sort((a, b) => b.views - a.views)
      break
    default:
      result = [...result].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  return result
})

const getCategoryCount = (category: string) => {
  if (!category) return prompts.value.length
  return prompts.value.filter(p => p.category === category).length
}

const getCategoryLabel = (category: string) => {
  const cat = categories.value.find(c => c.name === category)
  return cat?.description || cat?.name || category
}

const getCategoryGradient = (category: string) => {
  const gradients: Record<string, string> = {
    text: 'from-primary-600 to-violet-600',
    image: 'from-pink-500 to-rose-500',
    video: 'from-cyan-500 to-blue-500',
    code: 'from-emerald-500 to-teal-500'
  }
  return gradients[category] || 'from-slate-500 to-slate-600'
}

const getCategoryIcon = (category: string) => {
  const cat = categories.value.find(c => c.name === category)
  return cat?.icon || categoryIcons.text
}

const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

const viewPrompt = (id: number) => {
  router.push(`/prompts/${id}`)
}

const copyPrompt = async (prompt: Prompt) => {
  try {
    await navigator.clipboard.writeText(prompt.content)
    showToast('提示词已复制到剪贴板', 'success')
  } catch {
    showToast('复制失败', 'error')
  }
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
