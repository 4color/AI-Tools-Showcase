<template>
  <div class="bg-white rounded-xl border border-slate-100 p-6 sticky top-24">
    <div class="flex items-center gap-2 mb-4">
      <svg class="w-5 h-5 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
      <h3 class="font-semibold text-slate-900">相关教程</h3>
    </div>
    <div class="space-y-4">
      <div
        v-for="tutorial in tutorials"
        :key="tutorial.id"
        @click="$emit('view', tutorial.id)"
        class="group cursor-pointer p-3 -mx-3 rounded-xl hover:bg-slate-50 transition-colors"
      >
        <div class="flex items-start gap-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-violet-100 to-violet-50 flex items-center justify-center flex-shrink-0">
            <svg class="w-5 h-5 text-violet-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
          </div>
          <div class="flex-1 min-w-0">
            <h4 class="font-medium text-slate-900 text-sm group-hover:text-primary-600 transition-colors line-clamp-2 mb-1">{{ tutorial.title }}</h4>
            <p class="text-xs text-slate-500 line-clamp-2 mb-2">{{ tutorial.description }}</p>
            <div class="flex items-center gap-2 text-xs text-slate-400">
              <span class="px-1.5 py-0.5 bg-slate-100 rounded text-slate-600">{{ getDifficultyText(tutorial.difficulty) }}</span>
              <span>{{ tutorial.duration }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <router-link to="/tutorials" class="block mt-4 text-center text-sm text-primary-600 hover:text-primary-700 font-medium">
      查看更多教程 →
    </router-link>
  </div>
</template>

<script setup lang="ts">
import type { Tutorial } from '@/types'

defineProps<{
  tutorials: Tutorial[]
}>()

defineEmits<{
  (e: 'view', id: number): void
}>()

const getDifficultyText = (difficulty: string) => ({ beginner: '入门', intermediate: '进阶', advanced: '高级' }[difficulty] || difficulty)
</script>

