<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <div class="bg-white border-b border-slate-100 py-10">
      <div class="container-custom">
        <h1 class="text-3xl font-bold text-slate-900 mb-2">学习教程</h1>
        <p class="text-slate-600">掌握 AI 工具的使用技巧和最佳实践</p>
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
                :class="selectedCategory === '' ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span>全部</span>
              </button>
              <button
                v-for="cat in tutorialCategories"
                :key="cat.id"
                @click="selectedCategory = selectedCategory === cat.name ? '' : cat.name"
                class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                :class="selectedCategory === cat.name ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span class="truncate">{{ cat.name }}</span>
              </button>
            </div>

            <div class="border-t border-slate-100 mt-6 pt-6">
            <h3 class="font-semibold text-slate-900 mb-4">难度</h3>
            <div class="space-y-2">
              <button
                @click="selectedDifficulty = ''"
                class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                :class="selectedDifficulty === '' ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span>全部</span>
              </button>
              <button
                v-for="diff in difficultyOptions"
                :key="diff.value"
                @click="selectedDifficulty = selectedDifficulty === diff.value ? '' : diff.value"
                class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                :class="selectedDifficulty === diff.value ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span>{{ diff.label }}</span>
              </button>
            </div>
            </div>

            <div class="border-t border-slate-100 mt-6 pt-6">
              <h3 class="font-semibold text-slate-900 mb-4">相关工具</h3>
              <div class="space-y-2">
                <button
                  v-for="tool in availableTools"
                  :key="tool.id"
                  @click="selectedTool = selectedTool === tool.id ? '' : tool.id"
                  class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                  :class="selectedTool === tool.id ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>{{ tool.name }}</span>
                </button>
              </div>
            </div>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1">
          <!-- Search & Sort -->
          <div class="flex flex-col sm:flex-row gap-4 mb-6">
            <div class="flex-1 relative">
              <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索教程标题或内容..."
                class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
            </div>
            <select v-model="sortBy" class="px-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
              <option value="latest">最新发布</option>
              <option value="popular">最受欢迎</option>
              <option value="views">浏览最多</option>
            </select>
            <button
              v-if="isLoggedIn"
              type="button"
              class="px-4 py-2.5 rounded-lg text-sm font-medium transition-colors border whitespace-nowrap"
              :class="onlyMyTutorials ? 'bg-primary-50 border-primary-300 text-primary-700' : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'"
              @click="toggleOnlyMyTutorials"
            >
              我的教程
            </button>
          </div>

          <!-- Tutorials Grid -->
          <div class="grid md:grid-cols-2 gap-6">
            <div
              v-for="tutorial in tutorials"
              :key="tutorial.id"
              class="bg-white rounded-xl border border-slate-100 overflow-hidden hover:shadow-lg hover:border-slate-200 transition-all duration-300 cursor-pointer group"
              @click="viewTutorial(tutorial)"
            >
              <div class="p-5">
                <div class="flex items-start gap-3 mb-3">
                  <div class="w-12 h-12 rounded-lg bg-amber-50 flex items-center justify-center text-amber-600 flex-shrink-0">
                    <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                  </div>
                  <div class="flex-1 min-w-0">
                    <h3 class="font-semibold text-slate-900 truncate group-hover:text-primary-600 transition-colors">{{ tutorial.title }}</h3>
                    <div class="flex items-center gap-2 mt-1">
                      <span 
                        class="text-xs px-2 py-0.5 rounded"
                        :class="{
                          'bg-emerald-100 text-emerald-700': tutorial.difficulty === 'Beginner',
                          'bg-amber-100 text-amber-700': tutorial.difficulty === 'Intermediate',
                          'bg-red-100 text-red-700': tutorial.difficulty === 'Advanced'
                        }"
                      >
                        {{ getDifficultyLabel(tutorial.difficulty) }}
                      </span>
                      <span class="text-xs text-slate-400">{{ tutorial.authorName }}</span>
                    </div>
                  </div>
                </div>

                <p class="text-sm text-slate-600 line-clamp-2 mb-4">{{ tutorial.description }}</p>

                <div class="flex items-center justify-between pt-4 border-t border-slate-100">
                  <div class="flex items-center gap-4 text-sm text-slate-500 flex-wrap">
                    <span v-if="tutorial.createdAt" class="flex items-center gap-1">
                      <svg class="w-4 h-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                      {{ formatDate(tutorial.createdAt) }}
                    </span>
                    <span v-if="tutorial.readTime" class="flex items-center gap-1">
                      <svg class="w-4 h-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                      {{ tutorial.readTime }} 分钟
                    </span>
                    <span v-if="tutorial.views_count != null" class="flex items-center gap-1">
                      <svg class="w-4 h-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      {{ tutorial.views_count }}
                    </span>
                  </div>
                  <span class="flex items-center gap-1 text-primary-600 text-sm font-medium">
                    开始学习
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Pagination -->
          <div v-if="total > pageSize" class="mt-8 flex items-center justify-center gap-2">
            <button
              @click="handlePageChange(currentPage - 1)"
              :disabled="currentPage === 0"
              class="px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              上一页
            </button>
            <span class="text-sm text-slate-600">
              第 {{ currentPage + 1 }} / {{ Math.ceil(total / pageSize) }} 页
            </span>
            <button
              @click="handlePageChange(currentPage + 1)"
              :disabled="currentPage >= Math.ceil(total / pageSize) - 1"
              class="px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              下一页
            </button>
          </div>

          <!-- Empty State -->
          <div v-if="tutorials.length === 0 && !loading" class="text-center py-16">
            <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
            <h3 class="text-lg font-semibold text-slate-900 mb-2">没有找到匹配的教程</h3>
            <p class="text-slate-600">试试其他搜索条件</p>
          </div>

          <!-- Loading -->
          <div v-if="loading" class="flex items-center justify-center py-16">
            <div class="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { tutorialsApi } from '@/api/tutorial.api'
import { toolsApi } from '@/api/tool.api'
import request from '@/utils/request.api'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const loading = ref(true)
const tutorials = ref<any[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedDifficulty = ref('')
const selectedTool = ref('')
const currentPage = ref(0)
const pageSize = ref(12)
const sortBy = ref('latest')
const onlyMyTutorials = ref(false)
const availableTools = ref<any[]>([])
const total = ref(0)

interface CategoryItem {
  id: number
  name: string
  type?: string
  description?: string
}
const tutorialCategories = ref<CategoryItem[]>([])

const difficultyOptions = [
  { value: 'BEGINNER', label: '入门' },
  { value: 'INTERMEDIATE', label: '进阶' },
  { value: 'ADVANCED', label: '高级' }
]

const loadTutorials = async (resetPage = false) => {
  try {
    loading.value = true
    if (resetPage) {
      currentPage.value = 0
    }

    const response = await tutorialsApi.getTutorialsPage(
      currentPage.value,
      pageSize.value,
      searchQuery.value || undefined,
      selectedDifficulty.value || undefined,
      selectedTool.value || undefined,
      selectedCategory.value || undefined,
      sortBy.value,
      onlyMyTutorials.value && isLoggedIn.value ? true : undefined
    )
    tutorials.value = response.content
    total.value = response.total
  } catch (error) {
    console.error('加载教程失败:', error)
  } finally {
    loading.value = false
  }
}

const loadTools = async () => {
  try {
    const data = await toolsApi.getTools()
    availableTools.value = data
  } catch (error) {
    console.error('加载工具失败:', error)
  }
}

const loadTutorialCategories = async () => {
  try {
    const data = await request.get<CategoryItem[]>('/categories')
    tutorialCategories.value = (data as unknown as CategoryItem[]).filter(c => c.type === 'tutorial')
  } catch (error) {
    console.error('加载教程分类失败:', error)
    tutorialCategories.value = []
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadTutorials()
}

const getDifficultyLabel = (difficulty: string) => {
  if (!difficulty) return ''
  const key = difficulty.toUpperCase()
  const labels: { [key: string]: string } = {
    'BEGINNER': '入门',
    'INTERMEDIATE': '进阶',
    'ADVANCED': '高级'
  }
  return labels[key] || difficulty
}

const formatDate = (value: string) => {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const viewTutorial = (tutorial: any) => {
  router.push(`/tutorials/${tutorial.id}`)
}

function toggleOnlyMyTutorials() {
  onlyMyTutorials.value = !onlyMyTutorials.value
  loadTutorials(true)
}

// 监听搜索查询、筛选条件和排序变化（不包含 onlyMyTutorials，避免点击「我的教程」时请求两次）
watch([searchQuery, selectedCategory, selectedDifficulty, selectedTool, sortBy], () => {
  loadTutorials(true)
}, { debounce: 300 })

onMounted(() => {
  loadTutorials()
  loadTools()
  loadTutorialCategories()
})
</script>
