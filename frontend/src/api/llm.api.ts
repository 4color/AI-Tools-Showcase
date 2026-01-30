import request from '@/utils/request.api'

export interface LlmMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

export interface LlmChatRequest {
  apiId: number
  modelId: number
  messages: LlmMessage[]
  temperature?: number
  topP?: number
  maxTokens?: number
}

export interface LlmChatResponse {
  content: string
}

export interface LlmImageGenerateRequest {
  /** 类型：tutorial-banner / prompt-banner，决定存储目录与提示词模板 */
  type?: string
  title: string
  description: string
  size?: string
}

export interface LlmImageGenerateResponse {
  url: string
  path: string
  originalName: string
}

export const llmApi = {
  chat: async (payload: LlmChatRequest) => {
    const resp: any = await request.post<any>('/llm/chat', payload)
    // request.api.ts：code===200 返回 data，否则返回整个 response.data
    if (resp && typeof resp === 'object' && resp.code && resp.code !== 200) {
      throw new Error(resp?.message || '对话请求失败')
    }
    return resp as LlmChatResponse
  },

  /**
   * 生成教程 Banner 图（后端会选默认图像提供商 + 图像模型，并保存到 uploads）
   */
  generateTutorialBanner: async (payload: LlmImageGenerateRequest) => {
    const resp: any = await request.post<any>('/llm/image/generate', { ...payload, type: payload.type || 'tutorial-banner' })
    if (resp && typeof resp === 'object' && resp.code && resp.code !== 200) {
      throw new Error(resp?.message || 'Banner 生成失败')
    }
    return resp as LlmImageGenerateResponse
  },

  /**
   * 生成 Banner 图（按 type 使用对应配置：tutorial-banner / prompt-banner）
   */
  generateBanner: async (type: string, payload: { title: string; description: string; size?: string }) => {
    const resp: any = await request.post<any>('/llm/image/generate', { type, ...payload })
    if (resp && typeof resp === 'object' && resp.code && resp.code !== 200) {
      throw new Error(resp?.message || 'Banner 生成失败')
    }
    return resp as LlmImageGenerateResponse
  },

  /**
   * 生成效果图（以提示词内容为 AI 生成来源，type=prompt-effect）
   */
  generateEffectImage: async (content: string) => {
    const resp: any = await request.post<any>('/llm/image/generate', { type: 'prompt-effect', content })
    if (resp && typeof resp === 'object' && resp.code && resp.code !== 200) {
      throw new Error(resp?.message || '效果图生成失败')
    }
    return resp as LlmImageGenerateResponse
  },

  /**
   * 流式对话（SSE）。后端会不断推送 token 事件。
   */
  chatStream: async (
    payload: LlmChatRequest,
    onToken: (token: string) => void
  ) => {
    const token = localStorage.getItem('token')

    const res = await fetch('/api/llm/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {})
      },
      body: JSON.stringify(payload)
    })

    if (!res.ok || !res.body) {
      throw new Error(`对话请求失败(${res.status})`)
    }

    const reader = res.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })

      // SSE 按空行分隔事件
      const parts = buffer.split('\n\n')
      buffer = parts.pop() || ''

      for (const part of parts) {
        const lines = part.split('\n')
        const dataLines: string[] = []

        for (const rawLine of lines) {
          if (!rawLine.startsWith('data:')) continue
          let data = rawLine.slice('data:'.length)
          if (data.startsWith(' ')) data = data.slice(1)
          dataLines.push(data)
        }

        if (!dataLines.length) continue
        const data = dataLines.join('\n')

        if (data === '[DONE]') return

        // 后端 token 事件会发送 JSON：{"token":"..."}，以保留空格/换行
        try {
          const obj = JSON.parse(data)
          if (obj?.token !== undefined) {
            onToken(String(obj.token))
            continue
          }
          if (obj?.message !== undefined) {
            throw new Error(String(obj.message))
          }
        } catch {
          // 非 JSON，按纯文本处理
        }
        onToken(data)
      }
    }
  }
}

