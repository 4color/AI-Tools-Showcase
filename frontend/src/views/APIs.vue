<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Page Header -->
    <div class="bg-white border-b border-slate-100 py-10">
      <div class="container-custom">
        <h1 class="text-3xl font-bold text-slate-900 mb-2">API 接口大全</h1>
        <p class="text-slate-600">探索各种实用的 API 接口</p>
      </div>
    </div>

    <div class="container-custom py-8">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- Sidebar Filters -->
        <aside class="lg:w-64 flex-shrink-0">
          <div class="bg-white rounded-xl border border-slate-100 p-6 sticky top-24 space-y-6">
            <div>
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
                  v-for="cat in categories"
                  :key="cat.id"
                  @click="selectedCategory = cat.name"
                  class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                  :class="selectedCategory === cat.name ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>{{ cat.description || cat.name }}</span>
                </button>
              </div>
            </div>

            <div class="border-t border-slate-100 pt-6">
              <h3 class="font-semibold text-slate-900 mb-4">提供商</h3>
              <div class="space-y-2">
                <button
                  @click="selectedProvider = ''"
                  class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                  :class="selectedProvider === '' ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>全部</span>
                </button>
                <button
                  v-for="provider in providers"
                  :key="provider"
                  @click="selectedProvider = selectedProvider === provider ? '' : provider"
                  class="w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors"
                  :class="selectedProvider === provider ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
                >
                  <span>{{ provider }}</span>
                </button>
              </div>
            </div>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1">
          <!-- Search -->
          <div class="mb-6">
            <div class="relative">
              <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索 API 名称或描述..."
                class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
            </div>
          </div>

          <!-- APIs Grid -->
          <div class="grid md:grid-cols-2 gap-6">
            <div
              v-for="api in apis"
              :key="api.id"
              class="bg-white rounded-xl border border-slate-100 overflow-hidden hover:shadow-lg hover:border-slate-200 transition-all duration-300 cursor-pointer group"
              @click="viewApiDetail(api)"
            >
              <div class="p-5">
                <div class="flex items-start gap-3 mb-3">
                  <template v-if="api.logoUrl || (api as any).logo">
                    <img
                      :src="apiBaseUrl + (api.logoUrl || (api as any).logo)"
                      :alt="api.name"
                      class="w-12 h-12 rounded-lg object-cover flex-shrink-0"
                    >
                  </template>
                  <template v-else>
                    <div class="w-12 h-12 rounded-lg bg-emerald-50 flex items-center justify-center text-emerald-600 flex-shrink-0 font-semibold text-lg">
                      {{ api.name ? api.name.charAt(0) : '?' }}
                    </div>
                  </template>
                  <div class="flex-1 min-w-0">
                    <h3 class="font-semibold text-slate-900 truncate group-hover:text-primary-600 transition-colors">{{ api.name }}</h3>
                    <span class="text-xs text-slate-500">{{ api.provider }}</span>
                  </div>
                </div>

                <p class="text-sm text-slate-600 line-clamp-2 mb-4">{{ api.description }}</p>

                <div class="flex items-center justify-between pt-4 border-t border-slate-100">
                  <span class="flex items-center gap-1 text-primary-600 text-sm font-medium">
                    查看详情
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
          <div v-if="apis.length === 0 && !loading" class="text-center py-16">
            <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
            <h3 class="text-lg font-semibold text-slate-900 mb-2">没有找到匹配的 API</h3>
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
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { apisApi } from '@/api/api.api'

const router = useRouter()
const loading = ref(true)
const apis = ref<any[]>([])
const searchQuery = ref('')
const selectedProvider = ref('')
const selectedCategory = ref('')
const currentPage = ref(0)
const pageSize = ref(12)
const providers = ref<string[]>([])
const categories = ref<{ id: number; name: string; description?: string }[]>([])
const total = ref(0)

const apiBaseUrl = (typeof window !== 'undefined' && (window as any).__APP_ENV__?.apiBaseUrl) || ''

const loadApis = async (resetPage = false) => {
  try {
    loading.value = true
    if (resetPage) {
      currentPage.value = 0
    }

    const filters = {
      category: selectedCategory.value || undefined,
      provider: selectedProvider.value || undefined,
      keyword: searchQuery.value.trim() || undefined
    }
    const response = await apisApi.getApisPage(currentPage.value, pageSize.value, filters)
    apis.value = response.content
    total.value = response.total
  } catch (error) {
    console.error('加载 API 失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = await apisApi.getCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
    categories.value = []
  }
}

const loadProviders = async () => {
  try {
    providers.value = await apisApi.getProviders()
  } catch (error) {
    console.error('加载提供商失败:', error)
    providers.value = []
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadApis()
}

const viewApiDetail = (api: any) => {
  router.push(`/apis/${api.id}`)
}

// 监听筛选
watch([searchQuery, selectedProvider, selectedCategory], () => {
  loadApis(true)
})

onMounted(() => {
  loadApis()
  loadCategories()
  loadProviders()
})
</script>
