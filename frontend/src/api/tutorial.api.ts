import type { Tutorial } from '@/types'
import request from "@/utils/request.api";

export const tutorialsApi = {
  getTutorials: (params?: { page?: number; size?: number }) =>
      request.get<{ content: Tutorial[]; total: number }>('/tutorials', { params }),

  getTutorial: (id: number) =>
      request.get<Tutorial>(`/tutorials/${id}`),

  /**
   * 获取教程分页列表，支持搜索、筛选和排序
   * @param onlyLikedByMe 仅我的收藏（需登录）
   */
  getTutorialsPage: (
    page: number = 0,
    size: number = 10,
    query?: string,
    difficulty?: string,
    toolId?: number | string,
    category?: string,
    sortBy?: string,
    onlyLikedByMe?: boolean
  ) =>
      request.get<{ content: Tutorial[]; total: number }>('/tutorials/page', {
      params: { page, size, query, difficulty, toolId, category, sortBy, onlyLikedByMe }
    }),

  getRelatedTutorials: (toolId: number) =>
      request.get<Tutorial[]>('/tutorials/related', {
      params: { toolId }
    }),

  incrementViewCount: (id: number) =>
      request.post<void>(`/tutorials/${id}/view`)
}