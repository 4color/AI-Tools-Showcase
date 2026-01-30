<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl w-full max-w-5xl overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-900">
            {{ mode === 'edit' ? '编辑教程' : '新增教程' }}
          </h3>
          <button @click="$emit('close')" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="p-6 max-h-[80vh] overflow-y-auto">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Left: basic info -->
            <div>
              <div class="space-y-4">
                <div>
                  <label class="block text-sm text-slate-600 mb-1">标题</label>
                  <input
                    v-model="form.title"
                    class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                    placeholder="如：如何高效使用 ChatGPT"
                  />
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">简介</label>
                  <textarea
                    v-model="form.description"
                    rows="4"
                    class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                    placeholder="一句话介绍本教程的主要内容"
                  />
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm text-slate-600 mb-1">难度</label>
                    <select v-model="form.difficulty" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
                      <option value="beginner">入门</option>
                      <option value="intermediate">进阶</option>
                      <option value="advanced">高级</option>
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm text-slate-600 mb-1">分类（必填，可选下拉或自定义）</label>
                    <input
                      v-model="form.category"
                      list="tutorial-category-options"
                      class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      placeholder="选择或输入分类"
                    />
                    <datalist id="tutorial-category-options">
                      <option v-for="cat in (props.categories || [])" :key="cat" :value="cat" />
                    </datalist>
                  </div>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">关联工具（可选）</label>
                  <select
                    v-model.number="form.toolId"
                    class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                  >
                    <option :value="undefined">不关联工具</option>
                    <option
                      v-for="tool in tools || []"
                      :key="tool.id"
                      :value="tool.id"
                    >
                      {{ tool.name }}
                    </option>
                  </select>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">Banner 图（可选）</label>
                  <div class="flex flex-wrap items-center gap-3">
                    <input
                      ref="bannerInputRef"
                      type="file"
                      accept="image/*"
                      class="hidden"
                      @change="onBannerFileChange"
                    />
                    <button
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 disabled:opacity-50"
                      :disabled="bannerUploading || bannerGenerating"
                      @click="triggerBannerSelect"
                    >
                      {{ bannerUploading ? '上传中...' : '上传图片' }}
                    </button>
                    <button
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50"
                      :disabled="bannerUploading || bannerGenerating || !form.title.trim() || !form.description.trim()"
                      @click="generateBanner"
                    >
                      {{ bannerGenerating ? '生成中...' : 'AI 生成' }}
                    </button>
                    <button
                      v-if="form.coverImage"
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                      :disabled="bannerUploading || bannerGenerating"
                      @click="clearBanner"
                    >
                      清除
                    </button>
                    <span v-if="!form.coverImage" class="text-xs text-slate-400">未设置</span>
                  </div>

                  <div v-if="form.coverImage" class="mt-3">
                    <img
                      :src="apiBaseUrl + form.coverImage"
                      class="w-full h-32 rounded-lg object-cover border border-slate-200"
                      alt="Banner"
                    />
                    <a
                      :href="apiBaseUrl + form.coverImage"
                      target="_blank"
                      class="mt-1 inline-block text-xs text-primary-600 hover:underline break-all"
                    >
                      {{ form.coverImage }}
                    </a>
                  </div>
                  <p class="mt-1 text-xs text-slate-400">可上传图片，或根据标题+简介让 AI 生成（保存到同一上传目录）</p>
                </div>

                <div class="flex items-end">
                  <label class="inline-flex items-center gap-2 text-sm text-slate-700 select-none">
                    <input v-model="form.pinned" type="checkbox" class="h-4 w-4" />
                    置顶
                  </label>
                </div>
              </div>
            </div>

            <!-- Right: content editor -->
            <div>
              <label class="block text-sm text-slate-600 mb-1">正文内容（Markdown）</label>
              <div class="border border-slate-200 rounded-lg overflow-hidden">
                <v-md-editor v-model="form.content" height="500px" />
              </div>
              <p class="mt-2 text-xs text-slate-400">支持 Markdown / 代码高亮</p>
            </div>
          </div>

          <div v-if="error" class="mt-4 text-sm text-red-600">
            {{ error }}
          </div>
        </div>

        <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-end gap-3">
          <button @click="$emit('close')" class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200">
            取消
          </button>
          <button
            @click="submit"
            class="px-4 py-2 text-sm rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="saving"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'
import type { Tutorial, Tool } from '@/types'
import { uploadApi } from '@/api/upload.api'
import { llmApi } from '@/api/llm.api'

// @ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

type TutorialDraft = {
  id?: number
  title: string
  description: string
  content: string
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  category?: string
  /** 关联的工具ID，可选 */
  toolId?: number
  /** Banner / 封面图（虚拟路径） */
  coverImage?: string
  pinned: boolean
}

const props = defineProps<{
  open: boolean
  mode: 'add' | 'edit'
  tutorial?: Tutorial | null
  saving: boolean
  /** 可选的工具列表，用于下拉框 */
  tools?: Tool[]
  /** 可选的分类名称数组，用于 datalist 建议 */
  categories?: string[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', payload: TutorialDraft): void
}>()

const error = ref('')

const form = reactive<TutorialDraft>({
  id: undefined,
  title: '',
  description: '',
  content: '',
  difficulty: 'beginner',
  category: '',
  toolId: undefined,
  coverImage: '',
  pinned: false
})

const bannerInputRef = ref<HTMLInputElement | null>(null)
const bannerUploading = ref(false)
const bannerGenerating = ref(false)

watch(
  () => props.open,
  (open) => {
    if (!open) return
    error.value = ''
    const t = props.tutorial
    if (props.mode === 'edit' && t) {
      form.id = t.id
      form.title = t.title || ''
      form.description = t.description || ''
      form.content = t.content || ''
      form.difficulty = t.difficulty || 'beginner'
      form.category = t.category || ''
      form.toolId = (t as any).toolId ?? undefined
      form.coverImage = (t as any).coverImage || (t as any).cover_image || ''
      form.pinned = !!(t as any).pinned
    } else {
      form.id = undefined
      form.title = ''
      form.description = ''
      form.content = ''
      form.difficulty = 'beginner'
      form.category = ''
      form.toolId = undefined
      form.coverImage = ''
      form.pinned = false
    }
  },
  { immediate: true }
)

const triggerBannerSelect = () => {
  if (bannerUploading.value || bannerGenerating.value) return
  bannerInputRef.value?.click()
}

const clearBanner = () => {
  if (bannerUploading.value || bannerGenerating.value) return
  form.coverImage = ''
}

const onBannerFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  // reset input so selecting same file again triggers change
  input.value = ''

  if (!file.type.startsWith('image/')) {
    ElMessage.error('只允许上传图片文件')
    return
  }

  bannerUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file, 'tutorial-banner')
    form.coverImage = res.url
    ElMessage.success('Banner 上传成功')
  } catch (err) {
    console.error('Banner upload failed:', err)
    ElMessage.error('Banner 上传失败，请稍后重试')
  } finally {
    bannerUploading.value = false
  }
}

const generateBanner = async () => {
  if (bannerUploading.value || bannerGenerating.value) return
  if (!form.title.trim() || !form.description.trim()) {
    ElMessage.warning('请先填写标题和简介')
    return
  }

  bannerGenerating.value = true
  try {
    const res = await llmApi.generateTutorialBanner({
      title: form.title.trim(),
      description: form.description.trim()
    })
    form.coverImage = res.url
    ElMessage.success('Banner 生成成功')
  } catch (err: any) {
    console.error('Banner generate failed:', err)
    ElMessage.error(err?.message || 'Banner 生成失败，请稍后重试')
  } finally {
    bannerGenerating.value = false
  }
}

const submit = () => {
  error.value = ''
  if (!form.title.trim()) {
    error.value = '请输入教程标题'
    return
  }
  if (!form.description.trim()) {
    error.value = '请输入教程简介'
    return
  }
  if (!form.content.trim()) {
    error.value = '请输入教程内容'
    return
  }
  if (!form.category || !form.category.trim()) {
    error.value = '请选择或输入分类'
    return
  }
  emit('save', { ...form })
}
</script>

