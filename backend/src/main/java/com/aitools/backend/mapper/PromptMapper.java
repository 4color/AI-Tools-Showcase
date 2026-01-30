package com.aitools.backend.mapper;

import com.aitools.backend.entity.Prompt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PromptMapper extends BaseMapper<Prompt> {

    @Update("UPDATE prompt SET views = views + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    default List<Prompt> findByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapper<Prompt>().eq(Prompt::getCategoryId, categoryId));
    }

    default List<Prompt> searchPrompts(String query) {
        return selectList(new LambdaQueryWrapper<Prompt>()
                .like(Prompt::getTitle, query)
                .or()
                .like(Prompt::getDescription, query)
                .or()
                .like(Prompt::getContent, query));
    }

    default List<Prompt> findTopByOrderByLikesDesc(int limit) {
        return selectList(new LambdaQueryWrapper<Prompt>()
                .orderByDesc(Prompt::getLikes)
                .last("LIMIT " + limit));
    }

    default List<Prompt> findTopByOrderByViewsDesc(int limit) {
        return selectList(new LambdaQueryWrapper<Prompt>()
                .orderByDesc(Prompt::getViews)
                .last("LIMIT " + limit));
    }

    default List<Prompt> findTopByOrderByCreatedAtDesc(int limit) {
        return selectList(new LambdaQueryWrapper<Prompt>()
                .orderByDesc(Prompt::getCreatedAt)
                .last("LIMIT " + limit));
    }
}
