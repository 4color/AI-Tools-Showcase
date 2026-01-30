import request from '@/utils/request.api'
import type { Prompt, PageResponse } from '@/types'

export const promptsApi = {
  /** 分页列表（与后台同一后端接口） */
  getPromptsPage: (params: {
    page?: number
    size?: number
    query?: string
    category?: string
    tags?: string
    sortBy?: string
  }) => {
    const { page = 0, size = 200, query, category, tags, sortBy = 'latest' } = params
    return request.get<PageResponse<Prompt>>('/prompts/page', {
      params: { page, size, query: query || undefined, category: category || undefined, tags: tags || undefined, sortBy }
    })
  },

  /** 获取提示词列表（兼容旧用法，内部走分页） */
  getPrompts: (params?: { category?: string; keyword?: string; tags?: string }) =>
    request.get<Prompt[]>('/prompts', { params }),

  /** 根据ID获取提示词详情 */
  getPrompt: (id: number) =>
    request.get<Prompt>(`/prompts/${id}`),

  /** 获取热门提示词 */
  getPopularPrompts: (limit?: number) =>
    request.get<Prompt[]>('/prompts/popular', { params: { limit } }),

  /** 获取最新提示词 */
  getLatestPrompts: (limit?: number) =>
    request.get<Prompt[]>('/prompts/latest', { params: { limit } }),

  /** 获取相关提示词 */
  getRelatedPrompts: (id: number, limit?: number) =>
    request.get<Prompt[]>(`/prompts/${id}/related`, { params: { limit } }),

  /** 增加浏览量 */
  incrementViewCount: (id: number) =>
    request.post<void>(`/prompts/${id}/view`),

  /** 获取分类列表（通用，供提示词列表页筛选） */
  getCategories: () =>
    request.get('/categories'),

  /** 获取标签列表（type=prompt 时可传 namesOnly=true） */
  getTags: (params?: { type?: string; namesOnly?: boolean }) =>
    request.get<string[]>('/tags', { params })
}
