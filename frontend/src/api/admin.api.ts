import request from '@/utils/request.api'

export interface AdminDashboardStats {
  tools: number
  tutorials: number
  users: number
  apis: number
  prompts?: number
}

export interface AdminDashboardItem {
  id: number
  title: string
  createdAt: string
  kind: 'tool' | 'tutorial'
}

export interface AdminDashboardResponse {
  stats: AdminDashboardStats
  recent: AdminDashboardItem[]
}

export const adminApi = {
  getDashboard: async (): Promise<AdminDashboardResponse> => {
    // axios typings 会返回 AxiosResponse<T>，这里统一返回拦截器处理后的 data 结构
    return (await request.get('/admin/dashboard')) as unknown as AdminDashboardResponse
  }
}

