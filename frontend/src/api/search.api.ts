import request from '@/utils/request.api'

export interface SearchIndexItem {
  entityType: string
  entityId: number
  title: string
  content: string
  createdAt: string
  updatedAt?: string
}

export const searchApi = {
  search: (q?: string) =>
    request.get<SearchIndexItem[]>('/search', { params: { q } })
}
