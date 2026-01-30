<template>
  <div v-if="tool" class="bg-white rounded-2xl shadow-sm border border-slate-100 p-8">
    <div class="flex flex-col md:flex-row gap-8">
      <div class="flex flex-col items-center gap-4">
        <template v-if="tool.logoUrl">
          <img :src="apiBaseUrl+tool.logoUrl" class="w-32 h-32 rounded-2xl object-cover"></img>
        </template>
        <template v-else>
          <div
              class="w-32 h-32 rounded-2xl bg-gradient-to-br from-primary-100 to-primary-50 flex items-center justify-center text-primary-600 font-bold text-4xl shadow-sm">
            {{ tool.name.charAt(0) }}
          </div>
        </template>
        <div class="flex gap-2">
          <span class="px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm font-medium">{{
              tool.category
            }}</span>
          <span class="px-3 py-1 rounded-full text-sm font-medium"
                :class="tool.price === 'free' ? 'bg-emerald-100 text-emerald-700' : tool.price === 'freemium' ? 'bg-primary-100 text-primary-700' : 'bg-amber-100 text-amber-700'">
            {{ getPriceText(tool.price) }}
          </span>
        </div>
      </div>

      <div class="flex-1">
        <h1 class="text-3xl font-bold text-slate-900 mb-3">{{ tool.name }}</h1>
        <p class="text-slate-600 leading-relaxed mb-6">{{ tool.description }}</p>

        <div class="flex flex-wrap items-center gap-6 mb-6">
          <div class="flex items-center gap-2">
            <div class="flex items-center gap-1">
              <svg class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              <svg class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              <svg class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              <svg class="w-5 h-5 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              <svg class="w-5 h-5" :class="tool.rating >= 4.5 ? 'text-amber-400' : 'text-slate-200'" viewBox="0 0 24 24"
                   fill="currentColor">
                <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
            </div>
            <span class="font-semibold text-slate-900">{{ formatRating(tool.rating) }}</span>
            <span class="text-slate-500 text-sm">({{ tool.reviewCount?.toLocaleString() }} 条评价)</span>
          </div>
          <div class="flex items-center gap-2 text-slate-500">
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            <span>{{ (tool.viewCount || 0).toLocaleString() }} 浏览量</span>
          </div>
        </div>

        <div class="flex flex-wrap gap-3">
          <button @click="visitWebsite"
                  class="inline-flex items-center gap-2 px-5 py-2.5 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors shadow-sm">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
              <polyline points="15 3 21 3 21 9"/>
              <line x1="10" y1="14" x2="21" y2="3"/>
            </svg>
            访问官网
          </button>
          <button @click="shareTool"
                  class="inline-flex items-center gap-2 px-5 py-2.5 bg-slate-100 text-slate-700 rounded-lg font-medium hover:bg-slate-200 transition-colors">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="18" cy="5" r="3"/>
              <circle cx="6" cy="12" r="3"/>
              <circle cx="18" cy="19" r="3"/>
              <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
              <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
            </svg>
            分享
          </button>
          <button
              @click="toggleFavorite"
              :disabled="favoriteLoading"
              class="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium transition-colors"
              :class="isFavorited ? 'bg-primary-100 text-primary-700 hover:bg-primary-200' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'"
          >
            <svg
                v-if="!favoriteLoading"
                class="w-4 h-4"
                viewBox="0 0 24 24"
                :fill="isFavorited ? 'currentColor' : 'none'"
                stroke="currentColor"
                stroke-width="2"
            >
              <path
                  d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <svg
                v-else
                class="w-4 h-4 animate-spin"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
            >
              <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"/>
            </svg>
            {{ favoriteLoading ? '处理中...' : (isFavorited ? '已收藏' : '收藏') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import type {Tool} from '@/types'
import {useUserStore} from '@/stores/user'
import {likeApi} from '@/api/like.api'
import {ElMessage} from 'element-plus'
import 'element-plus/es/components/message/style/css'
import {redirectToLogin} from '@/utils/auth'

const props = defineProps<{
  tool: Tool | null
}>()

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

//@ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

const isFavorited = ref(false)
const favoriteLoading = ref(false)

watch(
    () => props.tool?.id,
    async (id) => {
      if (!id) return
      isFavorited.value = false
      if (!isLoggedIn.value) return
      try {
        isFavorited.value = (await likeApi.isLiked('tool', id)) as unknown as boolean
      } catch (e) {
        console.error('Failed to check favorite status:', e)
      }
    },
    {immediate: true}
)

const getPriceText = (price: string) => ({
  free: '免费',
  freemium: '免费增值',
  paid: '付费'
}[price.toLowerCase()] || price)

const formatRating = (rating: number | undefined): string => {
  if (rating === undefined || rating === null) return '0.0'
  return Number(rating).toFixed(1)
}

const visitWebsite = () => {
  if (props.tool?.url) window.open(props.tool.url, '_blank')
}

const shareTool = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

const toggleFavorite = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    redirectToLogin()
    return
  }
  if (!props.tool) return

  favoriteLoading.value = true
  try {
    const result = (await likeApi.toggleLike('tool', props.tool.id)) as unknown as boolean
    isFavorited.value = result
    ElMessage.success(result ? '收藏成功' : '已取消收藏')
  } catch (error: any) {
    console.error('Failed to toggle favorite:', error)
    if (error?.response?.status === 401) {
      ElMessage.error('请先登录')
      redirectToLogin()
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    favoriteLoading.value = false
  }
}
</script>

