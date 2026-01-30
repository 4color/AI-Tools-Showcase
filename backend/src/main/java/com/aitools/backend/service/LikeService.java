package com.aitools.backend.service;

import com.aitools.backend.entity.Like;
import com.aitools.backend.mapper.LikeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper likeMapper;

    public Integer getLikeCount(String entityType, Long entityId) {
        return likeMapper.countByEntity(entityType, entityId);
    }

    public boolean isLikedByUser(Long userId, String entityType, Long entityId) {
        return likeMapper.countByUserAndEntity(userId, entityType, entityId) > 0;
    }

    public boolean toggleLike(Long userId, String entityType, Long entityId) {
        // 使用查询条件直接查询，而不是查询所有记录再过滤
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId)
                .eq(Like::getEntityType, entityType)
                .eq(Like::getEntityId, entityId);
        
        Like existingLike = likeMapper.selectOne(queryWrapper);

        if (existingLike != null) {
            likeMapper.deleteById(existingLike.getId());
            return false; // unliked
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setEntityType(entityType);
            like.setEntityId(entityId);
            likeMapper.insert(like);
            return true; // liked
        }
    }
}