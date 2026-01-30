package com.aitools.backend.mapper;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.entity.Tool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ToolMapper extends BaseMapper<Tool> {

    default List<Tool> findByCategory(String category) {
        return selectList(new LambdaQueryWrapper<Tool>().eq(Tool::getCategory, category));
    }

    default List<Tool> searchTools(String query) {
        return selectList(new LambdaQueryWrapper<Tool>()
                .like(Tool::getName, query)
                .or()
                .like(Tool::getDescription, query)
                .or()
                .like(Tool::getCategory, query));
    }


    default List<Tool> findTop10ByOrderByRatingDesc() {
        // 置顶优先，再按评分排序
        return selectList(
                new LambdaQueryWrapper<Tool>()
                        .orderByDesc(Tool::getPinned)
                        .orderByDesc(Tool::getRating)
                        .last("LIMIT 10")
        );
    }

    default List<String> findAllCategories() {
        QueryWrapper<Tool> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT category").orderByAsc("category");
        List<Tool> tools = selectList(wrapper);
        return tools.stream().map(Tool::getCategory).distinct().sorted().collect(Collectors.toList());
    }


    default Integer selectCountByCategoryAndKeywordAndPrice(String category, String keyword, String price) {
        return selectCountByCategoryAndKeywordAndPrice(category, keyword, price, null);
    }

    default Integer selectCountByCategoryAndKeywordAndPrice(String category, String keyword, String price, List<Long> restrictToToolIds) {
        LambdaQueryWrapper<Tool> wrapper = new LambdaQueryWrapper<>();
        if (restrictToToolIds != null) {
            if (restrictToToolIds.isEmpty()) return 0;
            wrapper.in(Tool::getId, restrictToToolIds);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Tool::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Tool::getName, keyword)
                    .or()
                    .like(Tool::getDescription, keyword)
                    .or()
                    .like(Tool::getCategory, keyword));
        }
        if (price != null && !price.isEmpty()) {
            wrapper.eq(Tool::getPrice, price);
        }
        return Math.toIntExact(selectCount(wrapper));
    }

    default List<Tool> selectPageByCategoryAndKeywordAndPrice(PageRequest pageRequest, String category, String keyword, String price, String sort, String order) {
        return selectPageByCategoryAndKeywordAndPrice(pageRequest, category, keyword, price, sort, order, null);
    }

    default List<Tool> selectPageByCategoryAndKeywordAndPrice(PageRequest pageRequest, String category, String keyword, String price, String sort, String order, List<Long> restrictToToolIds) {
        if (restrictToToolIds != null && restrictToToolIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        Page<Tool> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        QueryWrapper<Tool> wrapper = new QueryWrapper<>();
        if (restrictToToolIds != null) {
            wrapper.in("id", restrictToToolIds);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword).or().like("category", keyword));
        }
        if (price != null && !price.isEmpty()) {
            wrapper.eq("price", price);
        }
        wrapper.orderByDesc("pinned");
        if (sort != null && !sort.isEmpty() && !"default".equals(sort)) {
            if ("desc".equalsIgnoreCase(order)) {
                wrapper.orderByDesc(sort);
            } else {
                wrapper.orderByAsc(sort);
            }
        } else {
            wrapper.lambda().orderByDesc(Tool::getCreatedAt).orderByAsc(Tool::getId);
        }
        IPage<Tool> result = this.selectPage(page, wrapper);
        return result.getRecords();
    }
}
