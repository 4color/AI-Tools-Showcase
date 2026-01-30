<template>
  <div class="space-y-6">
    <div v-if="models && models.length" class="grid md:grid-cols-2 gap-4">
      <div
        v-for="model in models"
        :key="model.id"
        class="group bg-white rounded-xl border border-slate-100 p-6 hover:shadow-lg hover:border-slate-200 transition-all"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="min-w-0">
            <h3 class="font-semibold text-slate-900 mb-1 truncate">{{ model.name }}</h3>
            <p class="text-sm text-slate-600">{{ model.description }}</p>
          </div>

          <div class="flex items-center gap-2 flex-shrink-0">
            <button
              class="px-3 py-1.5 text-xs font-medium rounded-lg bg-primary-50 text-primary-700 hover:bg-primary-100 transition-colors opacity-0 group-hover:opacity-100"
              @click="$emit('test-model', model.id)"
            >
              测试
            </button>
            <span v-if="model.recommended" class="px-2 py-1 bg-primary-100 text-primary-700 rounded text-xs font-medium">推荐</span>
          </div>
        </div>
        <div class="space-y-3">
          <div class="flex items-center gap-2 text-sm">
            <svg class="w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
            <span class="text-slate-600">上下文长度：</span>
            <span class="font-medium text-slate-900">{{ model.contextLength.toLocaleString() }} tokens</span>
          </div>
          <div class="flex flex-wrap gap-1">
            <span v-for="cap in model.capabilities" :key="cap" class="px-2 py-0.5 bg-slate-100 rounded text-xs text-slate-600">{{ cap }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-16 text-slate-500">
      暂无模型
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ApiModel } from '@/types'

defineProps<{
  models?: ApiModel[]
}>()

defineEmits<{
  (e: 'test-model', id: number): void
}>()
</script>

