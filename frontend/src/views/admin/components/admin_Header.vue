<template>
  <header class="bg-white border-b border-slate-200 sticky top-0 z-40">
    <div class="px-6 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <router-link to="/" class="flex items-center gap-2 text-slate-900">
            <svg class="w-6 h-6 text-primary-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
              <path d="M2 17L12 22L22 17"/>
              <path d="M2 12L12 17L22 12"/>
            </svg>
            <span class="text-lg font-bold">AI Tools</span>
          </router-link>
          <span class="text-slate-400">|</span>
          <h1 class="text-lg font-semibold text-slate-900">管理后台</h1>
        </div>
        <div class="flex items-center gap-4">
          <router-link to="/" class="text-sm text-slate-600 hover:text-slate-900 transition-colors">
            返回首页
          </router-link>
          <button @click="$emit('logout')" class="flex items-center gap-2 px-4 py-2 text-sm text-slate-600 hover:text-slate-900 transition-colors">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            退出登录
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'logout'): void
}>()
</script>

