<template>
  <div class="bg-white rounded-xl border border-slate-100 p-6">
    <h3 class="font-semibold text-slate-900 mb-4">分享工具</h3>
    <div class="flex gap-2">
      <button @click="shareTo('wechat')" class="flex-1 py-2 bg-slate-100 text-slate-600 rounded-lg text-sm font-medium hover:bg-slate-200 transition-colors">
        微信
      </button>
      <button @click="shareTo('weibo')" class="flex-1 py-2 bg-slate-100 text-slate-600 rounded-lg text-sm font-medium hover:bg-slate-200 transition-colors">
        微博
      </button>
      <button @click="copyLink" class="flex-1 py-2 bg-slate-100 text-slate-600 rounded-lg text-sm font-medium hover:bg-slate-200 transition-colors">
        复制链接
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'

const shareTo = (platform: string) => {
  ElMessage.success(`${platform === 'wechat' ? '微信' : platform === 'weibo' ? '微博' : ''}分享功能`)
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

