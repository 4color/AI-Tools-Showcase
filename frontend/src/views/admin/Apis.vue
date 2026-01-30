<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">API 管理</h2>
        <p class="text-slate-600 mt-1">管理和编辑 API 信息</p>
      </div>
      <button @click="openAdd"
              class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        添加 API
      </button>
    </div>

    <!-- Search and Filters -->
    <div class="bg-white rounded-xl border border-slate-100 p-4 mb-6">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1 relative">
          <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
          <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索 API 名称、提供商或描述..."
              class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
        </div>
        <select v-model="selectedCategory"
                class="px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
        <select v-model="selectedProvider"
                class="px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
          <option value="">全部提供商</option>
          <option v-for="p in providers" :key="p" :value="p">{{ p }}</option>
        </select>
        <button
            @click="loadApis"
            class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
            :disabled="loading"
        >
          {{ loading ? '加载中...' : '查询' }}
        </button>
      </div>
    </div>

    <!-- APIs Table -->
    <div class="bg-white rounded-xl border border-slate-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 border-b border-slate-200">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">API 名称</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">提供商</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">分类</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">定价</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="api in apis" :key="api.id" class="hover:bg-slate-50">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <template v-if="(api as any).logoUrl">
                    <img :src="apiBaseUrl + (api as any).logoUrl" alt="API Logo" class="w-10 h-10 rounded-lg object-cover">
                  </template>
                  <template v-else>
                    <div class="w-10 h-10 rounded-lg bg-amber-100 flex items-center justify-center text-amber-600 font-semibold">
                      {{ api.name.charAt(0) }}
                    </div>
                  </template>
                  <div>
                    <p class="font-medium text-slate-900">{{ api.name }}</p>
                    <p class="text-sm text-slate-500 line-clamp-1 max-w-[280px]">{{ api.description }}</p>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">{{ api.provider }}</td>
              <td class="px-6 py-4 text-sm text-slate-600">{{ api.category || '—' }}</td>
              <td class="px-6 py-4">
                <span class="text-xs font-medium px-2 py-1 rounded"
                  :class="{
                    'bg-emerald-100 text-emerald-700': api.pricing === 'free',
                    'bg-primary-100 text-primary-700': api.pricing === 'freemium',
                    'bg-amber-100 text-amber-700': api.pricing === 'paid'
                  }">
                  {{ getPricingLabel(api.pricing) }}
                </span>
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-2">
                  <button @click="editApi(api)" class="p-2 text-primary-600 hover:bg-primary-50 rounded transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button @click="deleteApi(api.id)" class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-end">
        <ElPagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="page + 1"
          :page-size="pageSize"
          :page-sizes="pageSizeOptions"
          @current-change="onCurrentChange"
          @size-change="onSizeChange"
        />
      </div>

      <div v-if="!loading && apis.length === 0" class="text-center py-12 text-slate-500">
        暂无数据
      </div>
      <div v-if="loading" class="text-center py-12 text-slate-400">
        加载中...
      </div>
    </div>

    <ApiFormModal
      :open="showModal"
      :mode="modalMode"
      :api="currentApi"
      :categories="categories"
      :providers="providers"
      :saving="saving"
      @close="closeModal"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessageBox, ElMessage, ElPagination } from 'element-plus'
import type { ApiInfo } from '@/types'
import { adminApisApi } from '@/api/admin.apis.api'
import ApiFormModal from './components/api_FormModal.vue'

const apis = ref<ApiInfo[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedProvider = ref('')
const categories = ref<string[]>([])
const providers = ref<string[]>([])
const loading = ref(false)
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const pageSizeOptions = [10, 20, 50, 100]

// @ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl || ''

const onCurrentChange = (p: number) => {
  page.value = Math.max(0, p - 1)
  loadApis()
}

const onSizeChange = (s: number) => {
  pageSize.value = s
  page.value = 0
  loadApis()
}

const showModal = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const currentApi = ref<ApiInfo | null>(null)
const saving = ref(false)

const getPricingLabel = (pricing: string) => {
  const map: Record<string, string> = {
    'free': '免费',
    'freemium': '免费增值',
    'paid': '付费'
  }
  return map[pricing] || pricing
}

const openAdd = () => {
  modalMode.value = 'add'
  currentApi.value = null
  showModal.value = true
}

const editApi = (api: ApiInfo) => {
  modalMode.value = 'edit'
  currentApi.value = api
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const deleteApi = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个 API 吗？删除后其下模型数据将一并删除。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await adminApisApi.remove(id)
    await loadApis()
    await loadCategories()
    ElMessage.success('已删除')
  } catch (e) {
    console.error('删除 API 失败:', e)
    ElMessage.error('删除失败，请稍后重试')
  }
}

const loadCategories = async () => {
  try {
    categories.value = await adminApisApi.getCategories()
  } catch (e) {
    console.error('加载分类失败:', e)
    categories.value = []
  }
}

const loadProviders = async () => {
  try {
    providers.value = await adminApisApi.getProviders()
  } catch (e) {
    console.error('加载提供商失败:', e)
    providers.value = []
  }
}

const loadApis = async () => {
  loading.value = true
  try {
    const pageRes = await adminApisApi.getApisPage({
      page: page.value,
      size: pageSize.value,
      keyword: searchQuery.value.trim() || undefined,
      category: selectedCategory.value || undefined,
      provider: selectedProvider.value || undefined
    })
    apis.value = (pageRes.content || []) as ApiInfo[]
    total.value = pageRes.total || 0
  } catch (e) {
    console.error('加载 API 列表失败:', e)
    apis.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSave = async (payload: any) => {
  saving.value = true
  try {
    await adminApisApi.save({
      id: payload.id,
      name: payload.name,
      provider: payload.provider,
      description: payload.description,
      documentation: payload.documentation || undefined,
      pricing: payload.pricing,
      category: payload.category || undefined,
      baseUrl: payload.baseUrl || undefined,
      apiKey: payload.apiKey || undefined,
      logoUrl: payload.logoUrl || undefined,
      defaultImageProvider: payload.defaultImageProvider
    } as any)
    showModal.value = false
    await loadCategories()
    await loadProviders()
    await loadApis()
    ElMessage.success('保存成功')
  } catch (e) {
    console.error('保存 API 失败:', e)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

watch([selectedCategory, selectedProvider], () => {
  page.value = 0
  loadApis()
})

onMounted(async () => {
  await loadCategories()
  await loadProviders()
  await loadApis()
})
</script>
