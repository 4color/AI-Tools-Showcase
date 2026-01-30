<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">提示词管理</h2>
        <p class="text-slate-600 mt-1">管理和编辑提示词内容</p>
      </div>
      <button
        @click="openAdd"
        class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
      >
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19" />
          <line x1="5" y1="12" x2="19" y2="12" />
        </svg>
        添加提示词
      </button>
    </div>

    <!-- Search and Filters -->
    <div class="bg-white rounded-xl border border-slate-100 p-4 mb-6">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1 relative">
          <svg
            class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索提示词标题或内容..."
            class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
        </div>
        <select
          v-model="selectedCategory"
          class="px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
        >
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
      </div>
    </div>

    <!-- Prompts Table -->
    <div class="bg-white rounded-xl border border-slate-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 border-b border-slate-200">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">标题</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">分类</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">点赞</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">浏览量</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">创建时间</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="prompt in prompts" :key="prompt.id" class="hover:bg-slate-50">
              <td class="px-6 py-4">
                <div class="space-y-1">
                  <p class="font-medium text-slate-900 max-w-[320px] overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ prompt.title }}
                  </p>
                  <p class="text-sm text-slate-500 max-w-[420px] overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ prompt.description }}
                  </p>
                </div>
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">
                {{ prompt.category }}
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">
                {{ prompt.likes || 0 }}
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">
                {{ prompt.views || 0 }}
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">
                {{ prompt.createdAt }}
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-2">
                  <button
                    @click="editPrompt(prompt)"
                    class="p-2 text-primary-600 hover:bg-primary-50 rounded transition-colors"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button
                    @click="deletePrompt(prompt.id)"
                    class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors"
                  >
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

      <div v-if="!loading && prompts.length === 0" class="text-center py-12 text-slate-500">
        暂无数据
      </div>
      <div v-if="loading" class="text-center py-12 text-slate-400">
        加载中...
      </div>
      <div v-if="!loading && total > 0" class="px-6 py-4 border-t border-slate-100 flex items-center justify-end">
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
    </div>

    <PromptFormModal
      :open="showModal"
      :mode="modalMode"
      :prompt="currentPrompt"
      :saving="saving"
      :categories="categories"
      @close="closeModal"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElPagination } from 'element-plus'
import type { Prompt } from '@/types'
import { adminPromptsApi } from '@/api/admin.prompts.api'
import request from '@/utils/request.api'
import PromptFormModal from './components/prompt_FormModal.vue'
import {getPromptsTypes} from "@/api/category.api";

const prompts = ref<Prompt[]>([])
const categories = ref<string[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const loading = ref(false)
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const pageSizeOptions = [10, 20, 50, 100]

const showModal = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const currentPrompt = ref<Prompt | null>(null)
const saving = ref(false)

const loadPrompts = async () => {
  loading.value = true
  try {
    const pageRes = await adminPromptsApi.getPromptsPage({
      page: page.value,
      size: pageSize.value,
      query: searchQuery.value.trim() || undefined,
      category: selectedCategory.value || undefined
    })
    prompts.value = pageRes.content || []
    total.value = pageRes.total || 0
  } catch (e) {
    console.error('加载提示词失败:', e)
    prompts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getPromptsTypes();
    const arr = (res || []) as any[]
    categories.value = arr.filter(c => !c.type || c.type === 'prompt').map(c => c.name)
  } catch (e) {
    console.error('加载分类失败:', e)
    categories.value = []
  }
}

const onCurrentChange = (p: number) => {
  page.value = Math.max(0, p - 1)
  loadPrompts()
}

const onSizeChange = (s: number) => {
  pageSize.value = s
  page.value = 0
  loadPrompts()
}

const openAdd = () => {
  modalMode.value = 'add'
  currentPrompt.value = null
  showModal.value = true
}

const editPrompt = (prompt: Prompt) => {
  modalMode.value = 'edit'
  currentPrompt.value = prompt
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSave = async (payload: any) => {
  saving.value = true
  try {
    await adminPromptsApi.save({
      id: payload.id,
      title: payload.title,
      description: payload.description,
      content: payload.content,
      category: payload.category,
      coverImage: payload.coverImage,
      effectImage: payload.effectImage,
      usageGuide: payload.usageGuide,
      tags: payload.tags
    } as any)
    showModal.value = false
    await loadPrompts()
    ElMessage.success('保存成功')

    await loadCategories();
  } catch (e) {
    console.error('保存提示词失败:', e)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const deletePrompt = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个提示词吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await adminPromptsApi.remove(id)
    await loadPrompts()
    ElMessage.success('已删除')
  } catch (e) {
    console.error('删除提示词失败:', e)
    ElMessage.error('删除失败，请稍后重试')
  }
}

watch([searchQuery, selectedCategory], () => {
  page.value = 0
  loadPrompts()
})

onMounted(() => {
  loadPrompts()
  loadCategories()
})
</script>

