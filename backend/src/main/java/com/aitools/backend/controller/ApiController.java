package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.ApiInfo;
import com.aitools.backend.entity.ApiModel;
import com.aitools.backend.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis")
@CrossOrigin
@Tag(name = "API管理", description = "API相关接口")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    @Operation(summary = "获取所有API", description = "获取所有API信息")
    public Result<List<ApiInfo>> getAllApis(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "提供商") @RequestParam(required = false) String provider,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        List<ApiInfo> apis = apiService.getAllApis(category, provider, keyword);
        return Result.success(apis);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取API", description = "分页获取API信息")
    public Result<PageResponse<ApiInfo>> getApisPage(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "提供商") @RequestParam(required = false) String provider,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        // 转换 Integer 到 Long
        PageRequest pageRequest = new PageRequest(page.longValue(), size.longValue());
        PageResponse<ApiInfo> result = apiService.getApisPage(pageRequest, category, provider, keyword);
        return Result.success(result);
    }

    @GetMapping("/providers")
    @Operation(summary = "获取 API 提供商列表", description = "返回去重后的提供商名称")
    public Result<List<String>> getProviders() {
        return Result.success(apiService.getProviders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取 API 详情", description = "返回 API 基础信息 + 模型列表（不包含价格方案/文档详情）")
    public Result<ApiService.ApiDetailResponse> getApiDetail(
            @Parameter(description = "API ID") @PathVariable Long id
    ) {
        return apiService.getApiDetail(id)
                .map(Result::success)
                .orElse(Result.error(404, "API 不存在"));
    }

    @PostMapping("/save")
    @Operation(summary = "保存 API（新增/更新，后台）")
    public Result<ApiInfo> saveApi(@RequestBody ApiInfo api) {
        try {
            ApiInfo saved = apiService.saveApi(api);
            return Result.success(saved, "保存成功");
        } catch (Exception e) {
            return Result.error("保存 API 失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 API（后台）")
    public Result<Void> deleteApi(@PathVariable Long id) {
        try {
            apiService.deleteApi(id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            return Result.error("删除 API 失败: " + e.getMessage());
        }
    }

    @GetMapping("/{apiId}/models")
    @Operation(summary = "获取某 API 下的模型列表（后台）")
    public Result<List<ApiModel>> getModelsByApiId(@Parameter(description = "API ID") @PathVariable("apiId") Long apiId) {
        return Result.success(apiService.getModelsByApiId(apiId));
    }

    @PostMapping("/models/save")
    @Operation(summary = "保存模型（新增/更新，后台）")
    public Result<ApiModel> saveModel(@RequestBody ApiModel model) {
        try {
            ApiModel saved = apiService.saveModel(model);
            return Result.success(saved, "保存成功");
        } catch (Exception e) {
            return Result.error("保存模型失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/models/{id}")
    @Operation(summary = "删除模型（后台）")
    public Result<Void> deleteModel(@Parameter(description = "模型 ID") @PathVariable Long id) {
        try {
            apiService.deleteModel(id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            return Result.error("删除模型失败: " + e.getMessage());
        }
    }
}