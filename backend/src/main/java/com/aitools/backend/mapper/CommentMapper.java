package com.aitools.backend.mapper;

import com.aitools.backend.entity.Comment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    default List<Comment> findByEntity(String entityType, Long entityId) {
        return selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getEntityType, entityType)
                        .eq(Comment::getEntityId, entityId)
                        .orderByDesc(Comment::getCreatedAt)
        );
    }

    default Double findAverageRating(String entityType, Long entityId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .eq("entity_type", entityType)
                .eq("entity_id", entityId)
                .isNotNull("rating")
                .select("AVG(rating)");
        List<Object> list = selectObjs(wrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        Object o = list.get(0);
        return o == null ? null : ((Number) o).doubleValue();
    }

    default Integer countRatings(String entityType, Long entityId) {
        return Math.toIntExact(selectCount(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getEntityType, entityType)
                        .eq(Comment::getEntityId, entityId)
                        .isNotNull(Comment::getRating)
        ));
    }
}
