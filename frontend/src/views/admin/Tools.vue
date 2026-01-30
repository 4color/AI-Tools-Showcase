<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">工具管理</h2>
        <p class="text-slate-600 mt-1">管理和编辑 AI 工具信息</p>
      </div>
      <button @click="openAdd"
              class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        添加工具
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
              placeholder="搜索工具名称或描述..."
              class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
        </div>
        <select v-model="selectedCategory"
                class="px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
        <button
            @click="loadTools"
            class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
            :disabled="loading"
        >
          {{ loading ? '加载中...' : '刷新' }}
        </button>
      </div>
    </div>

    <!-- Tools Table -->
    <div class="bg-white rounded-xl border border-slate-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 border-b border-slate-200">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">工具名称</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">分类</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">价格</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">评分</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">置顶</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
          <tr v-for="tool in filteredTools" :key="tool.id" class="hover:bg-slate-50">
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <template v-if="tool.logoUrl">
                  <img :src="apiBaseUrl+tool.logoUrl" alt="Tool Logo" class="w-10 h-10 rounded-lg">
                </template>
                <template v-else>
                  <div
                      class="w-10 h-10 rounded-lg bg-primary-100 flex items-center justify-center text-primary-600 font-semibold">
                    {{ tool.name.charAt(0) }}
                  </div>
                </template>
                <div>
                  <p class="font-medium text-slate-900 max-w-[320px] overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ tool.name }}
                  </p>
                  <p class="text-sm text-slate-500 max-w-[420px] overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ tool.description }}
                  </p>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 text-sm text-slate-600">{{ tool.category }}</td>
            <td class="px-6 py-4">
                <span class="text-xs font-medium px-2 py-1 rounded"
                      :class="{
                    'bg-emerald-100 text-emerald-700': tool.price === 'free',
                    'bg-primary-100 text-primary-700': tool.price === 'freemium',
                    'bg-amber-100 text-amber-700': tool.price === 'paid'
                  }">
                  {{ getPriceLabel(tool.price) }}
                </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-1">
                <svg class="w-4 h-4 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                  <polygon
                      points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
                <span class="text-sm font-medium text-slate-900">{{ tool.rating || '-' }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
                <span
                    class="text-xs font-medium px-2 py-1 rounded"
                    :class="(tool as any).pinned === 1 || (tool as any).pinned === true ? 'bg-violet-100 text-violet-700' : 'bg-slate-100 text-slate-600'"
                >
                  {{ (tool as any).pinned === 1 || (tool as any).pinned === true ? '是' : '否' }}
                </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <button @click="editTool(tool)"
                        class="p-2 text-primary-600 hover:bg-primary-50 rounded transition-colors">
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button @click="deleteTool(tool.id)" class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
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

      <!-- Pagination (Element Plus) -->
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

      <div v-if="!loading && filteredTools.length === 0" class="text-center py-12 text-slate-500">
        暂无数据
      </div>
      <div v-if="loading" class="text-center py-12 text-slate-400">
        加载中...
      </div>
    </div>

    <ToolFormModal
        :open="showModal"
        :mode="modalMode"
        :tool="currentTool"
        :categories="categories"
        :saving="saving"
        @close="closeModal"
        @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted, watch} from 'vue'
import {ElMessageBox, ElMessage,ElPagination} from 'element-plus'
import type {Tool} from '@/types'
import {adminToolsApi} from '@/api/admin.tools.api'
import ToolFormModal from './components/tool_FormModal.vue'

const tools = ref<Tool[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const categories = ref<string[]>([])
const loading = ref(false)
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const pageSizeOptions = [10, 20, 50, 100]

const onCurrentChange = (p: number) => {
  // el-pagination is 1-based
  page.value = Math.max(0, p - 1)
  loadTools()
}

const onSizeChange = (s: number) => {
  pageSize.value = s
  page.value = 0
  loadTools()
}

const showModal = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const currentTool = ref<Tool | null>(null)
const saving = ref(false)

//@ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

const filteredTools = computed(() => {
  let result = tools.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(t =>
        t.name.toLowerCase().includes(query) ||
        t.description.toLowerCase().includes(query)
    )
  }

  if (selectedCategory.value) {
    result = result.filter(t => t.category === selectedCategory.value)
  }

  return result
})

const getPriceLabel = (price: string) => {
  const map: Record<string, string> = {
    'free': '免费',
    'freemium': '免费增值',
    'paid': '付费'
  }
  return map[price] || price
}

const openAdd = () => {
  modalMode.value = 'add'
  currentTool.value = null
  showModal.value = true
}

const editTool = (tool: Tool) => {
  modalMode.value = 'edit'
  currentTool.value = tool
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const deleteTool = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个工具吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    // user cancelled
    return
  }

  try {
    await adminToolsApi.remove(id)
    await loadTools()
    await loadCategories()
    ElMessage.success('已删除')
  } catch (e) {
    console.error('删除工具失败:', e)
    ElMessage.error('删除失败，请稍后重试')
  }
}

const loadCategories = async () => {
  try {
    categories.value = await adminToolsApi.getCategories()
  } catch (e) {
    console.error('加载分类失败:', e)
    categories.value = []
  }
}

const loadTools = async () => {
  loading.value = true
  try {
    const pageRes = await adminToolsApi.getToolsPage({
      page: page.value,
      size: pageSize.value,
      keyword: searchQuery.value.trim() || undefined,
      category: selectedCategory.value || undefined
    })
    tools.value = (pageRes.content || []) as unknown as Tool[]
    total.value = pageRes.total || 0
  } catch (e) {
    console.error('加载工具失败:', e)
    tools.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSave = async (payload: any) => {
  saving.value = true
  try {
    await adminToolsApi.save({
      id: payload.id,
      name: payload.name,
      description: payload.description,
      introduction: payload.introduction,
      category: payload.category,
      price: payload.price,
      url: payload.url,
      logoUrl: payload.logoUrl,
      rating: payload.rating,
      pinned: payload.pinned ? 1 : 0
    } as any)
    showModal.value = false
    await loadCategories()
    await loadTools()
  } catch (e) {
    console.error('保存工具失败:', e)
  } finally {
    saving.value = false
  }
}

watch([searchQuery, selectedCategory], () => {
  page.value = 0
  loadTools()
})

onMounted(async () => {
  await loadCategories()
  await loadTools()
})
</script>
