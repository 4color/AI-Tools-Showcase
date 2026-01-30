<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <div class="bg-white border-b border-slate-100 py-10">
      <div class="container-custom">
        <h1 class="text-3xl font-bold text-slate-900 mb-2">AI 工具集合</h1>
        <p class="text-slate-600">发现最优秀的 AI 工具，提升工作效率</p>
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
                <span class="text-xs text-slate-400">{{ total }}</span>
              </button>
              <button
                  v-for="cat in categories"
                  :key="cat"
                  @click="selectedCategory = cat"
                  class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                  :class="selectedCategory === cat ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
              >
                <span>{{ cat }}</span>
              </button>
            </div>

            <div class="border-t border-slate-100 mt-6 pt-6">
              <h3 class="font-semibold text-slate-900 mb-4">价格类型</h3>
              <div class="space-y-2">
                <button
                    @click="selectedPrice = ''"
                    class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                    :class="selectedPrice === '' ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>全部</span>
                </button>
                <button
                    v-for="price in priceOptions"
                    :key="price.value"
                    @click="selectedPrice = selectedPrice === price.value ? '' : price.value"
                    class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                    :class="selectedPrice === price.value ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>{{ price.label }}</span>
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
              <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24"
                   fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
              <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="搜索工具名称、描述或分类..."
                  class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
            </div>
            <select v-model="sortOrder"
                    class="px-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
              <option value="default">默认排序</option>
              <option value="rating">按评分排序</option>
              <option value="name">按名称排序</option>
              <option value="category">按分类排序</option>
              <option value="price">按价格排序</option>
            </select>
            <button
              v-if="isLoggedIn"
              type="button"
              class="px-4 py-2.5 rounded-lg text-sm font-medium transition-colors border whitespace-nowrap"
              :class="onlyMyTools ? 'bg-primary-50 border-primary-300 text-primary-700' : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'"
              @click="toggleOnlyMyTools"
            >
              我的工具
            </button>
          </div>

          <!-- Tools Grid -->
          <div class="grid md:grid-cols-2 gap-6">
            <div
                v-for="tool in tools"
                :key="tool.id"
                class="bg-white rounded-xl border border-slate-100 overflow-hidden hover:shadow-lg hover:border-slate-200 transition-all duration-300 cursor-pointer group"
                @click="viewToolDetail(tool)"
            >
              <div class="p-5">
                <div class="flex items-start gap-3 mb-3">
                  <template v-if="tool.logoUrl">
                    <img :src="apiBaseUrl+tool.logoUrl" alt="Tool Logo" class="w-12 h-12 rounded-lg">
                  </template>
                  <template v-else>
                    <div
                        class="w-12 h-12 rounded-lg bg-primary-50 flex items-center justify-center text-primary-600 font-bold text-lg flex-shrink-0">
                      {{ tool.name.charAt(0) }}
                    </div>
                  </template>
                  <div class="flex-1 min-w-0">
                    <h3 class="font-semibold text-slate-900 truncate group-hover:text-primary-600 transition-colors">
                      {{ tool.name }}</h3>
                    <span class="text-xs text-slate-500">{{ tool.category }}</span>
                  </div>
                  <div class="flex items-center gap-1 text-amber-500 text-sm font-medium">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                      <polygon
                          points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                    </svg>
                    {{ formatRating(tool.rating) }}
                  </div>
                </div>

                <p class="text-sm text-slate-600 line-clamp-2 mb-4">{{ tool.description }}</p>

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
              第 {{ currentPage + 1 }} / {{ Math.ceil(total / pageSize) }} 页 (共 {{ total }} 条)
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
          <div v-if="tools.length === 0 && !loading" class="text-center py-16">
            <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="1.5">
              <rect x="2" y="6" width="20" height="12" rx="2"/>
              <path d="M12 12v4"/>
              <path d="M8 14h8"/>
            </svg>
            <h3 class="text-lg font-semibold text-slate-900 mb-2">没有找到匹配的工具</h3>
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
import { toolsApi } from '@/api/tool.api'
import { getToolCategories } from '@/api/category.api'
import type { Tool, PageResponse } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const loading = ref(true)
const tools = ref<Tool[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedPrice = ref('')
const currentPage = ref(0)
const pageSize = ref(12)
const sortOrder = ref<'default' | 'rating' | 'popular' | 'name' | 'category' | 'price' | 'created'>('default')
const onlyMyTools = ref(false)
const categories = ref<string[]>([])
const total = ref(0)
// @ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl

const priceOptions = [
  {value: 'free', label: '免费'},
  {value: 'freemium', label: '免费增值'},
  {value: 'paid', label: '付费'}
]

const loadTools = async (resetPage = false) => {
  try {
    loading.value = true
    if (resetPage) {
      currentPage.value = 0
    }

    const filters: Record<string, unknown> = {
      category: selectedCategory.value || undefined,
      keyword: searchQuery.value || undefined,
      price: selectedPrice.value || undefined,
      sort: sortOrder.value || undefined,
      order: 'asc'
    }
    if (onlyMyTools.value && isLoggedIn.value) {
      filters.onlyLikedByMe = true
    }

    const response = (await toolsApi.getTools(filters, {
      page: currentPage.value,
      size: pageSize.value
    })) as unknown as PageResponse<Tool>;
    tools.value = response.content;
    total.value = response.total;
  } catch (error) {
    console.error('加载工具失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const data = await getToolCategories()
    categories.value = data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadTools()
}

const getPriceLabel = (price: string) => {
  const labels: { [key: string]: string } = {
    'free': '免费',
    'paid': '付费',
    'freemium': '免费增值'
  }
  return labels[price.toLowerCase()] || price
}

const formatRating = (rating: number | undefined): string => {
  if (rating === undefined || rating === null) return '0.0'
  return Number(rating).toFixed(1)
}

const viewToolDetail = (tool: Tool) => {
  router.push(`/tools/${tool.id}`)
}

function toggleOnlyMyTools() {
  onlyMyTools.value = !onlyMyTools.value
  loadTools(true)
}

const debouncedLoadTools = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    loadTools(true)
  }, 300) as unknown as number
}

watch([searchQuery, selectedCategory, selectedPrice, sortOrder], debouncedLoadTools)

let debounceTimer: number | null = null

onMounted(() => {
  loadTools()
  loadCategories()
})
</script>
