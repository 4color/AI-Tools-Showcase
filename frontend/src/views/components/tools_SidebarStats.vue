<template>
  <div v-if="tool" class="bg-white rounded-xl border border-slate-100 p-6">
    <h3 class="font-semibold text-slate-900 mb-4">数据概览</h3>
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <span class="text-sm text-slate-600">评分</span>
        <div class="flex items-center gap-1">
          <svg class="w-4 h-4 text-amber-400" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
          <span class="font-semibold text-slate-900">{{ formatRating(tool.rating) }}</span>
        </div>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-sm text-slate-600">评价数</span>
        <span class="font-semibold text-slate-900">{{ tool.reviewCount?.toLocaleString() }}</span>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-sm text-slate-600">浏览量</span>
        <span class="font-semibold text-slate-900">{{ (tool.viewCount || 0).toLocaleString() }}</span>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-sm text-slate-600">价格</span>
        <span class="font-semibold" :class="tool.price === 'free' ? 'text-emerald-600' : tool.price === 'freemium' ? 'text-primary-600' : 'text-amber-600'">
          {{ getPriceText(tool.price) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Tool } from '@/types'

defineProps<{
  tool: Tool | null
}>()

const formatRating = (rating: number | undefined): string => {
  if (rating === undefined || rating === null) return '0.0'
  return Number(rating).toFixed(1)
}

const getPriceText = (price: string) => ({ free: '免费', freemium: '免费增值', paid: '付费' }[price.toLowerCase()] || price)
</script>

