import request from '@/utils/request.api'
import type { ApiInfo, ApiModel, PageResponse } from '@/types'

/** 后台接口返回的模型可能 capabilities 为逗号分隔字符串 */
export type ApiModelRow = Omit<ApiModel, 'capabilities'> & { capabilities?: string | string[] }

function normalizeModel(m: ApiModelRow): ApiModel {
  const caps = m.capabilities
  const capabilities = Array.isArray(caps) ? caps : (typeof caps === 'string' && caps ? caps.split(',').map(s => s.trim()) : [])
  return {
    id: m.id,
    apiId: m.apiId,
    name: m.name || '',
    description: m.description || '',
    contextLength: m.contextLength ?? 0,
    capabilities,
    recommended: m.recommended ?? false,
    imageModel: (m as { imageModel?: boolean }).imageModel ?? false
  }
}

export const adminApisApi = {
  getApisPage: async (params: {
    page?: number
    size?: number
    category?: string
    provider?: string
    keyword?: string
  }) => {
    const { page = 0, size = 10, category, provider, keyword } = params
    const res = await request.get('/apis/page', {
      params: { page, size, category, provider, keyword }
    })
    return res as unknown as PageResponse<ApiInfo>
  },

  getCategories: async (): Promise<string[]> => {
    const list = await request.get<{ name: string }[]>('/categories/getByType/api')
    return (list || []).map((c: { name: string }) => c.name)
  },

  getProviders: async (): Promise<string[]> => {
    return (await request.get('/apis/providers')) as unknown as string[]
  },

  save: async (api: Partial<ApiInfo> & { id?: number; category?: string; apiKey?: string }) => {
    return (await request.post('/apis/save', api)) as unknown as ApiInfo
  },

  remove: async (id: number) => {
    await request.delete(`/apis/${id}`)
  },

  /** 获取某 API 下的模型列表 */
  getModels: async (apiId: number): Promise<ApiModel[]> => {
    const list = (await request.get<ApiModelRow[]>(`/apis/${apiId}/models`)) as ApiModelRow[] | undefined
    return (list || []).map(normalizeModel)
  },

  /** 保存模型（新增需传 apiId，更新需传 id + apiId） */
  saveModel: async (model: Partial<ApiModel> & { apiId: number; id?: number }) => {
    const payload = {
      ...model,
      capabilities: Array.isArray(model.capabilities)
        ? model.capabilities.join(',')
        : (model.capabilities as string | undefined) ?? ''
    }
    return (await request.post('/apis/models/save', payload)) as unknown as ApiModelRow
  },

  /** 删除模型 */
  removeModel: async (id: number) => {
    await request.delete(`/apis/models/${id}`)
  }
}
