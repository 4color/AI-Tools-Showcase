<template>
  <div class="min-h-screen bg-slate-50">
    <ApiHeader :api="api" />

    <!-- Content -->
    <div class="container-custom py-8">
      <!-- Tabs -->
      <div class="flex gap-1 mb-8 border-b border-slate-200">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          class="px-4 py-3 text-sm font-medium transition-colors border-b-2 -mb-px"
          :class="activeTab === tab.id ? 'text-primary-600 border-primary-600' : 'text-slate-600 border-transparent hover:text-slate-900'"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Models Tab -->
      <ApiModelList
        v-if="activeTab === 'models'"
        :models="api?.models"
        @test-model="handleTestModel"
      />

      <!-- Chat Test Tab -->
      <ApiChatTest
        v-if="activeTab === 'chat' && api"
        :api-id="api.id"
        :models="api.models || []"
        :initial-model-id="selectedModelId || undefined"
      />

    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import type { ApiInfo } from '@/types'
import { apisApi } from '@/api/api.api'
import ApiHeader from './components/api_Header.vue'
import ApiModelList from './components/api_ModelList.vue'
import ApiChatTest from './components/api_ChatTest.vue'

const route = useRoute()
const api = ref<ApiInfo | null>(null)
const activeTab = ref('models')
const selectedModelId = ref<number | null>(null)

const tabs = [
  { id: 'models', label: '模型列表' },
  { id: 'chat', label: '对话测试' }
]

const handleTestModel = (modelId: number) => {
  selectedModelId.value = modelId
  activeTab.value = 'chat'
}

onMounted(() => {
  const id = Number(route.params.id)
  apisApi.getApi(id).then((data: any) => {
    api.value = data as ApiInfo
  }).catch((err) => {
    console.error('加载 API 详情失败:', err)
    api.value = null
  })
})
</script>
