package com.aitools.backend.service;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Prompt;
import com.aitools.backend.entity.User;
import com.aitools.backend.entity.Category;
import com.aitools.backend.mapper.PromptMapper;
import com.aitools.backend.mapper.TagMapper;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.mapper.CategoryMapper;
import com.aitools.backend.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptMapper promptMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final SearchIndexService searchIndexService;

    public List<Prompt> getAllPrompts() {
        List<Prompt> prompts = promptMapper.selectList(null);
        return enrichPrompts(prompts);
    }

    /**
     * 统一列表查询：category/keyword/tags(逗号分隔) 组合过滤
     */
    public List<Prompt> getPrompts(String categoryName, String keyword, String tagsCsv) {
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();

        // category
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            Category category = categoryMapper.selectList(null).stream()
                    .filter(c -> categoryName.equals(c.getName()) && "prompt".equals(c.getType()))
                    .findFirst()
                    .orElse(null);
            if (category == null) {
                return java.util.Collections.emptyList();
            }
            wrapper.eq(Prompt::getCategoryId, category.getId());
        }

        // keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Prompt::getTitle, keyword)
                    .or().like(Prompt::getDescription, keyword)
                    .or().like(Prompt::getContent, keyword));
        }

        // tags (AND 语义：必须同时包含所选标签)
        if (tagsCsv != null && !tagsCsv.trim().isEmpty()) {
            List<String> tags = Stream.of(tagsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (!tags.isEmpty()) {
                // 找出每个 tag 对应的 promptId 集合，然后取交集
                java.util.Set<Long> matched = null;
                for (String t : tags) {
                    java.util.Set<Long> ids = tagMapper.selectList(
                                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.aitools.backend.entity.Tag>()
                                            .eq(com.aitools.backend.entity.Tag::getType, "prompt")
                                            .eq(com.aitools.backend.entity.Tag::getName, t)
                            ).stream()
                            .map(com.aitools.backend.entity.Tag::getEntityId)
                            .filter(java.util.Objects::nonNull)
                            .collect(java.util.stream.Collectors.toSet());

                    if (matched == null) {
                        matched = ids;
                    } else {
                        matched.retainAll(ids);
                    }
                    if (matched.isEmpty()) {
                        return java.util.Collections.emptyList();
                    }
                }
                wrapper.in(Prompt::getId, matched);
            }
        }

        List<Prompt> prompts = promptMapper.selectList(wrapper);
        return enrichPrompts(prompts);
    }

    public List<Prompt> getPromptsByCategory(String categoryName) {
        // categoryName 来自统一 categories 表（type='prompt'）
        Category category = categoryMapper.selectList(null).stream()
                .filter(c -> categoryName.equals(c.getName()) && "prompt".equals(c.getType()))
                .findFirst()
                .orElse(null);
        if (category == null) {
            return java.util.Collections.emptyList();
        }
        List<Prompt> prompts = promptMapper.findByCategoryId(category.getId());
        return enrichPrompts(prompts);
    }

    public List<Prompt> searchPrompts(String keyword) {
        List<Prompt> prompts = promptMapper.searchPrompts(keyword);
        return enrichPrompts(prompts);
    }

    public Optional<Prompt> getPromptById(Long id) {
        Prompt prompt = promptMapper.selectById(id);
        if (prompt == null) {
            return Optional.empty();
        }
        enrichPrompt(prompt);
        return Optional.of(prompt);
    }

    public List<Prompt> getPopularPrompts(int limit) {
        List<Prompt> prompts = promptMapper.findTopByOrderByLikesDesc(limit);
        return enrichPrompts(prompts);
    }

    public List<Prompt> getLatestPrompts(int limit) {
        List<Prompt> prompts = promptMapper.findTopByOrderByCreatedAtDesc(limit);
        return enrichPrompts(prompts);
    }

    /**
     * 相关提示词：优先按标签匹配（有共同 tag 的提示词），无标签时按分类回退
     */
    public List<Prompt> getRelatedPrompts(Long promptId, int limit) {
        List<String> tagNames = tagMapper.findNamesByTypeAndEntityId("prompt", promptId);
        java.util.Set<Long> relatedIds = new java.util.HashSet<>();

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String name : tagNames) {
                List<com.aitools.backend.entity.Tag> rows = tagMapper.selectList(
                        new LambdaQueryWrapper<com.aitools.backend.entity.Tag>()
                                .eq(com.aitools.backend.entity.Tag::getType, "prompt")
                                .eq(com.aitools.backend.entity.Tag::getName, name)
                                .ne(com.aitools.backend.entity.Tag::getEntityId, promptId));
                for (com.aitools.backend.entity.Tag t : rows) {
                    if (t.getEntityId() != null) relatedIds.add(t.getEntityId());
                }
            }
        }

        if (!relatedIds.isEmpty()) {
            LambdaQueryWrapper<Prompt> w = new LambdaQueryWrapper<Prompt>()
                    .in(Prompt::getId, relatedIds)
                    .orderByDesc(Prompt::getLikes)
                    .last("LIMIT " + limit);
            List<Prompt> prompts = promptMapper.selectList(w);
            return enrichPrompts(prompts);
        }

        // 无标签时按分类回退
        Prompt current = promptMapper.selectById(promptId);
        if (current == null) return java.util.Collections.emptyList();
        LambdaQueryWrapper<Prompt> w = new LambdaQueryWrapper<Prompt>()
                .ne(Prompt::getId, promptId)
                .orderByDesc(Prompt::getLikes)
                .last("LIMIT " + limit);
        if (current.getCategoryId() != null) {
            w.eq(Prompt::getCategoryId, current.getCategoryId());
        }
        return enrichPrompts(promptMapper.selectList(w));
    }

    public void incrementViewCount(Long id) {
        promptMapper.incrementViewCount(id);
    }

    /**
     * 后台分页查询提示词（参考工具/教程管理）
     */
    public PageResponse<Prompt> getPromptsPage(PageRequest pageRequest, String query, String categoryName, String tagsCsv, String sortBy) {
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选：按 categories.name + type='prompt' 解析 category_id
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            Category category = categoryMapper.selectList(null).stream()
                    .filter(c -> categoryName.equals(c.getName()) && "prompt".equals(c.getType()))
                    .findFirst()
                    .orElse(null);
            if (category == null) {
                return new PageResponse<>(new Prompt[0], 0L, pageRequest.getPage().intValue(), pageRequest.getSize().intValue());
            }
            wrapper.eq(Prompt::getCategoryId, category.getId());
        }

        // 关键词筛选（标题 / 描述 / 内容）
        if (query != null && !query.trim().isEmpty()) {
            String keyword = query.trim();
            wrapper.and(w -> w.like(Prompt::getTitle, keyword)
                    .or().like(Prompt::getDescription, keyword)
                    .or().like(Prompt::getContent, keyword));
        }

        // tagsCsv 暂不在分页接口中强制 AND 过滤（避免复杂 sql），仅预留参数，后续需要时可补充

        // 排序：默认按创建时间倒序
        wrapper.orderByDesc(Prompt::getId);

        Page<Prompt> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        IPage<Prompt> result = promptMapper.selectPage(page, wrapper);

        List<Prompt> records = enrichPrompts(result.getRecords());
        Prompt[] content = records.toArray(new Prompt[0]);
        long total = result.getTotal();
        return new PageResponse<>(content, total, pageRequest.getPage().intValue(), pageRequest.getSize().intValue());
    }

    /**
     * 后台保存 / 更新提示词（供管理端使用）
     */
    public Prompt savePrompt(Prompt prompt) {
        if (prompt.getId() == null) {
            // 处理分类
            if (prompt.getCategory() != null && !prompt.getCategory().trim().isEmpty()) {
                Long categoryId = resolveCategoryId(prompt.getCategory().trim());
                prompt.setCategoryId(categoryId);
            }
            prompt.setCategory(null);

            // 设置默认统计字段
            if (prompt.getLikes() == null) prompt.setLikes(0);
            if (prompt.getViews() == null) prompt.setViews(0);
            if (prompt.getCommentCount() == null) prompt.setCommentCount(0);

            // 设置作者：当前登录用户
            if (prompt.getCreatorId() == null) {
                try {
                    Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                            ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                            : null;
                    if (principal instanceof String) {
                        String username = (String) principal;
                        userMapper.findByUsername(username).ifPresent(u -> prompt.setCreatorId(u.getId()));
                    }
                } catch (Exception ignored) {
                }
            }

            promptMapper.insert(prompt);
            if (prompt.getTags() != null) savePromptTags(prompt.getId(), prompt.getTags());
        } else {
            // 更新：点赞、浏览量、评论数等统计字段一律沿用库中原值，避免前端未传或传 0 导致被清空
            Prompt existing = promptMapper.selectById(prompt.getId());
            if (existing != null) {
                prompt.setLikes(existing.getLikes() != null ? existing.getLikes() : 0);
                prompt.setViews(existing.getViews() != null ? existing.getViews() : 0);
                prompt.setCommentCount(existing.getCommentCount() != null ? existing.getCommentCount() : 0);
                if (prompt.getCreatorId() == null) prompt.setCreatorId(existing.getCreatorId());
                // 封面图：未传时保留原值，传了（含空字符串）则更新
                if (prompt.getCoverImage() == null) prompt.setCoverImage(existing.getCoverImage());
                // 使用说明：未传时保留原值
                if (prompt.getUsageGuide() == null) prompt.setUsageGuide(existing.getUsageGuide());
                // 效果图：未传时保留原值
                if (prompt.getEffectImage() == null) prompt.setEffectImage(existing.getEffectImage());

                // 分类处理
                if (prompt.getCategory() != null) {
                    if (prompt.getCategory().trim().isEmpty()) {
                        prompt.setCategoryId(existing.getCategoryId());
                    } else {
                        Long categoryId = resolveCategoryId(prompt.getCategory().trim());
                        prompt.setCategoryId(categoryId);
                    }
                } else {
                    prompt.setCategoryId(existing.getCategoryId());
                }
            }
            prompt.setCategory(null);
            promptMapper.updateById(prompt);
            if (prompt.getTags() != null) savePromptTags(prompt.getId(), prompt.getTags());
        }

        searchIndexService.upsertPrompt(prompt);
        enrichPrompt(prompt);
        return prompt;
    }

    /**
     * 后台删除提示词
     */
    public void deletePrompt(Long id) {
        searchIndexService.deleteByEntity("prompt", id);
        promptMapper.deleteById(id);
        // tags 使用共享 tags 表，如需清理可在此补充删除逻辑
    }

    /**
     * 解析 / 创建分类（categories 表，type='prompt'）
     */
    private Long resolveCategoryId(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) return null;

        Category existing = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getName, categoryName)
                        .eq(Category::getType, "prompt")
                        .last("LIMIT 1")
        );
        if (existing != null) return existing.getId();

        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setType("prompt");
        try {
            categoryMapper.insert(newCategory);
            return newCategory.getId();
        } catch (DuplicateKeyException e) {
            Category now = categoryMapper.selectOne(
                    new LambdaQueryWrapper<Category>()
                            .eq(Category::getName, categoryName)
                            .eq(Category::getType, "prompt")
                            .last("LIMIT 1")
            );
            return now == null ? null : now.getId();
        }
    }

    private void enrichPrompt(Prompt prompt) {
        // Load tags (tags 表共用：type='prompt' + entity_id=prompt.id)
        List<String> tags = tagMapper.findNamesByTypeAndEntityId("prompt", prompt.getId());
        prompt.setTags(tags);

        // Load creator
        User creator = userMapper.selectById(prompt.getCreatorId());
        if (creator != null) {
            prompt.setCreator(creator);
        }

        // Load category name from shared categories table
        Category category = categoryMapper.selectById(prompt.getCategoryId());
        if (category != null) {
            prompt.setCategory(category.getName());
        }
    }

    /** 保存提示词标签：先删后插（tags 表 type=prompt, entity_id=promptId） */
    private void savePromptTags(Long promptId, List<String> tagNames) {
        if (promptId == null) return;
        tagMapper.deleteByTypeAndEntityId("prompt", promptId);
        if (tagNames == null || tagNames.isEmpty()) return;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (String name : tagNames) {
            String n = name == null ? null : name.trim();
            if (n == null || n.isEmpty()) continue;
            com.aitools.backend.entity.Tag tag = new com.aitools.backend.entity.Tag();
            tag.setName(n);
            tag.setType("prompt");
            tag.setEntityId(promptId);
            tag.setCreatedAt(now);
            tagMapper.insert(tag);
        }
    }

    private List<Prompt> enrichPrompts(List<Prompt> prompts) {
        return prompts.stream().peek(prompt -> {
            // Load tags
            List<String> tags = tagMapper.findNamesByTypeAndEntityId("prompt", prompt.getId());
            prompt.setTags(tags);

            // Load creator
            User creator = userMapper.selectById(prompt.getCreatorId());
            if (creator != null) {
                prompt.setCreator(creator);
            }

            // Load category name
            Category category = categoryMapper.selectById(prompt.getCategoryId());
            if (category != null) {
                prompt.setCategory(category.getName());
            }
        }).collect(Collectors.toList());
    }
}
