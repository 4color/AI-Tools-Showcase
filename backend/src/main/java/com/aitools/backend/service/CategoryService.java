package com.aitools.backend.service;

import com.aitools.backend.entity.Category;
import com.aitools.backend.mapper.CategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }

    public List<Category> getAllCategoriesByType(String type) {

        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Category::getType, type);
        return categoryMapper.selectList(wrapper);
    }

    public Optional<Category> getCategoryById(Long id) {
        return Optional.ofNullable(categoryMapper.selectById(id));
    }

    public Optional<Category> getCategoryByName(String name) {

        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Category::getName, name);
        return categoryMapper.selectList(wrapper).stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst();
    }

    public Category saveCategory(Category category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return category;
    }

    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}