<template>
  <div class="min-h-screen">
    <!-- Hero Section -->
    <section class="relative min-h-[560px] flex items-center justify-center overflow-hidden">
      <!-- Background -->
      <div class="absolute inset-0 bg-hero">
        <div
            class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width=&quot;60&quot; height=&quot;60&quot; viewBox=&quot;0 0 60 60&quot; xmlns=&quot;http://www.w3.org/2000/svg&quot;%3E%3Cg fill=&quot;none&quot; fill-rule=&quot;evenodd&quot;%3E%3Cg fill=&quot;%23ffffff&quot; fill-opacity=&quot;0.05&quot;%3E%3Cpath d=&quot;M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z&quot;/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
      </div>

      <!-- Content -->
      <div class="relative z-10 container-custom text-center py-16">
        <div
            class="inline-flex items-center gap-2 px-4 py-2 bg-white/15 backdrop-blur-lg rounded-full text-white text-sm font-medium mb-6 animate-fade-in">
          <span class="w-2 h-2 bg-emerald-400 rounded-full animate-pulse"></span>
          AI 工具导航平台
        </div>

        <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-4 animate-fade-in-up">
          发现最好的
          <span class="text-gradient">AI 工具</span>
        </h1>

        <p class="text-lg md:text-xl text-white/80 max-w-2xl mx-auto mb-8 animate-fade-in-up">
          探索、学习和分享人工智能工具，提升你的工作效率
        </p>

        <!-- Search Box -->
        <div class="max-w-lg mx-auto mb-6 animate-fade-in-up">
          <div class="flex items-center bg-white rounded-full shadow-xl shadow-primary/20 p-2">
            <svg class="w-5 h-5 text-slate-400 ml-4" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>
            <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索 AI 工具..."
                class="flex-1 px-4 py-3 bg-transparent outline-none text-slate-900 placeholder-slate-400"
                @keyup.enter="handleSearch"
            >
            <button @click="handleSearch"
                    class="px-6 py-3 bg-primary-600 text-white rounded-full font-medium hover:bg-primary-700 transition-colors shadow-sm">
              搜索
            </button>
          </div>
        </div>

        <!-- Popular Tags -->
        <div class="flex flex-wrap items-center justify-center gap-2 animate-fade-in-up">
          <span class="text-white/70 text-sm">热门搜索：</span>
          <button
              v-for="tag in popularTags"
              :key="tag"
              @click="searchByTag(tag)"
              class="px-3 py-1 bg-white/10 backdrop-blur-lg border border-white/20 rounded-full text-white text-xs hover:bg-white/20 transition-colors"
          >
            {{ tag }}
          </button>
        </div>
      </div>

      <!-- Floating Cards Decoration -->
      <div class="absolute inset-0 pointer-events-none overflow-hidden hidden lg:block">
        <div
            class="absolute top-1/4 left-[10%] w-14 h-14 bg-white/90 rounded-xl shadow-lg flex items-center justify-center animate-float text-primary-600">
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
        </div>
        <div
            class="absolute top-[30%] right-[15%] w-14 h-14 bg-white/90 rounded-xl shadow-lg flex items-center justify-center animate-float text-secondary-500"
            style="animation-delay: 1s;">
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <circle cx="8.5" cy="8.5" r="1.5"/>
            <polyline points="21 15 16 10 5 21"/>
          </svg>
        </div>
        <div
            class="absolute bottom-[25%] left-[15%] w-14 h-14 bg-white/90 rounded-xl shadow-lg flex items-center justify-center animate-float text-emerald-500"
            style="animation-delay: 2s;">
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
          </svg>
        </div>
        <div
            class="absolute bottom-[30%] right-[10%] w-14 h-14 bg-white/90 rounded-xl shadow-lg flex items-center justify-center animate-float text-amber-500"
            style="animation-delay: 3s;">
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polygon points="23 7 16 12 23 17 23 7"/>
            <rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>
          </svg>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="relative -mt-8 px-4">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="stat in stats" :key="stat.label"
               class="bg-white rounded-xl p-5 shadow-sm border border-slate-100 hover:-translate-y-0.5 transition-transform">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-lg flex items-center justify-center" :class="stat.bgColor">
                <svg class="w-6 h-6" :class="stat.iconColor" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                     stroke-width="2" v-html="stat.iconPath"></svg>
              </div>
              <div>
                <div class="text-2xl font-bold text-slate-900">{{ stat.value }}+</div>
                <div class="text-sm text-slate-500">{{ stat.label }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-16 bg-slate-50">
      <div class="container-custom">
        <div class="text-center mb-10">
          <h2 class="section-title">探索 AI 世界</h2>
          <p class="section-desc">发现最新的 AI 工具，提升你的创作效率</p>
        </div>

        <div class="grid md:grid-cols-3 gap-6">
          <div
              v-for="feature in features"
              :key="feature.title"
              class="card card-hover p-6"
          >
            <div class="w-14 h-14 rounded-lg flex items-center justify-center mb-4"
                 :style="{ background: feature.gradient }">
              <svg class="w-7 h-7 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                   v-html="feature.iconPath"></svg>
            </div>
            <h3 class="text-xl font-semibold text-slate-900 mb-2">{{ feature.title }}</h3>
            <p class="text-slate-600 text-sm leading-relaxed mb-4">{{ feature.desc }}</p>
            <router-link :to="feature.link"
                         class="inline-flex items-center gap-1 text-primary-600 text-sm font-medium hover:gap-2 transition-all">
              了解更多
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Popular Tools -->
    <section class="py-16">
      <div class="container-custom">
        <div class="flex items-center justify-between mb-8">
          <div>
            <h2 class="section-title">热门工具</h2>
            <p class="text-slate-600 mt-1">最受欢迎的 AI 工具推荐</p>
          </div>
          <router-link to="/tools"
                       class="flex items-center gap-1 text-primary-600 font-medium hover:gap-2 transition-all">
            查看全部
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </router-link>
        </div>

        <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div
              v-for="tool in popularTools"
              :key="tool.id"
              @click="viewTool(tool.id)"
              class="card card-hover p-5 cursor-pointer"
          >
            <div class="flex items-start gap-3 mb-3">
              <template v-if="tool.logoUrl">
                <img :src="apiBaseUrl+tool.logoUrl" class="w-12 h-12 rounded-lg object-cover"></img>
              </template>
              <template v-else>
                <div
                    class="w-12 h-12 rounded-lg bg-primary-50 flex items-center justify-center text-primary-600 font-bold text-lg flex-shrink-0">
                  {{ tool.name.charAt(0) }}
                </div>
              </template>
              <div class="flex-1 min-w-0">
                <h3 class="font-semibold text-slate-900 truncate">{{ tool.name }}</h3>
                <span class="text-xs text-slate-500">{{ tool.category }}</span>
              </div>
              <div class="flex items-center gap-1 text-amber-500 text-sm font-medium">
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                  <polygon
                      points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
                {{ formatRating(tool.rating) }}
              </div>
            </div>
            <p class="text-sm text-slate-600 line-clamp-2 mb-3">{{ tool.description }}</p>
            <div class="flex flex-wrap gap-1 mb-3">
              <span v-for="tag in tool.tags?.slice(0, 3)" :key="tag"
                    class="px-2 py-0.5 bg-slate-100 rounded text-xs text-slate-600">{{ tag }}</span>
            </div>
            <div class="flex items-center justify-between pt-3 border-t border-slate-100">
              <span
                  class="text-xs font-medium"
                  :class="{
                  'text-emerald-600': tool.price === 'free',
                  'text-primary-600': tool.price === 'freemium',
                  'text-amber-600': tool.price === 'paid'
                }"
              >
                {{ getPriceLabel(tool.price) }}
              </span>
              <span class="flex items-center gap-1 text-primary-600 text-sm font-medium">
                查看详情
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path
                    d="M5 12h14M12 5l7 7-7 7"/></svg>
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-16 bg-slate-50">
      <div class="container-custom">
        <div class="bg-hero rounded-2xl p-8 md:p-12 relative overflow-hidden">
          <!-- Decorative -->
          <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -translate-y-1/2 translate-x-1/2"></div>
          <div
              class="absolute bottom-0 left-0 w-48 h-48 bg-white/10 rounded-full translate-y-1/2 -translate-x-1/2"></div>

          <div class="relative z-10 text-center">
            <h2 class="text-2xl md:text-3xl font-bold text-white mb-3">开始你的 AI 之旅</h2>
            <p class="text-white/80 mb-6 max-w-xl mx-auto">注册账号，发现更多 AI 工具，获取个性化推荐</p>
            <div class="flex flex-wrap items-center justify-center gap-4">
              <router-link to="/register"
                           class="px-8 py-3 bg-white text-slate-900 rounded-lg font-semibold hover:shadow-lg transition-all">
                立即注册
              </router-link>
              <router-link to="/tools"
                           class="px-8 py-3 bg-white/10 text-white border border-white/30 rounded-lg font-semibold hover:bg-white/20 transition-all">
                浏览工具
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {toolsApi} from '@/api/tool.api'
import type {Tool} from '@/types'

const router = useRouter()
const searchQuery = ref('')
const popularTools = ref<Tool[]>([])
const popularTags = ['ChatGPT', 'Midjourney', 'Stable Diffusion', 'Claude']

//@ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

const stats = [
  {
    label: 'AI 工具',
    value: '100',
    iconPath: '<rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>',
    bgColor: 'bg-primary-50',
    iconColor: 'text-primary-600'
  },
  {
    label: '教程文章',
    value: '50',
    iconPath: '<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>',
    bgColor: 'bg-amber-50',
    iconColor: 'text-amber-600'
  },
  {
    label: '活跃用户',
    value: '10K',
    iconPath: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>',
    bgColor: 'bg-emerald-50',
    iconColor: 'text-emerald-600'
  },
  {
    label: '用户评分',
    value: '4.8',
    iconPath: '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>',
    bgColor: 'bg-red-50',
    iconColor: 'text-red-500'
  }
]

const features = [
  {
    title: 'AI 工具集合',
    desc: '发现和探索各种 AI 工具，从文本生成到图像处理，应有尽有',
    iconPath: '<rect x="2" y="6" width="20" height="12" rx="2"/><path d="M12 12v4"/><path d="M8 14h8"/>',
    gradient: 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)',
    link: '/tools'
  },
  {
    title: '学习教程',
    desc: '详细的教程和指南，帮助你快速掌握 AI 工具的使用方法',
    iconPath: '<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>',
    gradient: 'linear-gradient(135deg, #0891b2 0%, #06b6d4 100%)',
    link: '/tutorials'
  },
  {
    title: 'API 集成',
    desc: '了解和集成主流 AI API，如 OpenAI、Claude 等，构建你的 AI 应用',
    iconPath: '<path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/>',
    gradient: 'linear-gradient(135deg, #059669 0%, #10b981 100%)',
    link: '/apis'
  }
]

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({path: '/tools', query: {search: searchQuery.value}})
  }
}

const searchByTag = (tag: string) => {
  searchQuery.value = tag
  handleSearch()
}

const viewTool = (id: number) => {
  router.push(`/tools/${id}`)
}

const getPriceLabel = (price: string) => {
  const labels: Record<string, string> = {free: '免费', freemium: '免费增值', paid: '付费'}
  return labels[price] || price
}

const formatRating = (rating: number | undefined): string => {
  if (rating === undefined || rating === null) return '0.0'
  return Number(rating).toFixed(1)
}

onMounted(async () => {
  try {
    const response = await toolsApi.getTools({page: 1, size: 4})
    popularTools.value = response.content;
  } catch (error) {
    console.error('Failed to fetch popular tools:', error)
    // 可以在这里添加错误处理，比如显示错误消息
  }
})
</script>
