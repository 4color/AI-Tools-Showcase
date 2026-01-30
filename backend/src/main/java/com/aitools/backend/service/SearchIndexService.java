package com.aitools.backend.service;

import com.aitools.backend.entity.*;
import com.aitools.backend.mapper.SearchIndexMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchIndexService {

    @Autowired
    private SearchIndexMapper searchIndexMapper;

    private static final int MAX_CONTENT_LENGTH = 2000;

    public void upsertTool(Tool tool) {
        if (tool == null || tool.getId() == null) return;
        String content = truncate(tool.getDescription());
        SearchIndex row = new SearchIndex();
        row.setEntityType("tool");
        row.setEntityId(tool.getId());
        row.setTitle(tool.getName() != null ? tool.getName() : "");
        row.setContent(content);
        row.setCreatedAt(tool.getCreatedAt() != null ? tool.getCreatedAt() : LocalDateTime.now());
        row.setUpdatedAt(LocalDateTime.now());
        searchIndexMapper.upsert(row);
    }

    public void upsertTutorial(Tutorial tutorial) {
        if (tutorial == null || tutorial.getId() == null) return;
        String content = truncate(tutorial.getDescription());
        SearchIndex row = new SearchIndex();
        row.setEntityType("tutorial");
        row.setEntityId(tutorial.getId());
        row.setTitle(tutorial.getTitle() != null ? tutorial.getTitle() : "");
        row.setContent(content);
        row.setCreatedAt(tutorial.getCreatedAt() != null ? tutorial.getCreatedAt() : LocalDateTime.now());
        row.setUpdatedAt(LocalDateTime.now());
        searchIndexMapper.upsert(row);
    }

    public void upsertPrompt(Prompt prompt) {
        if (prompt == null || prompt.getId() == null) return;
        String content = truncate(prompt.getDescription());
        SearchIndex row = new SearchIndex();
        row.setEntityType("prompt");
        row.setEntityId(prompt.getId());
        row.setTitle(prompt.getTitle() != null ? prompt.getTitle() : "");
        row.setContent(content);
        row.setCreatedAt(prompt.getCreatedAt() != null ? prompt.getCreatedAt() : LocalDateTime.now());
        row.setUpdatedAt(LocalDateTime.now());
        searchIndexMapper.upsert(row);
    }

    public void upsertApi(ApiInfo api) {
        if (api == null || api.getId() == null) return;
        String content = truncate(api.getDescription());
        SearchIndex row = new SearchIndex();
        row.setEntityType("api");
        row.setEntityId(api.getId());
        row.setTitle(api.getName() != null ? api.getName() : "");
        row.setContent(content);
        row.setCreatedAt(api.getCreatedAt() != null ? api.getCreatedAt() : LocalDateTime.now());
        row.setUpdatedAt(LocalDateTime.now());
        searchIndexMapper.upsert(row);
    }

    public void deleteByEntity(String entityType, Long entityId) {
        if (entityType == null || entityId == null) return;
        searchIndexMapper.deleteByEntity(entityType, entityId);
    }

    /**
     * 全文搜索：关键词为空时返回最近更新的若干条；有关键词时用 MATCH AGAINST（需 FULLTEXT 索引）
     */
    public List<SearchIndex> search(String keyword) {
        LambdaQueryWrapper<SearchIndex> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.apply("MATCH(title, content) AGAINST({0} IN NATURAL LANGUAGE MODE)", keyword);
        }
        wrapper.orderByDesc(SearchIndex::getUpdatedAt).last("LIMIT 100");
        return searchIndexMapper.selectList(wrapper);
    }

    private static String truncate(String s) {
        if (s == null) return "";
        if (s.length() <= MAX_CONTENT_LENGTH) return s;
        return s.substring(0, MAX_CONTENT_LENGTH);
    }
}
