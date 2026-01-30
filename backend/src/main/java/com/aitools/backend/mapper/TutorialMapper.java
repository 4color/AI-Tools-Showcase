package com.aitools.backend.mapper;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.entity.Tutorial;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collections;
import java.util.List;

/**
 * 教程 Mapper 接口
 * 所有查询方法均使用 MyBatis-Plus Lambda 表达式实现
 */
@Mapper
public interface TutorialMapper extends BaseMapper<Tutorial> {

    /**
     * 获取所有教程，按置顶和创建时间排序
     */
    default List<Tutorial> selectAllOrderByPinnedAndCreatedAt() {
        return selectList(new LambdaQueryWrapper<Tutorial>()
                .orderByDesc(Tutorial::getPinned)
                .orderByDesc(Tutorial::getCreatedAt));
    }

    /**
     * 按工具 ID 查询教程
     */
    default List<Tutorial> findByToolId(Long toolId) {
        return selectList(new LambdaQueryWrapper<Tutorial>().eq(Tutorial::getToolId, toolId));
    }

    /**
     * 按作者 ID 查询教程
     */
    default List<Tutorial> findByAuthorId(Long authorId) {
        return selectList(new LambdaQueryWrapper<Tutorial>().eq(Tutorial::getAuthorId, authorId));
    }

    /**
     * 按难度查询教程
     */
    default List<Tutorial> findByDifficulty(String difficulty) {
        return selectList(new LambdaQueryWrapper<Tutorial>().eq(Tutorial::getDifficulty, difficulty));
    }

    /**
     * 分页查询教程（基础分页）
     */
    default List<Tutorial> selectPage(PageRequest pageRequest) {
        Page<Tutorial> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        LambdaQueryWrapper<Tutorial> wrapper = new LambdaQueryWrapper<Tutorial>().orderByAsc(Tutorial::getId);
        IPage<Tutorial> result = selectPage(page, wrapper);
        return result.getRecords();
    }

    /**
     * 统计总数（基础统计）
     */
    default Long selectCount(Object condition) {
        return Long.valueOf(selectCount(new LambdaQueryWrapper<Tutorial>()));
    }

    /**
     * 分页查询教程（支持关键词、难度、工具ID筛选及排序）
     * @param sortBy latest-最新, popular-最热门, views-浏览最多（后两者暂按创建时间降序）
     */
    default List<Tutorial> searchTutorials(PageRequest pageRequest, String query, String difficulty, Long toolId, Long categoryId, String sortBy) {
        return searchTutorials(pageRequest, query, difficulty, toolId, categoryId, sortBy, null);
    }

    default List<Tutorial> searchTutorials(PageRequest pageRequest, String query, String difficulty, Long toolId, Long categoryId, String sortBy, List<Long> restrictToTutorialIds) {
        if (restrictToTutorialIds != null && restrictToTutorialIds.isEmpty()) {
            return Collections.emptyList();
        }
        Page<Tutorial> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        LambdaQueryWrapper<Tutorial> wrapper = new LambdaQueryWrapper<>();
        if (restrictToTutorialIds != null) {
            wrapper.in(Tutorial::getId, restrictToTutorialIds);
        }
        if (query != null && !query.trim().isEmpty()) {
            wrapper.and(w -> w.like(Tutorial::getTitle, query)
                    .or().like(Tutorial::getDescription, query)
                    .or().like(Tutorial::getContent, query));
        }
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            wrapper.eq(Tutorial::getDifficulty, difficulty);
        }
        if (toolId != null) {
            wrapper.eq(Tutorial::getToolId, toolId);
        }
        if (categoryId != null) {
            wrapper.eq(Tutorial::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Tutorial::getPinned);
        if ("latest".equalsIgnoreCase(sortBy)) {
            wrapper.orderByDesc(Tutorial::getCreatedAt);
        } else if ("popular".equalsIgnoreCase(sortBy) || "views".equalsIgnoreCase(sortBy)) {
            wrapper.orderByDesc(Tutorial::getCreatedAt);
        } else {
            wrapper.orderByDesc(Tutorial::getCreatedAt);
        }
        IPage<Tutorial> result = selectPage(page, wrapper);
        return result.getRecords();
    }

    /**
     * 按筛选条件统计教程总数
     */
    default Long countTutorialsWithFilters(String query, String difficulty, Long toolId, Long categoryId) {
        return countTutorialsWithFilters(query, difficulty, toolId, categoryId, null);
    }

    default Long countTutorialsWithFilters(String query, String difficulty, Long toolId, Long categoryId, List<Long> restrictToTutorialIds) {
        LambdaQueryWrapper<Tutorial> wrapper = new LambdaQueryWrapper<>();
        if (restrictToTutorialIds != null) {
            if (restrictToTutorialIds.isEmpty()) return 0L;
            wrapper.in(Tutorial::getId, restrictToTutorialIds);
        }
        if (query != null && !query.trim().isEmpty()) {
            wrapper.and(w -> w.like(Tutorial::getTitle, query)
                    .or().like(Tutorial::getDescription, query)
                    .or().like(Tutorial::getContent, query));
        }
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            wrapper.eq(Tutorial::getDifficulty, difficulty);
        }
        if (toolId != null) {
            wrapper.eq(Tutorial::getToolId, toolId);
        }
        if (categoryId != null) {
            wrapper.eq(Tutorial::getCategoryId, categoryId);
        }
        return Long.valueOf(selectCount(wrapper));
    }
}
