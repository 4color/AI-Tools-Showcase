import request from '@/utils/request.api'
import type { PageResponse, Prompt } from '@/types'

export const adminPromptsApi = {
  getPromptsPage: async (params: {
    page?: number
    size?: number
    query?: string
    category?: string
    tags?: string
    sortBy?: string
  }) => {
    const {
      page = 0,
      size = 10,
      query,
      category,
      tags,
      sortBy = 'latest'
    } = params

    return (await request.get('/prompts/page', {
      params: {
        page,
        size,
        query: query || undefined,
        category: category || undefined,
        tags: tags || undefined,
        sortBy
      }
    })) as unknown as PageResponse<Prompt>
  },

  save: async (prompt: Partial<Prompt> & { id?: number }) => {
    // 后端：POST /prompts/save
    return (await request.post('/prompts/save', prompt)) as unknown as Prompt
  },

  remove: async (id: number) => {
    return (await request.delete(`/prompts/${id}`)) as unknown as void
  }
}

