import request from '@/utils/request.api'
import type { PageResponse, Tutorial } from '@/types'

export const adminTutorialsApi = {
  getTutorialsPage: async (params: {
    page?: number
    size?: number
    query?: string
    difficulty?: string
    category?: string
    sortBy?: string
  }) => {
    const {
      page = 0,
      size = 10,
      query,
      difficulty,
      category,
      sortBy = 'latest'
    } = params

    return (await request.get('/tutorials/page', {
      params: {
        page,
        size,
        query: query || undefined,
        difficulty: difficulty || undefined,
        category: category || undefined,
        sortBy
      }
    })) as unknown as PageResponse<Tutorial>
  },

  save: async (tutorial: Partial<Tutorial> & { id?: number }) => {
    // 后端：POST /tutorials/save
    return (await request.post('/tutorials/save', tutorial)) as unknown as Tutorial
  },

  remove: async (id: number) => {
    return (await request.delete(`/tutorials/${id}`)) as unknown as void
  }
}

