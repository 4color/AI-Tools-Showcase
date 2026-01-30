import type {PageResponse, Tool} from '@/types'
import request from "@/utils/request.api";

export const toolsApi = {
  getTools: (filters?: { category?: string; keyword?: string; price?: string; sort?: string; order?: string; onlyLikedByMe?: boolean }, pagination?: { page?: number; size?: number }) =>
      request.post<PageResponse<Tool>>('/tools', {
        ...filters,
        page: pagination?.page ?? 0,
        size: pagination?.size ?? 10
      }),

  getTool: (id: number) =>
      request.get<Tool>(`/tools/${id}`),

  incrementViewCount: (id: number) =>
      request.post<void>(`/tools/${id}/view`),

  searchTools: (query: string, filters?: any) =>
      request.post<{ content: Tool[]; total: number }>('/tools/page', {
      params: { query, ...filters }
    }),



  getToolsPage: (page: number = 0, size: number = 10) =>
      request.post<{ content: Tool[]; total: number }>('/tools', {
      params: { page, size }
    })
}