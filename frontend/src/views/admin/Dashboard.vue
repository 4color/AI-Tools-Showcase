<template>
  <div>
    <div class="mb-6 flex items-start justify-between gap-4">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">仪表盘</h2>
        <p class="text-slate-600 mt-1">系统概览和统计数据</p>
      </div>
      <button
        @click="loadDashboard"
        class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
        :disabled="loading"
      >
        {{ loading ? '刷新中...' : '刷新' }}
      </button>
    </div>

    <AdminDashboardStatsPanel :stats="stats" :loading="loading" />

    <!-- Recent Activity -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">
      <AdminDashboardRecentPanel
        title="最近添加的工具"
        to="/admin/tools"
        :items="recentTools"
        :loading="loading"
      />
      <AdminDashboardRecentPanel
        title="最近添加的教程"
        to="/admin/tutorials"
        :items="recentTutorials"
        :loading="loading"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi, type AdminDashboardItem, type AdminDashboardStats as AdminDashboardStatsType } from '@/api/admin.api'
import AdminDashboardStatsPanel from './components/admin_DashboardStats.vue'
import AdminDashboardRecentPanel from './components/admin_DashboardRecent.vue'

const loading = ref(false)
const stats = ref<AdminDashboardStatsType>({
  tools: 0,
  tutorials: 0,
  users: 0,
  apis: 0
})

const recentTools = ref<AdminDashboardItem[]>([])
const recentTutorials = ref<AdminDashboardItem[]>([])

const loadDashboard = async () => {
  loading.value = true
  try {
    const data = await adminApi.getDashboard()
    stats.value = data.stats
    recentTools.value = data.recent.filter(i => i.kind === 'tool')
    recentTutorials.value = data.recent.filter(i => i.kind === 'tutorial')
  } catch (e) {
    console.error('加载仪表盘失败:', e)
    stats.value = { tools: 0, tutorials: 0, users: 0, apis: 0 }
    recentTools.value = []
    recentTutorials.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})
</script>
