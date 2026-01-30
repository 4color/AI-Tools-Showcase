package com.aitools.backend.mapper;

import com.aitools.backend.entity.Favorite;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    default Integer countByEntity(String entityType, Long entityId) {
        return Math.toIntExact(selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getEntityType, entityType)
                        .eq(Favorite::getEntityId, entityId)
        ));
    }

    default Integer countByUserAndEntity(Long userId, String entityType, Long entityId) {
        return Math.toIntExact(selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getEntityType, entityType)
                        .eq(Favorite::getEntityId, entityId)
        ));
    }

    default List<Favorite> findByUserId(Long userId) {
        return selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt)
        );
    }
}
