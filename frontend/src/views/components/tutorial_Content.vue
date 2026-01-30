<template>
  <div class="space-y-6">
    <!-- Cover Image -->
    <div v-if="tutorial" class="rounded-2xl overflow-hidden border border-slate-100">
      <!-- Real cover image -->
      <div v-if="tutorial.coverImage" class="relative h-48 md:h-56">
        <img
          :src="apiBaseUrl + tutorial.coverImage"
          alt="Cover"
          class="absolute inset-0 w-full h-full object-cover"
        />
        <div class="absolute inset-0 bg-gradient-to-b from-black/0 via-black/10 to-black/30"></div>
      </div>

      <!-- Placeholder -->
      <div v-else class="bg-gradient-to-br from-primary-600 via-violet-600 to-indigo-500 p-8 text-white">
        <div class="flex items-center justify-center h-48">
          <svg class="w-20 h-20 text-white/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
            <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- Tutorial Content -->
    <div v-if="tutorial" class="bg-white rounded-xl border border-slate-100 p-8">
      <div class="prose prose-slate max-w-none markdown-content" v-html="renderedContent"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { renderMarkdown } from '@/utils/markdown-renderer'
import type { Tutorial } from '@/types'

// @ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

interface Props {
  tutorial: Tutorial | null
}

const props = defineProps<Props>()

// 渲染 markdown 内容
const renderedContent = computed(() => {
  if (!props.tutorial?.content) return ''
  return renderMarkdown(props.tutorial.content)
})
</script>

<style scoped>
/* Markdown 内容样式 */
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  scroll-margin-top: 100px;
}

.markdown-content :deep(h1) {
  font-size: 2rem;
  font-weight: 700;
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: #0f172a;
}

.markdown-content :deep(h2) {
  font-size: 1.5rem;
  font-weight: 600;
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: #0f172a;
}

.markdown-content :deep(h3) {
  font-size: 1.25rem;
  font-weight: 600;
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
  color: #0f172a;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin: 1rem 0;
  padding-left: 1.5rem;
}

.markdown-content :deep(li) {
  margin: 0.5rem 0;
  line-height: 1.75;
}

.markdown-content :deep(pre) {
  background-color: #0f172a;
  color: #f1f5f9;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
}

.markdown-content :deep(code) {
  font-family: 'Courier New', monospace;
  font-size: 0.875rem;
}

.markdown-content :deep(a) {
  color: #2563eb;
  text-decoration: underline;
}

.markdown-content :deep(a:hover) {
  color: #1d4ed8;
}
</style>
