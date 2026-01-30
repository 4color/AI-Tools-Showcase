import request from '@/utils/request.api'

export interface HomeStats {
  toolCount: number
  tutorialCount: number
  userCount: number
  averageRating: number
}

export const homeApi = {
  getStats: () => request.get<HomeStats>('/stats'),
}
