<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">教程管理</h2>
        <p class="text-slate-600 mt-1">管理和编辑教程内容</p>
      </div>
      <button @click="openAdd"
              class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        添加教程
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
              placeholder="搜索教程标题或描述..."
              class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
        </div>
        <select v-model="selectedDifficulty"
                class="px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
          <option value="">全部难度</option>
          <option value="beginner">入门</option>
          <option value="intermediate">进阶</option>
          <option value="advanced">高级</option>
        </select>
      </div>
    </div>

    <!-- Tutorials Table -->
    <div class="bg-white rounded-xl border border-slate-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 border-b border-slate-200">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">教程标题</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">难度</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">创建时间</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">浏览量</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">置顶</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
          <tr v-for="tutorial in tutorials" :key="tutorial.id" class="hover:bg-slate-50">
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div
                    class="w-10 h-10 rounded-lg bg-emerald-100 flex items-center justify-center text-emerald-600 font-semibold">
                  {{ tutorial.title.charAt(0) }}
                </div>
                <div>
                    <p class="font-medium text-slate-900">{{ tutorial.title }}</p>
                    <p class="text-sm text-slate-500 max-w-[420px] overflow-hidden text-ellipsis whitespace-nowrap">{{ tutorial.description }}</p>
                </div>
              </div>
            </td>
            <td class="px-6 py-4">
                <span class="text-xs font-medium px-2 py-1 rounded"
                      :class="{
                    'bg-emerald-100 text-emerald-700': tutorial.difficulty === 'beginner',
                    'bg-primary-100 text-primary-700': tutorial.difficulty === 'intermediate',
                    'bg-amber-100 text-amber-700': tutorial.difficulty === 'advanced'
                  }">
                  {{ getDifficultyLabel(tutorial.difficulty) }}
                </span>
            </td>
            <td class="px-6 py-4 text-sm text-slate-600">{{ tutorial.createdAt }}</td>
            <td class="px-6 py-4 text-sm text-slate-600">{{ tutorial.viewCount || 0 }}</td>
            <td class="px-6 py-4">
                <span
                    class="text-xs font-medium px-2 py-1 rounded"
                    :class="(tutorial as any).pinned ? 'bg-violet-100 text-violet-700' : 'bg-slate-100 text-slate-600'"
                >
                  {{ (tutorial as any).pinned ? '是' : '否' }}
                </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <button @click="editTutorial(tutorial)"
                        class="p-2 text-primary-600 hover:bg-primary-50 rounded transition-colors">
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button @click="deleteTutorial(tutorial.id)"
                        class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
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
      <div v-if="!loading && tutorials.length === 0" class="text-center py-12 text-slate-500">
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

    <TutorialFormModal
        :open="showModal"
        :mode="modalMode"
        :tutorial="currentTutorial"
        :saving="saving"
        :tools="tools"
        :categories="categories"
        @close="closeModal"
        @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, watch} from 'vue'
import {ElMessage, ElMessageBox, ElPagination} from 'element-plus'
import type {Tutorial, Tool} from '@/types'
import {adminTutorialsApi} from '@/api/admin.tutorials.api'
import {adminToolsApi} from '@/api/admin.tools.api'
import request from '@/utils/request.api'
import TutorialFormModal from './components/tutorial_FormModal.vue'
import {getTutorialTypes} from "@/api/category.api";

const tutorials = ref<Tutorial[]>([])
const tools = ref<Tool[]>([])
const categories = ref<string[]>([])
const searchQuery = ref('')
const selectedDifficulty = ref('')
const loading = ref(false)
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const pageSizeOptions = [10, 20, 50, 100]

const showModal = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const currentTutorial = ref<Tutorial | null>(null)
const saving = ref(false)

const getDifficultyLabel = (difficulty: string) => {
  const map: Record<string, string> = {
    'beginner': '入门',
    'intermediate': '进阶',
    'advanced': '高级'
  }
  return map[difficulty] || difficulty
}

const loadTutorials = async () => {
  loading.value = true
  try {
    const pageRes = await adminTutorialsApi.getTutorialsPage({
      page: page.value,
      size: pageSize.value,
      query: searchQuery.value.trim() || undefined,
      difficulty: selectedDifficulty.value || undefined
    })
    tutorials.value = pageRes.content || []
    total.value = pageRes.total || 0
  } catch (e) {
    console.error('加载教程失败:', e)
    tutorials.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadTools = async () => {
  try {
    const pageRes = await adminToolsApi.getToolsPage({page: 0, size: 100})
    tools.value = (pageRes.content || []) as unknown as Tool[]
  } catch (e) {
    console.error('加载工具列表失败:', e)
    tools.value = []
  }
}

const loadCategories = async () => {
  try {
    const res = await getTutorialTypes();
    const arr = (res || []) as any[]
    categories.value = arr.filter(c => !c.type || c.type === 'tutorial').map(c => c.name)
  } catch (e) {
    console.error('加载分类失败:', e)
    categories.value = []
  }
}

const onCurrentChange = (p: number) => {
  page.value = Math.max(0, p - 1)
  loadTutorials()
}

const onSizeChange = (s: number) => {
  pageSize.value = s
  page.value = 0
  loadTutorials()
}

const openAdd = () => {
  modalMode.value = 'add'
  currentTutorial.value = null
  showModal.value = true
}

const editTutorial = (tutorial: Tutorial) => {
  modalMode.value = 'edit'
  currentTutorial.value = tutorial
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSave = async (payload: any) => {
  saving.value = true
  try {
    // 如果是编辑模式，保留原有的浏览量
    const saveData: any = {
      id: payload.id,
      title: payload.title,
      description: payload.description,
      content: payload.content,
      difficulty: payload.difficulty,
      category: payload.category,
      toolId: payload.toolId,
      coverImage: payload.coverImage,
      pinned: payload.pinned
    }

    // 编辑模式下，保留原有的 viewCount
    if (payload.id && currentTutorial.value) {
      saveData.viewCount = (currentTutorial.value as any).viewCount || 0
    }

    const result = await adminTutorialsApi.save(saveData)
    if (!result) {
      return;
    }
    showModal.value = false
    await loadTutorials()
    ElMessage.success('保存成功')
  } catch (e) {
    console.error('保存教程失败:', e)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const deleteTutorial = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个教程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await adminTutorialsApi.remove(id)
    await loadTutorials()
    ElMessage.success('已删除')
  } catch (e) {
    console.error('删除教程失败:', e)
    ElMessage.error('删除失败，请稍后重试')
  }
}

watch([searchQuery, selectedDifficulty], () => {
  page.value = 0
  loadTutorials()
})

onMounted(() => {
  loadTutorials()
  loadTools()
  loadCategories()
})
</script>

