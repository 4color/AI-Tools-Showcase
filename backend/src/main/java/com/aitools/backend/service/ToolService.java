package com.aitools.backend.service;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Tool;
import com.aitools.backend.mapper.LikeMapper;
import com.aitools.backend.mapper.TagMapper;
import com.aitools.backend.mapper.ToolMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToolService {

    @Autowired
    private ToolMapper toolMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private LikeMapper likeMapper;

    public PageResponse<Tool> getToolsPageByCategoryAndKeywordAndPrice(PageRequest pageRequest, String category, String keyword, String price, String sort, String order) {
        return getToolsPageByCategoryAndKeywordAndPrice(pageRequest, category, keyword, price, sort, order, null, false);
    }

    /**
     * 分页查询工具；当 onlyLikedByMe 为 true 且 userId 非空时，仅返回该用户在 likes 表中收藏（点赞）过的工具
     */
    public PageResponse<Tool> getToolsPageByCategoryAndKeywordAndPrice(PageRequest pageRequest, String category, String keyword, String price, String sort, String order, Long userId, Boolean onlyLikedByMe) {
        List<Long> restrictToToolIds = null;
        if (Boolean.TRUE.equals(onlyLikedByMe) && userId != null) {
            restrictToToolIds = likeMapper.findEntityIdsByUserIdAndEntityType(userId, "tool");
        }
        Long total = Long.valueOf(toolMapper.selectCountByCategoryAndKeywordAndPrice(category, keyword, price, restrictToToolIds));
        List<Tool> tools = toolMapper.selectPageByCategoryAndKeywordAndPrice(pageRequest, category, keyword, price, sort, order, restrictToToolIds);
        enrichToolTags(tools);
        Tool[] content = tools.toArray(new Tool[0]);
        Integer page = pageRequest.getPage().intValue();
        Integer size = pageRequest.getSize().intValue();
        return new PageResponse<>(content, total, page, size);
    }

    public Optional<Tool> getToolById(Long id) {
        Tool tool = toolMapper.selectById(id);
        if (tool == null) {
            return Optional.empty();
        }
        // tags 表共用：type='tool' + entity_id=tool.id
        List<String> tags = tagMapper.findNamesByTypeAndEntityId("tool", id);
        tool.setTags(tags);
        // 兼容前端旧字段：features 也给一份同样的数据
        tool.setFeatures(tags);
        return Optional.of(tool);
    }

    public List<Tool> searchTools(String query) {
        return toolMapper.searchTools(query);
    }

    public List<Tool> searchToolsByCategoryAndKeyword(String category, String query) {
        return searchToolsByCategoryAndKeywordAndPrice(category, query, null);
    }

    public List<Tool> searchToolsByCategoryAndKeywordAndPrice(String category, String query, String price) {
        LambdaQueryWrapper<Tool> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Tool::getCategory, category);
        }
        if (query != null && !query.isEmpty()) {
            wrapper.and(w -> w.like(Tool::getName, query)
                    .or()
                    .like(Tool::getDescription, query)
                    .or()
                    .like(Tool::getCategory, query));
        }
        if (price != null && !price.isEmpty()) {
            wrapper.eq(Tool::getPrice, price);
        }
        return toolMapper.selectList(wrapper);
    }

    public List<Tool> getToolsByCategory(String category) {
        return toolMapper.findByCategory(category);
    }

    public List<Tool> getPopularTools() {
        List<Tool> tools = toolMapper.findTop10ByOrderByRatingDesc();
        enrichToolTags(tools);
        return tools;
    }

    /**
     * tags 表共用：type='tool' + entity_id=tool.id
     */
    private void enrichToolTags(List<Tool> tools) {
        if (tools == null || tools.isEmpty()) return;
        for (Tool tool : tools) {
            if (tool == null || tool.getId() == null) continue;
            List<String> tags = tagMapper.findNamesByTypeAndEntityId("tool", tool.getId());
            tool.setTags(tags);
            // 兼容前端旧字段
            tool.setFeatures(tags);
        }
    }

    public List<String> getAllCategories() {
        return toolMapper.findAllCategories();
    }

    public Tool saveTool(Tool tool) {
        if (tool.getId() == null) {
            toolMapper.insert(tool);
        } else {
            toolMapper.updateById(tool);
        }

        return tool;
    }

    public void deleteTool(Long id) {
        toolMapper.deleteById(id);
    }

    /**
     * 增加工具浏览量
     */
    public void incrementViewCount(Long id) {
        Tool tool = toolMapper.selectById(id);
        if (tool != null) {
            tool.setViewCount((tool.getViewCount() != null ? tool.getViewCount() : 0) + 1);
            toolMapper.updateById(tool);
        }
    }
}
