<template>
  <div v-if="tools.length > 0" class="bg-white rounded-xl border border-slate-100 p-6">
    <h3 class="font-semibold text-slate-900 mb-4">相关工具</h3>
    <div class="grid sm:grid-cols-2 gap-4">
      <div
        v-for="related in tools"
        :key="related.id"
        @click="$emit('view', related.id)"
        class="flex items-center gap-3 p-3 bg-slate-50 rounded-xl cursor-pointer hover:bg-slate-100 transition-colors"
      >
        <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-primary-100 to-primary-50 flex items-center justify-center text-primary-600 font-bold shadow-sm">
          {{ related.name.charAt(0) }}
        </div>
        <div class="flex-1 min-w-0">
          <h4 class="font-medium text-slate-900 truncate">{{ related.name }}</h4>
          <p class="text-sm text-slate-500 truncate">{{ related.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Tool } from '@/types'

defineProps<{
  tools: Tool[]
}>()

defineEmits<{
  (e: 'view', id: number): void
}>()
</script>

