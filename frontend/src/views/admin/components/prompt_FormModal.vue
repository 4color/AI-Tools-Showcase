<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl w-full max-w-5xl overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-900">
            {{ mode === 'edit' ? '编辑提示词' : '新增提示词' }}
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
                    placeholder="如：专业文章写作助手"
                  />
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">简介</label>
                  <textarea
                    v-model="form.description"
                    rows="2"
                    class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                    placeholder="一句话介绍本提示词的用途"
                  />
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">提示词内容</label>
                  <textarea
                    v-model="form.content"
                    rows="8"
                    class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm font-mono"
                    placeholder="粘贴或输入完整提示词模版（纯文本）"
                  />
                  <p class="mt-2 text-xs text-slate-400">请使用纯文本，不要使用 Markdown。直接放入可复制的提示词模版。</p>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm text-slate-600 mb-1">分类（必填）</label>
                    <input
                      v-model="form.category"
                      list="prompt-category-options"
                      class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      placeholder="选择或输入分类，如 text/image/code"
                    />
                    <datalist id="prompt-category-options">
                      <option v-for="cat in (categories || [])" :key="cat" :value="cat" />
                    </datalist>
                  </div>
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
                  <p class="mt-1 text-xs text-slate-400">可上传图片，或根据标题+简介让 AI 生成（保存到 prompt-banner 目录）</p>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">生成效果图（可选）</label>
                  <div class="flex flex-wrap items-center gap-3">
                    <input
                      ref="effectInputRef"
                      type="file"
                      accept="image/*"
                      class="hidden"
                      @change="onEffectFileChange"
                    />
                    <button
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 disabled:opacity-50"
                      :disabled="effectUploading || effectGenerating"
                      @click="triggerEffectSelect"
                    >
                      {{ effectUploading ? '上传中...' : '上传图片' }}
                    </button>
                    <button
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50"
                      :disabled="effectUploading || effectGenerating || !form.content.trim()"
                      @click="generateEffect"
                    >
                      {{ effectGenerating ? '生成中...' : 'AI 生成（以提示词为内容）' }}
                    </button>
                    <button
                      v-if="form.effectImage"
                      type="button"
                      class="px-3 py-2 text-sm rounded-lg bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                      :disabled="effectUploading || effectGenerating"
                      @click="form.effectImage = ''"
                    >
                      清除
                    </button>
                    <span v-if="!form.effectImage" class="text-xs text-slate-400">未设置</span>
                  </div>
                  <div v-if="form.effectImage" class="mt-3">
                    <img
                      :src="apiBaseUrl + form.effectImage"
                      class="w-full h-32 rounded-lg object-cover border border-slate-200"
                      alt="效果图"
                    />
                  </div>
                  <p class="mt-1 text-xs text-slate-400">可上传或根据下方「提示词内容」让 AI 生成效果图，详情页会展示在提示词上方。</p>
                </div>
              </div>
            </div>

            <!-- Right: usage guide + tags -->
            <div class="space-y-6">
              <div>
                <label class="block text-sm text-slate-600 mb-1">使用说明（可选）</label>
                <div class="border border-slate-200 rounded-lg overflow-hidden">
                  <v-md-editor v-model="form.usageGuide" height="420px" />
                </div>
                <p class="mt-2 text-xs text-slate-400">支持 Markdown，用于说明如何使用本提示词（步骤、示例、注意事项等）。</p>
              </div>

              <div>
                <label class="block text-sm text-slate-600 mb-1">标签管理</label>
                <el-input-tag
                  v-model="form.tags"
                  placeholder="输入后按回车添加标签，详情页相关提示词按标签匹配"
                  style="width: 100%"
                />
                <p class="mt-2 text-xs text-slate-400">添加标签后，详情页「相关提示词」会按共同标签推荐类似提示词。</p>
              </div>
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
import { ElMessage, ElInputTag } from 'element-plus'
import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/input-tag/style/css'
import type { Prompt } from '@/types'
import { uploadApi } from '@/api/upload.api'
import { llmApi } from '@/api/llm.api'

// @ts-ignore
const apiBaseUrl = window.__APP_ENV__?.apiBaseUrl ?? ''

type PromptDraft = {
  id?: number
  title: string
  description: string
  content: string
  category: string
  /** Banner/封面图虚拟路径 */
  coverImage?: string
  /** 生成效果图虚拟路径（以提示词为 AI 生成来源） */
  effectImage?: string
  /** 使用方法说明（支持 Markdown） */
  usageGuide?: string
  /** 标签（详情页相关提示词按标签匹配） */
  tags?: string[]
}

const props = defineProps<{
  open: boolean
  mode: 'add' | 'edit'
  prompt?: Prompt | null
  saving: boolean
  categories?: string[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', payload: PromptDraft): void
}>()

const error = ref('')

const form = reactive<PromptDraft>({
  id: undefined,
  title: '',
  description: '',
  content: '',
  category: '',
  coverImage: '',
  effectImage: '',
  usageGuide: '',
  tags: []
})

const bannerInputRef = ref<HTMLInputElement | null>(null)
const bannerUploading = ref(false)
const bannerGenerating = ref(false)
const effectInputRef = ref<HTMLInputElement | null>(null)
const effectUploading = ref(false)
const effectGenerating = ref(false)

watch(
  () => props.open,
  (open) => {
    if (!open) return
    error.value = ''
    const p = props.prompt
    if (props.mode === 'edit' && p) {
      form.id = p.id
      form.title = p.title || ''
      form.description = p.description || ''
      form.content = p.content || ''
      form.category = (p as any).category || ''
      form.coverImage = (p as any).coverImage ?? (p as any).cover_image ?? ''
      form.effectImage = (p as any).effectImage ?? (p as any).effect_image ?? ''
      form.usageGuide = (p as any).usageGuide ?? (p as any).usage_guide ?? ''
      form.tags = Array.isArray((p as any).tags) ? [...(p as any).tags] : []
    } else {
      form.id = undefined
      form.title = ''
      form.description = ''
      form.content = ''
      form.category = ''
      form.coverImage = ''
      form.effectImage = ''
      form.usageGuide = ''
      form.tags = []
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

const triggerEffectSelect = () => {
  if (effectUploading.value || effectGenerating.value) return
  effectInputRef.value?.click()
}

const onBannerFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  input.value = ''
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只允许上传图片文件')
    return
  }
  bannerUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file, 'prompt-banner')
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
    const res = await llmApi.generateBanner('prompt-banner', {
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

const onEffectFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  input.value = ''
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只允许上传图片文件')
    return
  }
  effectUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file, 'prompt-effect')
    form.effectImage = res.url
    ElMessage.success('效果图上传成功')
  } catch (err) {
    console.error('Effect image upload failed:', err)
    ElMessage.error('效果图上传失败，请稍后重试')
  } finally {
    effectUploading.value = false
  }
}

const generateEffect = async () => {
  if (effectUploading.value || effectGenerating.value) return
  if (!form.content.trim()) {
    ElMessage.warning('请先填写提示词内容')
    return
  }
  effectGenerating.value = true
  try {
    const res = await llmApi.generateEffectImage(form.content.trim())
    form.effectImage = res.url
    ElMessage.success('效果图生成成功')
  } catch (err: any) {
    console.error('Effect image generate failed:', err)
    ElMessage.error(err?.message || '效果图生成失败，请稍后重试')
  } finally {
    effectGenerating.value = false
  }
}

const submit = () => {
  error.value = ''
  if (!form.title.trim()) {
    error.value = '请输入提示词标题'
    return
  }
  if (!form.description.trim()) {
    error.value = '请输入提示词简介'
    return
  }
  if (!form.content.trim()) {
    error.value = '请输入提示词内容'
    return
  }
  if (!form.category.trim()) {
    error.value = '请选择或输入分类'
    return
  }
  emit('save', { ...form })
}
</script>

