<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl w-full max-w-4xl overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-900">{{ mode === 'edit' ? '编辑 API' : '新增 API' }}</h3>
          <button @click="$emit('close')" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <div class="p-6 max-h-[75vh] overflow-y-auto">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="md:col-span-2">
              <label class="block text-sm text-slate-600 mb-1">API 名称</label>
              <input v-model="form.name" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                     placeholder="如：OpenAI API"/>
            </div>

            <div>
              <label class="block text-sm text-slate-600 mb-1">提供商</label>
              <input
                v-model="form.provider"
                list="api-provider-options"
                class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                placeholder="如：OpenAI"
              />
              <datalist id="api-provider-options">
                <option v-for="p in (providers || [])" :key="p" :value="p"/>
              </datalist>
            </div>

            <div>
              <label class="block text-sm text-slate-600 mb-1">分类</label>
              <input
                v-model="form.category"
                list="api-category-options"
                class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                placeholder="选择或输入分类"
              />
              <datalist id="api-category-options">
                <option v-for="cat in (categories || [])" :key="cat" :value="cat"/>
              </datalist>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm text-slate-600 mb-1">描述</label>
              <textarea v-model="form.description" rows="3"
                        class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                        placeholder="一句话介绍该 API"></textarea>
            </div>

            <div>
              <label class="block text-sm text-slate-600 mb-1">文档链接（可选）</label>
              <input v-model="form.documentation" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                     placeholder="https://..."/>
            </div>

            <div>
              <label class="block text-sm text-slate-600 mb-1">定价</label>
              <select v-model="form.pricing" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
                <option value="free">免费</option>
                <option value="freemium">免费增值</option>
                <option value="paid">付费</option>
              </select>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm text-slate-600 mb-1">Base URL（可选）</label>
              <input v-model="form.baseUrl" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                     placeholder="https://api.example.com"/>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm text-slate-600 mb-1">API Key（可选，编辑时不修改则保留原值）</label>
              <input v-model="form.apiKey" type="password" autocomplete="off"
                     class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                     :placeholder="mode === 'edit' ? '留空则保留原 API Key' : '可选'"/>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm text-slate-600 mb-1">Logo 图片（可选）</label>
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
                <img :src="apiBaseUrl + form.logoUrl"
                     class="w-10 h-10 rounded-lg object-cover border border-slate-200"/>
                <a :href="apiBaseUrl + form.logoUrl" target="_blank"
                   class="text-xs text-primary-600 hover:underline break-all">
                  {{ form.logoUrl }}
                </a>
              </div>
            </div>

            <div class="flex items-end">
              <label class="inline-flex items-center gap-2 text-sm text-slate-700 select-none">
                <input v-model="form.defaultImageProvider" type="checkbox" class="h-4 w-4"/>
                默认图像大模型提供商（用于 Banner 图生成）
              </label>
            </div>
          </div>

          <!-- 模型管理（仅编辑时显示） -->
          <div v-if="mode === 'edit' && form.id" class="mt-8 pt-6 border-t border-slate-200">
            <h4 class="text-sm font-semibold text-slate-800 mb-3">模型管理</h4>
            <div class="mb-3 flex justify-end">
              <button
                type="button"
                class="px-3 py-1.5 text-sm rounded-lg bg-primary-600 text-white hover:bg-primary-700"
                @click="openModelForm()"
              >
                添加模型
              </button>
            </div>
            <div class="rounded-lg border border-slate-200 overflow-hidden">
              <table class="w-full text-sm">
                <thead class="bg-slate-50 border-b border-slate-200">
                  <tr>
                    <th class="px-3 py-2 text-left font-medium text-slate-600">名称</th>
                    <th class="px-3 py-2 text-left font-medium text-slate-600">描述</th>
                    <th class="px-3 py-2 text-left font-medium text-slate-600">上下文</th>
                    <th class="px-3 py-2 text-left font-medium text-slate-600">推荐</th>
                    <th class="px-3 py-2 text-left font-medium text-slate-600">图像模型</th>
                    <th class="px-3 py-2 text-right font-medium text-slate-600 w-24">操作</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                  <tr v-for="m in models" :key="m.id" class="hover:bg-slate-50">
                    <td class="px-3 py-2 font-medium text-slate-900">{{ m.name }}</td>
                    <td class="px-3 py-2 text-slate-600 max-w-[200px] truncate">{{ m.description || '—' }}</td>
                    <td class="px-3 py-2 text-slate-600">{{ m.contextLength || '—' }}</td>
                    <td class="px-3 py-2">
                      <span class="text-xs px-1.5 py-0.5 rounded" :class="m.recommended ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'">
                        {{ m.recommended ? '是' : '否' }}
                      </span>
                    </td>
                    <td class="px-3 py-2">
                      <span class="text-xs px-1.5 py-0.5 rounded" :class="m.imageModel ? 'bg-violet-100 text-violet-700' : 'bg-slate-100 text-slate-500'">
                        {{ m.imageModel ? '是' : '否' }}
                      </span>
                    </td>
                    <td class="px-3 py-2 text-right">
                      <button type="button" class="text-primary-600 hover:underline mr-2" @click="openModelForm(m)">编辑</button>
                      <button type="button" class="text-red-600 hover:underline" @click="deleteModel(m.id)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <p v-if="models.length === 0 && !modelFormOpen" class="text-sm text-slate-500 mt-2">暂无模型，可点击「添加模型」</p>

            <!-- 添加/编辑模型表单 -->
            <div v-if="modelFormOpen" class="mt-4 p-4 rounded-lg border border-slate-200 bg-slate-50 space-y-3">
              <h5 class="text-sm font-medium text-slate-700">{{ editingModel ? '编辑模型' : '添加模型' }}</h5>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="md:col-span-2">
                  <label class="block text-xs text-slate-600 mb-1">模型名称</label>
                  <input v-model="modelForm.name" class="w-full px-2 py-1.5 border border-slate-200 rounded text-sm" placeholder="如：gpt-4" />
                </div>
                <div class="md:col-span-2">
                  <label class="block text-xs text-slate-600 mb-1">描述（可选）</label>
                  <input v-model="modelForm.description" class="w-full px-2 py-1.5 border border-slate-200 rounded text-sm" placeholder="简短描述" />
                </div>
                <div>
                  <label class="block text-xs text-slate-600 mb-1">上下文长度（可选）</label>
                  <input v-model.number="modelForm.contextLength" type="number" min="0" class="w-full px-2 py-1.5 border border-slate-200 rounded text-sm" placeholder="如：8192" />
                </div>
                <div>
                  <label class="block text-xs text-slate-600 mb-1">能力（逗号分隔，可选）</label>
                  <input v-model="modelForm.capabilitiesStr" class="w-full px-2 py-1.5 border border-slate-200 rounded text-sm" placeholder="文本,图像" />
                </div>
                <div class="flex items-center gap-4">
                  <label class="inline-flex items-center gap-2 text-sm text-slate-700">
                    <input v-model="modelForm.recommended" type="checkbox" class="h-3.5 w-3.5" />
                    推荐
                  </label>
                  <label class="inline-flex items-center gap-2 text-sm text-slate-700">
                    <input v-model="modelForm.imageModel" type="checkbox" class="h-3.5 w-3.5" />
                    图像生成模型（Banner 用）
                  </label>
                </div>
              </div>
              <div class="flex gap-2 pt-1">
                <button type="button" class="px-3 py-1.5 text-sm rounded bg-slate-200 text-slate-700 hover:bg-slate-300" @click="closeModelForm">取消</button>
                <button type="button" class="px-3 py-1.5 text-sm rounded bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50" :disabled="modelSaving || !modelForm.name.trim()" @click="saveModelForm">
                  {{ modelSaving ? '保存中...' : '保存' }}
                </button>
              </div>
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
import { reactive, watch, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'element-plus/es/components/message/style/css'
import type { ApiInfo, ApiModel } from '@/types'
import { uploadApi } from '@/api/upload.api'
import { adminApisApi } from '@/api/admin.apis.api'

// @ts-ignore
const apiBaseUrl = window.__APP_ENV__.apiBaseUrl

type ApiDraft = {
  id?: number
  name: string
  provider: string
  description: string
  documentation?: string
  pricing: 'free' | 'freemium' | 'paid'
  category: string
  baseUrl?: string
  apiKey?: string
  logoUrl?: string
  defaultImageProvider: boolean
}

const props = defineProps<{
  open: boolean
  mode: 'add' | 'edit'
  api?: ApiInfo | null
  categories?: string[]
  providers?: string[]
  saving: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', payload: ApiDraft): void
}>()

const error = ref('')
const logoInputRef = ref<HTMLInputElement | null>(null)
const logoUploading = ref(false)

// 模型管理（仅编辑时）
const models = ref<ApiModel[]>([])
const modelFormOpen = ref(false)
const editingModel = ref<ApiModel | null>(null)
const modelSaving = ref(false)
const modelForm = reactive({
  id: undefined as number | undefined,
  name: '',
  description: '',
  contextLength: 0 as number,
  capabilitiesStr: '',
  recommended: false,
  imageModel: false
})

const form = reactive<ApiDraft>({
  id: undefined,
  name: '',
  provider: '',
  description: '',
  documentation: '',
  pricing: 'free',
  category: '',
  baseUrl: '',
  apiKey: '',
  logoUrl: '',
  defaultImageProvider: false
})

async function loadModels(apiId: number) {
  try {
    models.value = await adminApisApi.getModels(apiId)
  } catch (e) {
    console.error('加载模型列表失败:', e)
    models.value = []
  }
}

function openModelForm(m?: ApiModel) {
  editingModel.value = m ?? null
  modelFormOpen.value = true
  if (m) {
    modelForm.id = m.id
    modelForm.name = m.name || ''
    modelForm.description = m.description || ''
    modelForm.contextLength = m.contextLength ?? 0
    modelForm.capabilitiesStr = Array.isArray(m.capabilities) ? m.capabilities.join(', ') : (m.capabilities as string || '')
    modelForm.recommended = m.recommended ?? false
    modelForm.imageModel = m.imageModel ?? false
  } else {
    modelForm.id = undefined
    modelForm.name = ''
    modelForm.description = ''
    modelForm.contextLength = 0
    modelForm.capabilitiesStr = ''
    modelForm.recommended = false
    modelForm.imageModel = false
  }
}

function closeModelForm() {
  modelFormOpen.value = false
  editingModel.value = null
}

async function saveModelForm() {
  if (!form.id) return
  modelSaving.value = true
  try {
    const capabilities = modelForm.capabilitiesStr
      ? modelForm.capabilitiesStr.split(',').map(s => s.trim()).filter(Boolean)
      : []
    await adminApisApi.saveModel({
      id: modelForm.id,
      apiId: form.id,
      name: modelForm.name.trim(),
      description: modelForm.description?.trim() || '',
      contextLength: modelForm.contextLength || 0,
      capabilities,
      recommended: modelForm.recommended,
      imageModel: modelForm.imageModel
    })
    ElMessage.success('模型已保存')
    closeModelForm()
    await loadModels(form.id)
  } catch (e) {
    console.error('保存模型失败:', e)
    ElMessage.error('保存模型失败')
  } finally {
    modelSaving.value = false
  }
}

async function deleteModel(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除该模型吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await adminApisApi.removeModel(id)
    ElMessage.success('已删除')
    if (form.id) await loadModels(form.id)
  } catch (e) {
    console.error('删除模型失败:', e)
    ElMessage.error('删除失败')
  }
}

watch(
  () => props.open,
  async (open) => {
    if (!open) return
    error.value = ''
    modelFormOpen.value = false
    models.value = []
    const a = props.api
    if (props.mode === 'edit' && a) {
      form.id = a.id
      form.name = a.name || ''
      form.provider = a.provider || ''
      form.description = a.description || ''
      form.documentation = a.documentation || ''
      form.pricing = (a.pricing as ApiDraft['pricing']) || 'free'
      form.category = a.category || ''
      form.baseUrl = a.baseUrl || ''
      form.apiKey = '' // 不回显敏感信息
      form.logoUrl = (a as { logoUrl?: string }).logoUrl || a.logo || ''
      form.defaultImageProvider = (a as { defaultImageProvider?: boolean }).defaultImageProvider === true
      if (form.id) await loadModels(form.id)
    } else {
      form.id = undefined
      form.name = ''
      form.provider = ''
      form.description = ''
      form.documentation = ''
      form.pricing = 'free'
      form.category = ''
      form.baseUrl = ''
      form.apiKey = ''
      form.logoUrl = ''
      form.defaultImageProvider = false
    }
  },
  { immediate: true }
)

const submit = () => {
  error.value = ''
  if (!form.name.trim()) return (error.value = '请输入 API 名称')
  if (!form.provider.trim()) return (error.value = '请输入提供商')
  if (!form.description.trim()) return (error.value = '请输入描述')
  emit('save', { ...form })
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
  input.value = ''

  if (!file.type.startsWith('image/')) {
    ElMessage.error('只允许上传图片文件')
    return
  }

  logoUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file, 'images')
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
