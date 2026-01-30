<template>
  <div class="min-h-screen bg-slate-50">
    <div class="bg-white border-b border-slate-100 py-10">
      <div class="container-custom">
        <h1 class="text-3xl font-bold text-slate-900 mb-2">全站搜索</h1>
        <p class="text-slate-600 mb-6">搜索工具、教程、提示词、API</p>
        <div class="max-w-2xl flex items-center bg-slate-50 rounded-xl border border-slate-200 p-2">
          <svg class="w-5 h-5 text-slate-400 ml-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
          <input
            v-model="searchInput"
            type="text"
            placeholder="输入关键词搜索..."
            class="flex-1 px-4 py-2.5 bg-transparent outline-none text-slate-900 placeholder-slate-400"
            @keyup.enter="doSearch"
          >
          <button
            type="button"
            @click="doSearch"
            class="px-5 py-2.5 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors"
          >
            搜索
          </button>
        </div>
      </div>
    </div>

    <div class="container-custom py-8">
      <div v-if="loading" class="flex justify-center py-16">
        <div class="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
      </div>

      <div v-else-if="searched && results.length === 0" class="text-center py-16">
        <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="11" cy="11" r="8"/>
          <path d="m21 21-4.35-4.35"/>
        </svg>
        <h3 class="text-lg font-semibold text-slate-900 mb-2">未找到相关结果</h3>
        <p class="text-slate-600">试试其他关键词</p>
      </div>

      <div v-else-if="results.length > 0" class="space-y-4">
        <p class="text-sm text-slate-500 mb-4">共 {{ results.length }} 条结果</p>
        <div
          v-for="item in results"
          :key="item.entityType + '-' + item.entityId"
          class="bg-white rounded-xl border border-slate-100 p-5 hover:shadow-md hover:border-slate-200 transition-all cursor-pointer"
          @click="goDetail(item)"
        >
          <div class="flex items-start justify-between gap-4">
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span
                  class="text-xs font-medium px-2 py-0.5 rounded"
                  :class="typeClass(item.entityType)"
                >
                  {{ typeLabel(item.entityType) }}
                </span>
                <h3 class="font-semibold text-slate-900 truncate">{{ item.title }}</h3>
              </div>
              <p v-if="item.content" class="text-sm text-slate-600 line-clamp-2">{{ item.content }}</p>
            </div>
            <span class="flex-shrink-0 text-primary-600 text-sm font-medium">
              查看详情
              <svg class="w-4 h-4 inline-block ml-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </span>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-16 text-slate-500">
        <p>在首页或上方输入关键词进行搜索</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchApi, type SearchIndexItem } from '@/api/search.api'

const route = useRoute()
const router = useRouter()
const searchInput = ref('')
const results = ref<SearchIndexItem[]>([])
const loading = ref(false)
const searched = ref(false)

function typeLabel(type: string) {
  const map: Record<string, string> = {
    tool: '工具',
    tutorial: '教程',
    prompt: '提示词',
    api: 'API'
  }
  return map[type] || type
}

function typeClass(type: string) {
  const map: Record<string, string> = {
    tool: 'bg-primary-100 text-primary-700',
    tutorial: 'bg-amber-100 text-amber-700',
    prompt: 'bg-emerald-100 text-emerald-700',
    api: 'bg-violet-100 text-violet-700'
  }
  return map[type] || 'bg-slate-100 text-slate-600'
}

function detailPath(item: SearchIndexItem) {
  const base: Record<string, string> = {
    tool: '/tools',
    tutorial: '/tutorials',
    prompt: '/prompts',
    api: '/apis'
  }
  const path = base[item.entityType]
  return path ? `${path}/${item.entityId}` : '/'
}

function goDetail(item: SearchIndexItem) {
  router.push(detailPath(item))
}

async function doSearch() {
  const q = searchInput.value.trim()
  if (!q) {
    router.replace({ query: {} })
    results.value = []
    searched.value = false
    return
  }
  router.replace({ query: { q } })
  loading.value = true
  searched.value = true
  try {
    results.value = await searchApi.search(q) || []
  } catch (e) {
    console.error('搜索失败:', e)
    results.value = []
  } finally {
    loading.value = false
  }
}

watch(
  () => route.query.q,
  (q) => {
    searchInput.value = (q as string) || ''
    if (q) {
      loading.value = true
      searched.value = true
      searchApi.search(q as string).then((list) => {
        results.value = list || []
      }).catch(() => {
        results.value = []
      }).finally(() => {
        loading.value = false
      })
    } else {
      results.value = []
      searched.value = false
    }
  },
  { immediate: true }
)
</script>
