import request from '@/utils/request.api'
import type { PageResponse, Tool } from '@/types'

export const adminToolsApi = {
  getToolsPage: async (params: { page?: number; size?: number; keyword?: string; category?: string; price?: string }) => {
    const { page = 0, size = 50, ...filters } = params
    // 后端：POST /api/tools?page=&size=，body 为 filters map
    return (await request.post('/tools', filters, { params: { page, size } })) as unknown as PageResponse<Tool>
  },

  getCategories: async () => {
    return (await request.get('/tools/categories')) as unknown as string[]
  },

  save: async (tool: Partial<Tool> & { id?: number }) => {
    // 后端：POST /api/tools/save （新增/更新同一个接口）
    return (await request.post('/tools/save', tool)) as unknown as Tool
  },

  remove: async (id: number) => {
    return (await request.delete(`/tools/${id}`)) as unknown as void
  }
}

