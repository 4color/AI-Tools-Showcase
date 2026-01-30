<template>
  <div>
    <div v-if="isLoggedIn" class="border-t border-slate-100 pt-6">
      <div class="mb-4">
        <label class="block text-sm font-medium text-slate-700 mb-2">评分（可选）</label>
        <div class="flex items-center gap-1">
          <button
            v-for="star in 5"
            :key="star"
            @click="selectedRating = star"
            class="p-1 transition-colors"
            :class="selectedRating >= star ? 'text-amber-400' : 'text-slate-300'"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </button>
          <span v-if="selectedRating > 0" class="text-sm text-slate-600 ml-2">{{ selectedRating }} 星</span>
        </div>
      </div>
      <textarea 
        v-model="content" 
        rows="3" 
        placeholder="分享您的学习心得..." 
        class="w-full px-4 py-3 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 mb-4 resize-none"
      ></textarea>
      <button 
        @click="handleSubmit" 
        :disabled="submitting || !content.trim()"
        class="inline-flex items-center gap-2 px-5 py-2.5 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <svg v-if="!submitting" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          <line x1="12" y1="19" x2="12" y2="12"/>
          <line x1="9" y1="15" x2="15" y2="9"/>
        </svg>
        <div v-else class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
        {{ submitting ? '发布中...' : '发布评论' }}
      </button>
    </div>

    <div v-else class="border-t border-slate-100 pt-4">
      <p class="text-slate-600 text-sm">
        <a @click.prevent="redirectToLogin()" class="text-primary-600 font-medium hover:underline cursor-pointer">登录</a> 后发表评论
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { redirectToLogin } from '@/utils/auth'
import { commentApi } from '@/api/comment.api'

interface Props {
  entityType: string
  entityId: number
}

interface Emits {
  (e: 'success'): void
  (e: 'error', message: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const content = ref('')
const selectedRating = ref(0)
const submitting = ref(false)

const handleSubmit = async () => {
  if (!content.value.trim()) return
  
  if (!isLoggedIn.value) {
    redirectToLogin()
    return
  }
  
  submitting.value = true
  try {
    await commentApi.addComment({
      entityType: props.entityType,
      entityId: props.entityId,
      content: content.value.trim(),
      rating: selectedRating.value > 0 ? selectedRating.value : undefined
    })
    
    // 清空表单
    content.value = ''
    selectedRating.value = 0
    
    emit('success')
  } catch (error: any) {
    console.error('发布评论失败:', error)
    emit('error', error?.message || '发布评论失败')
  } finally {
    submitting.value = false
  }
}
</script>
