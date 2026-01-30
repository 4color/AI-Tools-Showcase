<template>
  <div class="min-h-screen bg-slate-50 py-8">
    <div class="container-custom">
      <!-- Back Navigation -->
      <router-link to="/tools"
                   class="inline-flex items-center gap-2 text-slate-600 hover:text-primary-600 mb-6 transition-colors">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回工具列表
      </router-link>

      <div class="grid lg:grid-cols-3 gap-8">
        <!-- Main Content (Left Column) -->
        <div class="lg:col-span-2 space-y-8">
          <ToolsHeaderCard :tool="tool"/>
          <ToolsTags :tags="tool?.tags"/>
          <ToolsIntroduction :markdown="tool?.introduction"/>
          <ToolsComments v-if="tool" :entity-id="tool.id" @success="handleCommentSuccess" @error="handleCommentError"/>
          <ToolsRelatedTools :tools="relatedTools" @view="viewTool"/>
        </div>

        <!-- Sidebar (Right Column) -->
        <div class="lg:col-span-1 space-y-6">
          <ToolsSidebarRelatedTutorials :tutorials="relatedTutorials" @view="viewTutorial"/>
          <ToolsSidebarStats :tool="tool"/>
          <ToolsSidebarShare/>
        </div>
      </div>

      <!-- Toast Notification -->
      <Transition name="fade">
        <div v-if="toast.show"
             class="fixed bottom-6 right-6 px-4 py-3 rounded-lg shadow-lg text-white text-sm font-medium"
             :class="toast.type === 'success' ? 'bg-emerald-600' : toast.type === 'error' ? 'bg-red-600' : 'bg-slate-600'">
          {{ toast.message }}
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {toolsApi} from '@/api/tool.api'
import {tutorialsApi} from '@/api/tutorial.api'
import type {Tool, Tutorial} from '@/types'
import ToolsHeaderCard from '@/views/components/tools_HeaderCard.vue'
import ToolsTags from '@/views/components/tools_Tags.vue'
import ToolsIntroduction from '@/views/components/tools_Introduction.vue'
import ToolsComments from '@/views/components/tools_Comments.vue'
import ToolsRelatedTools from '@/views/components/tools_RelatedTools.vue'
import ToolsSidebarRelatedTutorials from '@/views/components/tools_SidebarRelatedTutorials.vue'
import ToolsSidebarStats from '@/views/components/tools_SidebarStats.vue'
import ToolsSidebarShare from '@/views/components/tools_SidebarShare.vue'

const route = useRoute()
const router = useRouter()
const tool = ref<Tool | null>(null)
const relatedTools = ref<Tool[]>([])
const relatedTutorials = ref<Tutorial[]>([])

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
  setTimeout(() => {
    toast.show = false
  }, 3000)
}

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    // 增加浏览量
    try {
      await toolsApi.incrementViewCount(id)
    } catch (error) {
      console.error('Failed to increment view count:', error)
      // 浏览量增加失败不影响页面加载，只记录错误
    }

    // 加载工具详情
    const toolResponse: Tool = await toolsApi.getTool(id) as unknown as Tool;
    tool.value = toolResponse;

    // 获取相关教程
    try {
      const tutorialsResponse: Tutorial[] = await tutorialsApi.getRelatedTutorials(id) as unknown as Tutorial[];
      relatedTutorials.value = tutorialsResponse;
    } catch (error) {
      console.error('Failed to load related tutorials:', error);
      relatedTutorials.value = [];
    }

  } catch (error) {
    console.error('Failed to fetch tool details:', error)
    showToast('加载工具详情失败', 'error')
  }
})

// 处理评论成功
const handleCommentSuccess = () => {
  showToast('评论发布成功', 'success')
}

// 处理评论错误
const handleCommentError = (message: string) => {
  showToast(message, 'error')
}

const viewTool = (id: number) => router.push(`/tools/${id}`)
const viewTutorial = (id: number) => router.push(`/tutorials/${id}`)
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

:deep() {
  .vuepress-markdown-body {
    padding: 0px !important;
  }
}

</style>
