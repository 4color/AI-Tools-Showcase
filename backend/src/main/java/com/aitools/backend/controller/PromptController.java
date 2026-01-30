package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Prompt;
import com.aitools.backend.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prompts")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "提示词管理", description = "提示词相关接口")
public class PromptController {

    private final PromptService promptService;

    @GetMapping
    @Operation(summary = "获取提示词列表", description = "获取所有提示词，支持分类和关键词过滤")
    public Result<List<Prompt>> getPrompts(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "标签过滤（逗号分隔）") @RequestParam(required = false) String tags) {
        try {
            List<Prompt> prompts = promptService.getPrompts(category, keyword, tags);
            return Result.success(prompts);
        } catch (Exception e) {
            return Result.error("获取提示词列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取提示词（后台管理）", description = "分页获取提示词信息，支持搜索和分类筛选")
    public Result<PageResponse<Prompt>> getPromptsPage(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String query,
            @Parameter(description = "分类筛选（categories.name，type=prompt）") @RequestParam(required = false) String category,
            @Parameter(description = "标签筛选（预留，逗号分隔）") @RequestParam(required = false) String tags,
            @Parameter(description = "排序方式：latest-最新、popular-最热门、views-浏览最多") @RequestParam(defaultValue = "latest") String sortBy
    ) {
        try {
            PageRequest pageRequest = new PageRequest(page.longValue(), size.longValue());
            PageResponse<Prompt> result = promptService.getPromptsPage(pageRequest, query, category, tags, sortBy);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("分页获取提示词失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取提示词", description = "根据提示词ID获取详细信息")
    public Result<Prompt> getPromptById(@Parameter(description = "提示词ID") @PathVariable Long id) {
        try {
            return promptService.getPromptById(id)
                    .map(Result::success)
                    .orElse(Result.error(404, "提示词不存在"));
        } catch (Exception e) {
            return Result.error("获取提示词失败: " + e.getMessage());
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门提示词", description = "获取点赞数最高的提示词列表")
    public Result<List<Prompt>> getPopularPrompts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Prompt> prompts = promptService.getPopularPrompts(limit);
            return Result.success(prompts);
        } catch (Exception e) {
            return Result.error("获取热门提示词失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新提示词", description = "获取最新发布的提示词列表")
    public Result<List<Prompt>> getLatestPrompts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Prompt> prompts = promptService.getLatestPrompts(limit);
            return Result.success(prompts);
        } catch (Exception e) {
            return Result.error("获取最新提示词失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/related")
    @Operation(summary = "获取相关提示词", description = "获取与指定提示词相关的提示词列表")
    public Result<List<Prompt>> getRelatedPrompts(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        try {
            return promptService.getPromptById(id)
                    .map(prompt -> {
                        List<Prompt> related = promptService.getRelatedPrompts(id, limit);
                        return Result.success(related);
                    })
                    .orElse(Result.error(404, "提示词不存在"));
        } catch (Exception e) {
            return Result.error("获取相关提示词失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "增加浏览量", description = "提示词详情页访问时调用，浏览量加1")
    public Result<Void> incrementViewCount(
            @Parameter(description = "提示词ID") @PathVariable Long id) {
        try {
            promptService.incrementViewCount(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("增加浏览量失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "保存提示词（新增/更新，后台）", description = "后台管理使用的保存接口")
    public Result<Prompt> savePrompt(@RequestBody Prompt prompt) {
        try {
            Prompt saved = promptService.savePrompt(prompt);
            return Result.success(saved, "保存成功");
        } catch (Exception e) {
            return Result.error("保存提示词失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除提示词（后台）", description = "根据ID删除提示词，用于后台管理")
    public Result<Void> deletePrompt(@PathVariable Long id) {
        try {
            promptService.deletePrompt(id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            return Result.error("删除提示词失败: " + e.getMessage());
        }
    }
}
