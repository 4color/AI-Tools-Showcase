package com.aitools.backend.service;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.ApiInfo;
import com.aitools.backend.entity.ApiModel;
import com.aitools.backend.entity.Category;
import com.aitools.backend.mapper.ApiInfoMapper;
import com.aitools.backend.mapper.ApiModelMapper;
import com.aitools.backend.mapper.CategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;
import org.springframework.dao.DuplicateKeyException;

@Service
public class ApiService {

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ApiModelMapper apiModelMapper;

    private Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }
        return categoryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getType, "api")
                        .eq(Category::getName, categoryName)
                .last("LIMIT 1")
        );
    }

    private List<ApiInfo> enrichCategories(List<ApiInfo> apis) {
        return apis.stream().peek(api -> {
            if (api.getCategoryId() != null) {
                Category c = categoryMapper.selectById(api.getCategoryId());
                if (c != null) {
                    api.setCategory(c.getName());
                }
            }
        }).collect(Collectors.toList());
    }

    public List<ApiInfo> getAllApis(String category, String provider, String keyword) {
        LambdaQueryWrapper<ApiInfo> wrapper = buildFilterWrapper(category, provider, keyword);
        List<ApiInfo> apis = apiInfoMapper.selectList(wrapper);
        return enrichCategories(apis);
    }

    public List<String> getProviders() {
        return apiInfoMapper.findAllProviders();
    }

    @lombok.Data
    public static class ApiModelResponse {
        private Long id;
        private String name;
        private String description;
        private Integer contextLength;
        private List<String> capabilities;
        private Boolean recommended;
    }

    @lombok.Data
    public static class ApiDetailResponse {
        private Long id;
        private String name;
        private String provider;
        private String description;
        private String pricing;
        private String category;
        private Long categoryId;
        private String logoUrl;
        private String baseUrl;
        private List<ApiModelResponse> models;
        private java.time.LocalDateTime createdAt;
    }

    public Optional<ApiDetailResponse> getApiDetail(Long id) {
        ApiInfo api = apiInfoMapper.selectById(id);
        if (api == null) {
            return Optional.empty();
        }

        // enrich category name
        if (api.getCategoryId() != null) {
            Category c = categoryMapper.selectById(api.getCategoryId());
            if (c != null) {
                api.setCategory(c.getName());
            }
        }

        List<ApiModel> models = apiModelMapper.findByApiId(id);
        List<ApiModelResponse> modelResponses = models.stream().map(m -> {
            ApiModelResponse r = new ApiModelResponse();
            r.setId(m.getId());
            r.setName(m.getName());
            r.setDescription(m.getDescription());
            r.setContextLength(m.getContextLength());
            r.setRecommended(m.getRecommended());
            if (m.getCapabilities() == null || m.getCapabilities().trim().isEmpty()) {
                r.setCapabilities(java.util.Collections.emptyList());
            } else {
                r.setCapabilities(Arrays.stream(m.getCapabilities().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()));
            }
            return r;
        }).collect(Collectors.toList());

        ApiDetailResponse resp = new ApiDetailResponse();
        resp.setId(api.getId());
        resp.setName(api.getName());
        resp.setProvider(api.getProvider());
        resp.setDescription(api.getDescription());
        resp.setPricing(api.getPricing());
        resp.setCategory(api.getCategory());
        resp.setCategoryId(api.getCategoryId());
        resp.setLogoUrl(api.getLogoUrl());
        resp.setBaseUrl(api.getBaseUrl());
        resp.setModels(modelResponses);
        resp.setCreatedAt(api.getCreatedAt());
        return Optional.of(resp);
    }

    public PageResponse<ApiInfo> getApisPage(PageRequest pageRequest) {
        return getApisPage(pageRequest, null, null, null);
    }

    public PageResponse<ApiInfo> getApisPage(PageRequest pageRequest, String category, String provider, String keyword) {
        LambdaQueryWrapper<ApiInfo> wrapper = buildFilterWrapper(category, provider, keyword);

        Page<ApiInfo> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        IPage<ApiInfo> result = apiInfoMapper.selectPage(page, wrapper);

        List<ApiInfo> enriched = enrichCategories(result.getRecords());
        ApiInfo[] content = enriched.toArray(new ApiInfo[0]);

        Integer pageIndex = pageRequest.getPage().intValue();
        Integer size = pageRequest.getSize().intValue();
        Long total = result.getTotal();
        return new PageResponse<>(content, total, pageIndex, size);
    }

    private LambdaQueryWrapper<ApiInfo> buildFilterWrapper(String category, String provider, String keyword) {
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();

        // category
        if (category != null && !category.trim().isEmpty()) {
            Category c = resolveCategory(category);
            if (c == null) {
                // 无匹配分类，直接返回空条件保证 0 结果
                wrapper.eq(ApiInfo::getId, -1);
                return wrapper;
            }
            wrapper.eq(ApiInfo::getCategoryId, c.getId());
        }

        // provider
        if (provider != null && !provider.trim().isEmpty()) {
            wrapper.eq(ApiInfo::getProvider, provider.trim());
        }

        // keyword -> name/description/documentation 模糊搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(ApiInfo::getName, kw)
                    .or().like(ApiInfo::getDescription, kw)
                    .or().like(ApiInfo::getDocumentation, kw));
        }

        wrapper.orderByDesc(ApiInfo::getCreatedAt);
        return wrapper;
    }

    /**
     * 后台保存 API（新增/更新）
     */
    public ApiInfo saveApi(ApiInfo api) {
        if (api.getId() == null) {
            if (api.getCategory() != null && !api.getCategory().trim().isEmpty()) {
                Long categoryId = resolveCategoryId(api.getCategory().trim());
                api.setCategoryId(categoryId);
            }
            api.setCategory(null);
            if (api.getPricing() == null || api.getPricing().trim().isEmpty()) api.setPricing("free");
            apiInfoMapper.insert(api);
        } else {
            ApiInfo existing = apiInfoMapper.selectById(api.getId());
            if (existing != null) {
                if (api.getApiKey() == null || api.getApiKey().trim().isEmpty()) api.setApiKey(existing.getApiKey());
            }
            if (api.getCategory() != null && !api.getCategory().trim().isEmpty()) {
                api.setCategoryId(resolveCategoryId(api.getCategory().trim()));
            } else if (existing != null) {
                api.setCategoryId(existing.getCategoryId());
            }
            api.setCategory(null);
            apiInfoMapper.updateById(api);
        }
        if (api.getCategoryId() != null) {
            Category c = categoryMapper.selectById(api.getCategoryId());
            if (c != null) api.setCategory(c.getName());
        }
        return api;
    }

    /**
     * 后台删除 API（api_model 有 ON DELETE CASCADE）
     */
    public void deleteApi(Long id) {
        apiInfoMapper.deleteById(id);
    }

    /**
     * 后台获取某 API 下的模型列表
     */
    public List<ApiModel> getModelsByApiId(Long apiId) {
        if (apiId == null) return java.util.Collections.emptyList();
        return apiModelMapper.findByApiId(apiId);
    }

    /**
     * 后台保存模型（新增/更新）
     */
    public ApiModel saveModel(ApiModel model) {
        if (model.getApiId() == null) {
            throw new IllegalArgumentException("apiId 不能为空");
        }
        if (model.getCapabilities() != null && model.getCapabilities().contains(",")) {
            // 已是字符串，保持
        } else if (model.getCapabilities() == null || model.getCapabilities().trim().isEmpty()) {
            model.setCapabilities(null);
        }
        if (model.getId() == null) {
            apiModelMapper.insert(model);
            return model;
        }
        ApiModel existing = apiModelMapper.selectById(model.getId());
        if (existing != null && existing.getApiId() != null && existing.getApiId().equals(model.getApiId())) {
            apiModelMapper.updateById(model);
        }
        return model;
    }

    /**
     * 后台删除模型
     */
    public void deleteModel(Long id) {
        apiModelMapper.deleteById(id);
    }

    private Long resolveCategoryId(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) return null;
        Category existing = categoryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getType, "api")
                        .eq(Category::getName, categoryName)
                        .last("LIMIT 1"));
        if (existing != null) return existing.getId();
        Category newCat = new Category();
        newCat.setName(categoryName);
        newCat.setType("api");
        try {
            categoryMapper.insert(newCat);
            return newCat.getId();
        } catch (DuplicateKeyException e) {
            Category now = categoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                            .eq(Category::getType, "api")
                            .eq(Category::getName, categoryName)
                            .last("LIMIT 1"));
            return now != null ? now.getId() : null;
        }
    }
}