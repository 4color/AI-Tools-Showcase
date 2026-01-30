<template>
  <div class="space-y-6">
    <div class="grid lg:grid-cols-3 gap-6">
      <!-- Chat Interface -->
      <div class="lg:col-span-2 bg-white rounded-xl border border-slate-100 overflow-hidden">
        <div class="bg-slate-50 px-4 py-3 border-b border-slate-100">
          <h3 class="font-semibold text-slate-900">对话测试</h3>
          <p class="text-sm text-slate-500">选择模型并开始测试</p>
        </div>

        <!-- Model Selector -->
        <div class="px-4 py-3 border-b border-slate-100">
          <select v-model="selectedModel" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500">
            <option value="">选择模型...</option>
            <option v-for="model in models" :key="model.id" :value="model.id">{{ model.name }}</option>
          </select>
        </div>

        <!-- Chat Messages -->
        <div ref="chatScrollRef" class="h-96 overflow-y-auto p-4 space-y-4">
          <div v-if="messages.length === 0" class="text-center py-16 text-slate-500">
            <svg class="w-12 h-12 mx-auto text-slate-300 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            <p>输入消息开始对话测试</p>
          </div>
          <div v-for="(msg, index) in messages" :key="index" class="flex gap-3" :class="msg.role === 'user' ? 'flex-row-reverse' : ''">
            <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0" :class="msg.role === 'user' ? 'bg-primary-600 text-white' : 'bg-slate-200 text-slate-600'">
              <svg v-if="msg.role === 'assistant'" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 3a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm0 16a1 1 0 1 1 0-2 1 1 0 0 1 0 2z"/></svg>
              <svg v-else class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="max-w-[70%] rounded-xl px-4 py-2" :class="msg.role === 'user' ? 'bg-primary-600 text-white' : 'bg-slate-100 text-slate-900'">
              <p v-if="msg.role === 'user'" class="text-sm whitespace-pre-wrap">{{ msg.content }}</p>
              <div v-else class="text-sm api-markdown" v-html="renderMarkdown(msg.content)"></div>
            </div>
          </div>
          <div v-if="isLoading" class="flex items-center gap-2 text-slate-500">
            <svg class="w-4 h-4 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10" stroke-dasharray="60" stroke-dashoffset="20"/></svg>
            <span class="text-sm">思考中...</span>
          </div>
        </div>

        <!-- Input -->
        <div class="p-4 border-t border-slate-100">
          <div class="flex gap-3">
            <input
              v-model="userInput"
              type="text"
              placeholder="输入消息..."
              class="flex-1 px-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              @keyup.enter="sendMessage"
            >
            <button
              @click="sendMessage"
              :disabled="!selectedModel || !userInput.trim() || isLoading"
              class="px-4 py-2 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              发送
            </button>
          </div>
        </div>
      </div>

      <!-- Settings Sidebar -->
      <div class="space-y-4">
        <div class="bg-white rounded-xl border border-slate-100 p-6">
          <h3 class="font-semibold text-slate-900 mb-4">参数设置</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-slate-600 mb-1">Temperature</label>
              <input v-model.number="chatParams.temperature" type="range" min="0" max="2" step="0.1" class="w-full">
              <div class="flex justify-between text-xs text-slate-400">
                <span>精确</span>
                <span class="font-medium text-slate-600">{{ chatParams.temperature }}</span>
                <span>创意</span>
              </div>
            </div>
            <div>
              <label class="block text-sm text-slate-600 mb-1">Max Tokens</label>
              <input v-model.number="chatParams.maxTokens" type="number" min="1" max="4096" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
            </div>
            <div>
              <label class="block text-sm text-slate-600 mb-1">Top P</label>
              <input v-model.number="chatParams.topP" type="range" min="0" max="1" step="0.1" class="w-full">
              <div class="text-right text-xs text-slate-600 mt-1">{{ chatParams.topP }}</div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl border border-slate-100 p-6">
          <h3 class="font-semibold text-slate-900 mb-4">提示词模板</h3>
          <div class="space-y-2">
            <button v-for="template in promptTemplates" :key="template.name" @click="applyTemplate(template.content)" class="w-full text-left px-3 py-2 rounded-lg text-sm hover:bg-slate-50 transition-colors">
              <span class="font-medium text-slate-900">{{ template.name }}</span>
              <p class="text-xs text-slate-500 truncate">{{ template.description }}</p>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref, watch } from 'vue'
import { marked } from 'marked'
import type { ApiModel } from '@/types'
import { llmApi } from '@/api/llm.api'

const props = defineProps<{
  apiId: number
  models: ApiModel[]
  initialModelId?: number
}>()

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const selectedModel = ref<number | ''>('')
const userInput = ref('')
const isLoading = ref(false)
const messages = ref<Message[]>([])
const chatScrollRef = ref<HTMLDivElement | null>(null)

const chatParams = reactive({
  temperature: 0.7,
  maxTokens: 1024,
  topP: 1
})

const promptTemplates = [
  { name: '代码助手', description: '帮助编写和解释代码', content: '请帮我编写一段代码，实现[功能描述]。请解释你的实现思路。' },
  { name: '文案生成', description: '生成营销文案', content: '请为[产品名称]生成一段吸引人的营销文案，目标受众是[受众描述]。' },
  { name: '翻译', description: '多语言翻译', content: '请将以下文本翻译成[目标语言]：[待翻译文本]' },
  { name: '总结', description: '长文本摘要', content: '请总结以下文本的要点：[长文本]' }
]

const applyTemplate = (content: string) => {
  userInput.value = content
}

const renderMarkdown = (content: string) => {
  if (!content) return ''
  return marked.parse(content, { breaks: true }) as string
}

let scrollRaf = 0
const scrollToBottom = async () => {
  if (scrollRaf) return
  scrollRaf = window.requestAnimationFrame(async () => {
    scrollRaf = 0
    await nextTick()
    const el = chatScrollRef.value
    if (!el) return
    el.scrollTop = el.scrollHeight
  })
}

watch(
  () => messages.value.length,
  () => {
    // 新增消息时自动滚到底部
    scrollToBottom()
  }
)

const sendMessage = async () => {
  if (!userInput.value.trim() || !selectedModel.value || isLoading.value) return

  const content = userInput.value
  messages.value.push({ role: 'user', content })
  userInput.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    // 先插入一条空的 assistant 消息，流式拼接内容
    messages.value.push({ role: 'assistant', content: '' })
    const assistantIndex = messages.value.length - 1
    scrollToBottom()

    await llmApi.chatStream(
      {
        apiId: props.apiId,
        modelId: Number(selectedModel.value),
        messages: messages.value.slice(0, assistantIndex),
        temperature: chatParams.temperature,
        topP: chatParams.topP,
        maxTokens: chatParams.maxTokens
      },
      (token) => {
        messages.value[assistantIndex].content += token
        scrollToBottom()
      }
    )
  } catch (err) {
    console.error('对话失败:', err)
    messages.value.push({ role: 'assistant', content: '对话失败，请检查该 API 的 baseUrl / apiKey 是否配置正确。' })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

watch(
  () => props.initialModelId,
  (id) => {
    if (id && !selectedModel.value) {
      selectedModel.value = id
    }
  },
  { immediate: true }
)
</script>

<style scoped>
/* markdown 基础排版：解决“文字堆在一起” */
.api-markdown {
  line-height: 1.7;
  white-space: normal;
  word-break: break-word;
}

.api-markdown :deep(p) {
  display: block;
  margin: 0.75rem 0;
}

.api-markdown :deep(br) {
  content: '';
  display: block;
  margin: 0.5rem 0;
}

.api-markdown :deep(ul),
.api-markdown :deep(ol) {
  display: block;
  padding-left: 1.25rem;
  margin: 0.75rem 0;
}
.api-markdown :deep(ul) { list-style: disc; }
.api-markdown :deep(ol) { list-style: decimal; }

.api-markdown :deep(li) {
  margin: 0.25rem 0;
}

.api-markdown :deep(code) {
  background: rgba(148, 163, 184, 0.25);
  padding: 0.1rem 0.25rem;
  border-radius: 0.25rem;
}

.api-markdown :deep(pre) {
  background: #0f172a;
  color: #e2e8f0;
  padding: 0.9rem;
  border-radius: 0.75rem;
  overflow: auto;
  margin: 0.9rem 0;
}
.api-markdown :deep(pre code) {
  background: transparent;
  padding: 0;
}

.api-markdown :deep(blockquote) {
  margin: 0.75rem 0;
  padding-left: 0.75rem;
  border-left: 3px solid rgba(148, 163, 184, 0.8);
  color: rgba(15, 23, 42, 0.8);
}

.api-markdown :deep(a) {
  color: #2563eb;
  text-decoration: underline;
}

.api-markdown :deep(h1),
.api-markdown :deep(h2),
.api-markdown :deep(h3),
.api-markdown :deep(h4) {
  font-weight: 600;
  margin: 0.9rem 0 0.5rem;
}

.api-markdown :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 0.75rem 0;
}
.api-markdown :deep(th),
.api-markdown :deep(td) {
  border: 1px solid rgba(226, 232, 240, 1);
  padding: 0.35rem 0.5rem;
}
</style>

