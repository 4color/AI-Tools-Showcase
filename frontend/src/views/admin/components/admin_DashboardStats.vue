<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
    <div class="bg-white rounded-xl border border-slate-100 p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-slate-600 mb-1">工具总数</p>
          <p class="text-2xl font-bold text-slate-900">{{ loading ? '-' : stats.tools }}</p>
        </div>
        <div class="w-12 h-12 rounded-lg bg-primary-100 flex items-center justify-center">
          <svg class="w-6 h-6 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl border border-slate-100 p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-slate-600 mb-1">教程总数</p>
          <p class="text-2xl font-bold text-slate-900">{{ loading ? '-' : stats.tutorials }}</p>
        </div>
        <div class="w-12 h-12 rounded-lg bg-emerald-100 flex items-center justify-center">
          <svg class="w-6 h-6 text-emerald-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
            <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl border border-slate-100 p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-slate-600 mb-1">用户总数</p>
          <p class="text-2xl font-bold text-slate-900">{{ loading ? '-' : stats.users }}</p>
        </div>
        <div class="w-12 h-12 rounded-lg bg-violet-100 flex items-center justify-center">
          <svg class="w-6 h-6 text-violet-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl border border-slate-100 p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-slate-600 mb-1">API 总数</p>
          <p class="text-2xl font-bold text-slate-900">{{ loading ? '-' : stats.apis }}</p>
        </div>
        <div class="w-12 h-12 rounded-lg bg-amber-100 flex items-center justify-center">
          <svg class="w-6 h-6 text-amber-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" y1="22.08" x2="12" y2="12"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AdminDashboardStats } from '@/api/admin.api'

defineProps<{
  stats: AdminDashboardStats
  loading: boolean
}>()
</script>

