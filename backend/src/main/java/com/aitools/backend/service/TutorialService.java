package com.aitools.backend.service;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Category;
import com.aitools.backend.entity.Tutorial;
import com.aitools.backend.mapper.CategoryMapper;
import com.aitools.backend.mapper.LikeMapper;
import com.aitools.backend.mapper.TutorialMapper;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.service.SearchIndexService;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
@RequiredArgsConstructor
public class TutorialService {

    private final TutorialMapper tutorialMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LikeMapper likeMapper;
    private final SearchIndexService searchIndexService;

    public List<Tutorial> getAllTutorials() {
        List<Tutorial> tutorials = tutorialMapper.selectAllOrderByPinnedAndCreatedAt();
        enrichCategory(tutorials);
        return tutorials;
    }

    public PageResponse<Tutorial> getTutorialsPage(PageRequest pageRequest, String query, String difficulty, Long toolId, String category, String sortBy) {
        return getTutorialsPage(pageRequest, query, difficulty, toolId, category, sortBy, null, false);
    }

    /**
     * 分页查询教程；当 onlyLikedByMe 为 true 且 userId 非空时，仅返回该用户在 likes 表中收藏过的教程
     */
    public PageResponse<Tutorial> getTutorialsPage(PageRequest pageRequest, String query, String difficulty, Long toolId, String category, String sortBy, Long userId, Boolean onlyLikedByMe) {
        Long categoryId = resolveCategoryId(category);
        java.util.List<Long> restrictToTutorialIds = null;
        if (Boolean.TRUE.equals(onlyLikedByMe) && userId != null) {
            restrictToTutorialIds = likeMapper.findEntityIdsByUserIdAndEntityType(userId, "tutorial");
        }
        List<Tutorial> tutorials = tutorialMapper.searchTutorials(pageRequest, query, difficulty, toolId, categoryId, sortBy, restrictToTutorialIds);
        enrichCategory(tutorials);
        Long total = tutorialMapper.countTutorialsWithFilters(query, difficulty, toolId, categoryId, restrictToTutorialIds);
        Tutorial[] content = tutorials.toArray(new Tutorial[0]);
        Integer page = pageRequest.getPage().intValue();
        Integer size = pageRequest.getSize().intValue();
        return new PageResponse<>(content, total != null ? total : 0L, page, size);
    }

    public Tutorial getTutorialById(Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial != null) {
            enrichCategory(java.util.Collections.singletonList(tutorial));
        }
        return tutorial;
    }

    public List<Tutorial> getRelatedTutorials(Long toolId) {
        List<Tutorial> tutorials = tutorialMapper.findByToolId(toolId);
        enrichCategory(tutorials);
        return tutorials;
    }

    public Tutorial setPinned(Long id, Boolean pinned) {
        Tutorial t = tutorialMapper.selectById(id);
        if (t == null) {
            throw new RuntimeException("教程不存在");
        }
        t.setPinned(Boolean.TRUE.equals(pinned));
        tutorialMapper.updateById(t);
        return t;
    }

    /**
     * 后台保存 / 更新教程
     */
    public Tutorial saveTutorial(Tutorial tutorial) {
        if (tutorial.getId() == null) {
            // 新增：处理分类和设置默认值
            if (tutorial.getCategory() != null && !tutorial.getCategory().trim().isEmpty()) {
                Long categoryId = resolveCategoryId(tutorial.getCategory());
                tutorial.setCategoryId(categoryId);
            }
            tutorial.setCategory(null); // 清除临时字段，避免更新时出错
            // 如果未指定作者，尝试从当前登录用户获取
            if (tutorial.getAuthorId() == null) {
                try {
                    Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                            ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                            : null;
                    if (principal instanceof String) {
                        String username = (String) principal;
                        userMapper.findByUsername(username).ifPresent(u -> tutorial.setAuthorId(u.getId()));
                    }
                } catch (Exception ignored) {
                }
            }
            if (tutorial.getViewCount() == null) {
                tutorial.setViewCount(0);
            }
            tutorialMapper.insert(tutorial);
        } else {
            // 更新：保留原有的浏览量和分类ID
            Tutorial existing = tutorialMapper.selectById(tutorial.getId());
            if (existing != null) {
                // 保留原有的浏览量
                if (tutorial.getViewCount() == null) {
                    tutorial.setViewCount(existing.getViewCount());
                }
                // 保留原有的 Banner / 封面图
                if (tutorial.getCoverImage() == null) {
                    tutorial.setCoverImage(existing.getCoverImage());
                }
                // 处理分类：如果传递了分类名称，转换为分类ID；否则保留原有的分类ID
                if (tutorial.getCategory() != null) {
                    if (tutorial.getCategory().trim().isEmpty()) {
                        // 用户清空分类
                        tutorial.setCategoryId(null);
                    } else {
                        // 用户设置了分类名称，转换为ID（如果不存在会自动创建）
                        Long categoryId = resolveCategoryId(tutorial.getCategory());
                        tutorial.setCategoryId(categoryId);
                    }
                } else {
                    // 用户没有传递分类字段，保留原有的分类ID
                    tutorial.setCategoryId(existing.getCategoryId());
                }
            }
            tutorial.setCategory(null); // 清除临时字段，避免更新时出错
            tutorialMapper.updateById(tutorial);
        }
        searchIndexService.upsertTutorial(tutorial);
        return tutorial;
    }

    /**
     * 后台删除教程
     */
    public void deleteTutorial(Long id) {
        searchIndexService.deleteByEntity("tutorial", id);
        tutorialMapper.deleteById(id);
    }

    /**
     * 增加教程浏览量
     */
    public void incrementViewCount(Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial != null) {
            tutorial.setViewCount((tutorial.getViewCount() != null ? tutorial.getViewCount() : 0) + 1);
            tutorialMapper.updateById(tutorial);
        }
    }

    private Long resolveCategoryId(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) return null;

        // 先查找是否存在该分类（按 name + type）
        Category existing = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getName, categoryName)
                        .eq(Category::getType, "tutorial")
                        .last("LIMIT 1")
        );
        if (existing != null) return existing.getId();

        // 如果不存在，尝试插入（并发情况下可能出现唯一键冲突）
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setType("tutorial");
        try {
            categoryMapper.insert(newCategory);
            return newCategory.getId();
        } catch (DuplicateKeyException dk) {
            // 发生重复键（并发插入），再查询一次获取已有记录的 id
            Category now = categoryMapper.selectOne(
                    new LambdaQueryWrapper<Category>()
                            .eq(Category::getName, categoryName)
                            .eq(Category::getType, "tutorial")
                            .last("LIMIT 1")
            );
            return now == null ? null : now.getId();
        }
    }

    private void enrichCategory(List<Tutorial> tutorials) {
        if (tutorials == null) return;
        for (Tutorial t : tutorials) {
            if (t == null || t.getCategoryId() == null) continue;
            Category c = categoryMapper.selectById(t.getCategoryId());
            if (c != null) t.setCategory(c.getName());
        }
    }
}