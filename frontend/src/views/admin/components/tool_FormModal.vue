<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl w-full max-w-7xl overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-900">{{ mode === 'edit' ? '编辑工具' : '新增工具' }}</h3>
          <button @click="$emit('close')" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <div class="p-6 max-h-[75vh] overflow-y-auto">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Left: basic fields -->
            <div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="md:col-span-2">
                  <label class="block text-sm text-slate-600 mb-1">工具名称</label>
                  <input v-model="form.name" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                         placeholder="如：ChatGPT"/>
                </div>

                <div class="md:col-span-2">
                  <label class="block text-sm text-slate-600 mb-1">描述</label>
                  <textarea v-model="form.description" rows="5"
                            class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                            placeholder="一句话介绍工具"></textarea>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">分类</label>
                  <input
                      v-model="form.category"
                      list="tool-category-options"
                      class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      placeholder="选择或输入分类"
                  />
                  <datalist id="tool-category-options">
                    <option v-for="cat in (categories || [])" :key="cat" :value="cat"/>
                  </datalist>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">价格</label>
                  <select v-model="form.price" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
                    <option value="free">免费</option>
                    <option value="freemium">免费增值</option>
                    <option value="paid">付费</option>
                  </select>
                </div>

                <div class="md:col-span-2">
                  <label class="block text-sm text-slate-600 mb-1">官网链接</label>
                  <input v-model="form.url" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                         placeholder="https://..."/>
                </div>

                <div class="md:col-span-2">
                  <label class="block text-sm text-slate-600 mb-1">Logo（仅图片，可选）</label>
                  <div class="flex items-center gap-3">
                    <input
                        ref="logoInputRef"
                        type="file"
                        accept="image/*"
                        class="hidden"
                        @change="onLogoFileChange"
                    />
                    <button
                        type="button"
                        class="px-3 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 disabled:opacity-50"
                        :disabled="logoUploading"
                        @click="triggerLogoSelect"
                    >
                      {{ logoUploading ? '上传中...' : '上传图片' }}
                    </button>
                    <button
                        v-if="form.logoUrl"
                        type="button"
                        class="px-3 py-2 text-sm rounded-lg bg-white border border-slate-200 text-slate-700 hover:bg-slate-50"
                        :disabled="logoUploading"
                        @click="clearLogo"
                    >
                      清除
                    </button>
                    <span v-if="!form.logoUrl" class="text-xs text-slate-400">未上传</span>
                  </div>

                  <div v-if="form.logoUrl" class="mt-2 flex items-center gap-3">
                    <img :src="apiBaseUrl+form.logoUrl"
                         class="w-10 h-10 rounded-lg object-cover border border-slate-200"/>
                    <a :href="apiBaseUrl+form.logoUrl" target="_blank"
                       class="text-xs text-primary-600 hover:underline break-all">
                      {{ form.logoUrl }}
                    </a>
                  </div>
                </div>

                <div>
                  <label class="block text-sm text-slate-600 mb-1">评分</label>
                  <select v-model.number="form.rating" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
                    <option v-for="n in 6" :key="n - 1" :value="n - 1">{{ n - 1 }}</option>
                  </select>
                </div>

                <div class="flex items-end">
                  <label class="inline-flex items-center gap-2 text-sm text-slate-700 select-none">
                    <input v-model="form.pinned" type="checkbox" class="h-4 w-4"/>
                    置顶
                  </label>
                </div>
              </div>
            </div>

            <!-- Right: introduction editor -->
            <div>
              <label class="block text-sm text-slate-600 mb-1">介绍（可选）</label>
              <div class="border border-slate-200 rounded-lg overflow-hidden">
                <v-md-editor v-model="form.introduction" height="500px"/>
              </div>
              <p class="mt-2 text-xs text-slate-400">支持 Markdown / 代码高亮</p>
            </div>
          </div>

          <div v-if="error" class="mt-4 text-sm text-red-600">
            {{ error }}
          </div>
        </div>

        <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-end gap-3">
          <button @click="$emit('close')"
                  class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200">
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
import {reactive, watch, ref} from 'vue'
import {ElMessage} from 'element-plus'
import 'element-plus/es/components/message/style/css'
import type {Tool} from '@/types'
import {uploadApi} from '@/api/upload.api'

//@ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl;

type ToolDraft = {
  id?: number
  name: string
  description: string
  introduction?: string
  category: string
  price: 'free' | 'freemium' | 'paid'
  url: string
  logoUrl?: string
  rating: number
  pinned: boolean
}

const props = defineProps<{
  open: boolean
  mode: 'add' | 'edit'
  tool?: Tool | null
  categories?: string[]
  saving: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', payload: ToolDraft): void
}>()

const error = ref('')
const logoInputRef = ref<HTMLInputElement | null>(null)
const logoUploading = ref(false)

const form = reactive<ToolDraft>({
  id: undefined,
  name: '',
  description: '',
  introduction: '',
  category: '',
  price: 'free',
  url: '',
  logoUrl: '',
  rating: 0,
  pinned: false
})

watch(
    () => props.open,
    (open) => {
      if (!open) return
      error.value = ''
      const t = props.tool
      if (props.mode === 'edit' && t) {
        form.id = t.id
        form.name = t.name || ''
        form.description = t.description || ''
        form.introduction = t.introduction || ''
        form.category = t.category || ''
        form.price = t.price || 'free'
        form.url = t.url || ''
        form.logoUrl = (t as any).logoUrl || t.logoUrl || ''
        form.rating = typeof t.rating === 'number' ? t.rating : 0
        form.pinned = (t as any).pinned === 1 || (t as any).pinned === true
      } else {
        form.id = undefined
        form.name = ''
        form.description = ''
        form.introduction = ''
        form.category = ''
        form.price = 'free'
        form.url = ''
        form.logoUrl = ''
        form.rating = 0
        form.pinned = false
      }
    },
    {immediate: true}
)

const submit = () => {
  error.value = ''
  if (!form.name.trim()) return (error.value = '请输入工具名称')
  if (!form.description.trim()) return (error.value = '请输入描述')
  if (!form.category.trim()) return (error.value = '请输入分类')
  if (!form.url.trim()) return (error.value = '请输入官网链接')
  if (Number.isNaN(form.rating) || form.rating < 0) form.rating = 0
  if (form.rating > 5) form.rating = 5
  emit('save', {...form})
}

const triggerLogoSelect = () => {
  if (logoUploading.value) return
  logoInputRef.value?.click()
}

const clearLogo = () => {
  if (logoUploading.value) return
  form.logoUrl = ''
}

const onLogoFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  // reset input so selecting same file again triggers change
  input.value = ''

  if (!file.type.startsWith('image/')) {
    ElMessage.error('只允许上传图片文件')
    return
  }

  logoUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file, 'tool-logo')
    form.logoUrl = res.url
    ElMessage.success('Logo 上传成功')
  } catch (err) {
    console.error('Logo upload failed:', err)
    ElMessage.error('Logo 上传失败，请稍后重试')
  } finally {
    logoUploading.value = false
  }
}
</script>


