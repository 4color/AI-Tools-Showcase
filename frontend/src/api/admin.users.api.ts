import request from '@/utils/request.api'
import type { PageResponse, User } from '@/types'

export const adminUsersApi = {
  getUsersPage: async (params: {
    page?: number
    size?: number
    keyword?: string
  }) => {
    const { page = 0, size = 10, keyword } = params
    const res = await request.get('/admin/users', {
      params: { page, size, keyword }
    })
    return res as unknown as PageResponse<User>
  },

  updateUser: async (id: number, payload: { role?: string; officeId?: string }) => {
    return (await request.put(`/admin/users/${id}`, payload)) as unknown as User
  },

  remove: async (id: number) => {
    await request.delete(`/admin/users/${id}`)
  }
}
