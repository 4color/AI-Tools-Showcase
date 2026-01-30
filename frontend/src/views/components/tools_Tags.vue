<template>
  <div v-if="tags && tags.length > 0" class="bg-white rounded-xl border border-slate-100 p-6">
    <h3 class="font-semibold text-slate-900 mb-4">标签</h3>
    <div class="flex flex-wrap gap-2">
      <span v-for="tag in tags" :key="tag" class="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-sm">{{ tag }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  tags: string[] | undefined
}>()
</script>

