import request from "@/utils/request.api";

export interface Comment {
  id: number
  userId: number
  entityType: 'tool' | 'tutorial' | 'api' | 'prompt'
  entityId: number
  content: string
  rating?: number
  createdAt: string
  updatedAt?: string
  username?: string
  userAvatar?: string
}

export const commentApi = {
  /**
   * 获取评论列表
   * @param entityType 实体类型：tool, tutorial, api
   * @param entityId 实体ID
   */
  getComments: (entityType: string, entityId: number) =>
    request.get<Comment[]>(`/comments/${entityType}/${entityId}`),

  /**
   * 添加评论
   */
  addComment: (comment: {
    entityType: string
    entityId: number
    content: string
    rating?: number
  }) =>
    request.post<Comment>('/comments', comment),

  /**
   * 更新评论
   */
  updateComment: (id: number, comment: {
    content: string
    rating?: number
  }) =>
    request.put<Comment>(`/comments/${id}`, comment),

  /**
   * 删除评论
   */
  deleteComment: (id: number) =>
    request.delete<void>(`/comments/${id}`),

  /**
   * 获取平均评分
   */
  getAverageRating: (entityType: string, entityId: number) =>
    request.get<number>(`/comments/${entityType}/${entityId}/rating`)
}
