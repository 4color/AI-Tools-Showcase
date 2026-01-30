<template>
  <aside class="w-64 bg-white border-r border-slate-200 min-h-[calc(100vh-73px)] sticky top-[73px]">
    <nav class="p-4 space-y-1">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors"
        :class="isActive(item.path) ? 'bg-primary-50 text-primary-600' : 'text-slate-600 hover:bg-slate-50'"
      >
        <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-html="item.iconSvg"></svg>
        <span>{{ item.label }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

interface MenuItem {
  path: string
  label: string
  iconSvg: string
}

const props = defineProps<{
  menuItems: MenuItem[]
}>()

const isActive = (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}

</script>

