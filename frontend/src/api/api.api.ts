import type { ApiInfo } from '@/types'
import request from '@/utils/request.api'

/** API 分类项（与教程分类接口一致：getByType） */
export type ApiCategoryItem = { id: number; name: string; description?: string }

export const apisApi = {
  getApis: (params?: { category?: string; provider?: string; keyword?: string }) =>
    request.get<ApiInfo[]>('/apis', { params }),

  getApi: (id: number) =>
    request.get<ApiInfo>(`/apis/${id}`),

  getApisPage: (page: number = 0, size: number = 10, filters?: { category?: string; provider?: string; keyword?: string }) =>
    request.get<{ content: ApiInfo[]; total: number }>('/apis/page', {
      params: { page, size, ...filters }
    }),

  /** 获取 API 分类列表（参考教程：/categories/getByType/{type}） */
  getCategories: (): Promise<ApiCategoryItem[]> =>
    request.get<ApiCategoryItem[]>('/categories/getByType/api').then((list: unknown) =>
      (Array.isArray(list) ? list : []).map((c: { id: number; name: string; description?: string }) => ({
        id: c.id,
        name: c.name,
        description: c.description
      }))
    ),

  /** 获取 API 提供商列表 */
  getProviders: (): Promise<string[]> =>
    request.get<string[]>('/apis/providers').then((data: unknown) => (Array.isArray(data) ? data : []))
}