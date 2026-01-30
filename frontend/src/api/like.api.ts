import request from "@/utils/request.api";

export const likeApi = {
  // 获取收藏数量
  getLikeCount: (entityType: string, entityId: number) =>
    request.get<number>(`/likes/${entityType}/${entityId}/count`),

  // 检查当前用户是否已收藏
  isLiked: (entityType: string, entityId: number) =>
    request.get<boolean>(`/likes/${entityType}/${entityId}/status`),

  // 切换收藏状态（收藏/取消收藏）
  toggleLike: (entityType: string, entityId: number) =>
    request.post<boolean>(`/likes/${entityType}/${entityId}`)
}
