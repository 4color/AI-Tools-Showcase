<template>
  <div class="bg-white rounded-2xl border border-slate-100 px-6 py-7">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-semibold text-slate-900">{{ title }}</h3>
      <router-link v-if="to" :to="to" class="text-sm text-primary-600 hover:text-primary-700">
        查看更多
      </router-link>
    </div>

    <div class="space-y-3 mt-2">
      <div
        v-for="item in items"
        :key="`${item.kind}-${item.id}`"
        class="flex items-center gap-3 px-3 py-3 bg-slate-50 rounded-xl"
      >
        <div class="w-10 h-10 rounded-lg flex items-center justify-center font-semibold"
             :class="item.kind === 'tool' ? 'bg-primary-100 text-primary-600' : 'bg-emerald-100 text-emerald-600'">
          {{ item.title.charAt(0) }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="font-medium text-slate-900 text-sm truncate">{{ item.title }}</p>
          <p class="text-xs text-slate-500">{{ formatDate(item.createdAt) }}</p>
        </div>
      </div>

      <div v-if="!loading && items.length === 0" class="text-center py-8 text-slate-500 text-sm">
        暂无数据
      </div>
      <div v-if="loading" class="text-center py-8 text-slate-400 text-sm">
        加载中...
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AdminDashboardItem } from '@/api/admin.api'

defineProps<{
  title: string
  to?: string
  items: AdminDashboardItem[]
  loading: boolean
}>()

const formatDate = (val: string) => {
  // 兼容 LocalDateTime / ISO
  try {
    const d = new Date(val)
    if (Number.isNaN(d.getTime())) return val
    return d.toLocaleString()
  } catch {
    return val
  }
}
</script>

