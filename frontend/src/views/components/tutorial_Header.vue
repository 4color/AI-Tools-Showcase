<template>
  <div class="bg-white border-b border-slate-100 py-8">
    <div class="container-custom">
      <!-- Back Navigation -->
      <router-link to="/tutorials" class="inline-flex items-center gap-2 text-slate-600 hover:text-primary-600 mb-4 transition-colors text-sm">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回教程列表
      </router-link>

      <h1 v-if="tutorial" class="text-3xl md:text-4xl font-bold text-slate-900 mb-4 leading-tight">{{ tutorial.title }}</h1>
      <p v-if="tutorial" class="text-slate-600 text-lg mb-6">{{ tutorial.description }}</p>

      <!-- Meta Info + Actions (same row) -->
      <div v-if="tutorial" class="flex flex-wrap items-center gap-6 text-sm">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 rounded-full bg-gradient-to-r from-primary-600 to-primary-500 flex items-center justify-center text-white font-medium text-sm">
            {{ tutorial.author?.username?.charAt(0) || 'A' }}
          </div>
          <span class="text-slate-700 font-medium">{{ tutorial.author?.username }}</span>
        </div>

        <div class="flex items-center gap-2 text-slate-500">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
          <span>{{ tutorial.duration || '30 分钟' }}</span>
        </div>

        <div class="flex items-center gap-2 text-slate-500">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          <span>{{ (tutorial.viewCount || 0).toLocaleString() }} 阅读</span>
        </div>

        <div v-if="tutorial.rating" class="flex items-center gap-1 text-amber-500">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
          <span>{{ tutorial.rating }}</span>
        </div>

        <span class="text-slate-400">|</span>
        <span class="text-slate-500 flex items-center gap-4">
          <span>{{ tutorial.createdAt }}</span>
          <!-- Action Buttons inline -->
          <button 
            @click="$emit('toggleFavorite')" 
            :disabled="favoriteLoading"
            class="inline-flex items-center gap-2 px-4 py-1.5 rounded-lg font-medium transition-colors"
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
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
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
          <button @click="$emit('share')" class="inline-flex items-center gap-2 px-4 py-1.5 bg-slate-100 text-slate-700 rounded-lg font-medium hover:bg-slate-200 transition-colors">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>
            分享
          </button>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Tutorial } from '@/types'

interface Props {
  tutorial: Tutorial | null
  isFavorited?: boolean
  favoriteLoading?: boolean
}

defineProps<Props>()

defineEmits<{
  toggleFavorite: []
  share: []
}>()

const getDifficultyLabel = (difficulty?: string) => {
  const map: Record<string, string> = {
    'beginner': '入门',
    'intermediate': '进阶',
    'advanced': '高级'
  }
  return map[difficulty || ''] || '入门'
}
</script>
