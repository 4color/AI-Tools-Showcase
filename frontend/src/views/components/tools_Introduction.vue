<template>
  <div v-if="markdown" class="bg-white rounded-xl border border-slate-100 p-8">
    <div class="prose prose-slate max-w-none">
      <v-md-preview :text="markdown" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  markdown?: string
}>()
</script>
