<template>
  <div class="bg-white border-b border-slate-100 py-8">
    <div class="container-custom">
      <!-- Back Navigation -->
      <router-link to="/apis" class="inline-flex items-center gap-2 text-slate-600 hover:text-primary-600 mb-4 transition-colors text-sm">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        返回 API 列表
      </router-link>

      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <div class="flex items-center gap-3 mb-2">
            <h1 v-if="api" class="text-3xl font-bold text-slate-900">{{ api.name }}</h1>
            <span
              v-if="api"
              class="px-3 py-1 rounded-full text-sm font-medium"
              :class="getPricingBadgeClass(api.pricing)"
            >
              {{ getPricingText(api.pricing) }}
            </span>
          </div>
          <p v-if="api" class="text-slate-600">{{ api.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ApiInfo } from '@/types'

defineProps<{
  api: ApiInfo | null
}>()

const getPricingText = (pricing: string) => ({ free: '免费', freemium: '免费增值', paid: '付费' }[pricing] || pricing)

const getPricingBadgeClass = (pricing: string) => ({
  'bg-emerald-100 text-emerald-700': pricing === 'free',
  'bg-primary-100 text-primary-700': pricing === 'freemium',
  'bg-amber-100 text-amber-700': pricing === 'paid'
}[pricing] || 'bg-slate-100 text-slate-700')
</script>

