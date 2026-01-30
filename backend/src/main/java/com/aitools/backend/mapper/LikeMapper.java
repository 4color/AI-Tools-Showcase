package com.aitools.backend.mapper;

import com.aitools.backend.entity.Like;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    default Integer countByEntity(String entityType, Long entityId) {
        return Math.toIntExact(selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getEntityType, entityType)
                        .eq(Like::getEntityId, entityId)
        ));
    }

    default Integer countByUserAndEntity(Long userId, String entityType, Long entityId) {
        return Math.toIntExact(selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getEntityType, entityType)
                        .eq(Like::getEntityId, entityId)
        ));
    }

    /** 查询某用户对某类型实体的收藏/点赞 ID 列表（用于「我的工具」等筛选） */
    default java.util.List<Long> findEntityIdsByUserIdAndEntityType(Long userId, String entityType) {
        if (userId == null || entityType == null || entityType.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return selectList(
                new LambdaQueryWrapper<Like>()
                        .select(Like::getEntityId)
                        .eq(Like::getUserId, userId)
                        .eq(Like::getEntityType, entityType)
        ).stream().map(Like::getEntityId).collect(java.util.stream.Collectors.toList());
    }
}
