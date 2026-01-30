<template>
  <div id="app" class="min-h-screen flex flex-col">
    <!-- Header Navigation -->
    <header v-if="!hideSiteHeaderFooter" class="sticky top-0 z-50 bg-white/80 backdrop-blur-lg border-b border-slate-100 h-16">
      <div class="container-custom h-full flex items-center justify-between">
        <!-- Logo -->
        <router-link to="/" class="flex items-center gap-3 text-slate-900 no-underline">
          <div class="w-9 h-9 flex items-center justify-center bg-primary-600 rounded-lg text-white shadow-sm">
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
              <path d="M2 17L12 22L22 17"/>
              <path d="M2 12L12 17L22 12"/>
            </svg>
          </div>
          <span class="text-xl font-bold text-slate-900">AI Tools</span>
        </router-link>

        <!-- Main Navigation -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link 
            to="/" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-all"
            :class="{ 'bg-primary-50 text-primary-700': $route.path === '/' }"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
            <span>首页</span>
          </router-link>
          <router-link 
            to="/tools" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-all"
            :class="{ 'bg-primary-50 text-primary-700': $route.path.startsWith('/tools') }"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            <span>工具</span>
          </router-link>
          <router-link 
            to="/tutorials" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-all"
            :class="{ 'bg-primary-50 text-primary-700': $route.path.startsWith('/tutorials') }"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
            <span>教程</span>
          </router-link>
          <router-link 
            to="/prompts" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-all"
            :class="{ 'bg-primary-50 text-primary-700': $route.path.startsWith('/prompts') }"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            <span>提示词</span>
          </router-link>
          <router-link 
            to="/apis" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-all"
            :class="{ 'bg-primary-50 text-primary-700': $route.path === '/apis' }"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
            <span>API</span>
          </router-link>
        </nav>

        <!-- Right Actions -->
        <div class="flex items-center gap-3">
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="btn btn-ghost text-sm">登录</router-link>
            <router-link to="/register" class="btn btn-primary text-sm">注册</router-link>
          </template>
          <template v-else>
            <router-link
              v-if="userStore.isAdmin"
              to="/admin"
              class="hidden sm:inline-flex items-center px-3 py-1.5 rounded-lg text-sm font-medium bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
            >
              运维入口
            </router-link>
            <div class="relative group">
              <button class="flex items-center gap-2 px-2 py-1 rounded-lg hover:bg-slate-50 transition-all">
                <div class="w-8 h-8 rounded-full bg-primary-600 flex items-center justify-center text-white text-sm font-medium">
                  {{ userStore.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <span class="hidden sm:block text-sm font-medium text-slate-700">{{ userStore.user?.username }}</span>
                <svg class="w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
              </button>
              
              <!-- Dropdown -->
              <div class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-slate-100 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-150 transform origin-top-right">
                <div class="py-2">
                  <router-link v-if="userStore.isAdmin" to="/admin" class="flex items-center gap-2 px-4 py-2 text-sm text-slate-700 hover:bg-slate-50">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4"/></svg>
                    管理后台
                  </router-link>
                  <button @click="userStore.logout()" class="w-full flex items-center gap-2 px-4 py-2 text-sm text-slate-700 hover:bg-slate-50">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                    退出登录
                  </button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1 bg-slate-50">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer v-if="!hideSiteHeaderFooter" class="bg-slate-900 text-slate-400">
      <div class="container-custom py-12">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8 pb-8 border-b border-slate-800">
          <div class="col-span-1 md:col-span-2">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-9 h-9 flex items-center justify-center bg-primary-600 rounded-lg">
                <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
                  <path d="M2 17L12 22L22 17"/>
                  <path d="M2 12L12 17L22 12"/>
                </svg>
              </div>
              <span class="text-lg font-semibold text-white">AI Tools Showcase</span>
            </div>
            <p class="text-sm leading-relaxed text-slate-500">发现、探索和分享最好的 AI 工具，提升你的工作效率</p>
          </div>
          
          <div>
            <h4 class="text-sm font-semibold text-white mb-4">产品</h4>
            <ul class="space-y-2 text-sm">
              <li><router-link to="/tools" class="hover:text-white transition-colors">工具导航</router-link></li>
              <li><router-link to="/tutorials" class="hover:text-white transition-colors">教程指南</router-link></li>
              <li><router-link to="/prompts" class="hover:text-white transition-colors">提示词库</router-link></li>
              <li><router-link to="/apis" class="hover:text-white transition-colors">API 文档</router-link></li>
            </ul>
          </div>
          
          <div>
            <h4 class="text-sm font-semibold text-white mb-4">资源</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="hover:text-white transition-colors">帮助中心</a></li>
              <li><a href="#" class="hover:text-white transition-colors">社区</a></li>
              <li><a href="#" class="hover:text-white transition-colors">反馈</a></li>
            </ul>
          </div>
        </div>
        
        <div class="flex flex-col md:flex-row justify-between items-center gap-4 text-sm">
          <p>&copy; {{ new Date().getFullYear() }} AI Tools Showcase. All rights reserved.</p>
          <p class="text-slate-500">本站由Cursor构建</p>
          <div class="flex items-center gap-4">
            <a href="#" class="w-9 h-9 flex items-center justify-center bg-slate-800 rounded-full hover:bg-slate-700 transition-colors">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/></svg>
            </a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

// 登录、注册、admin 不显示整站头部和尾部
const hideSiteHeaderFooter = computed(() => {
  return route.path === '/login' || route.path === '/register' || route.path.startsWith('/admin')
})

// 应用启动时初始化用户状态
onMounted(() => {
  userStore.initUser()
})
</script>

<style>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
