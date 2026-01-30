package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Tool;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.service.ToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tools")
@CrossOrigin
@Tag(name = "工具管理", description = "工具相关接口")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    @Operation(summary = "获取工具列表", description = "获取工具列表，支持分类、关键词、价格类型过滤；仅登录且 onlyLikedByMe=true 时返回当前用户收藏（likes 表）的工具")
    public Result<PageResponse<Tool>> getTools(
            @RequestBody(required = false) Map<String, Object> filters,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = new PageRequest(page.longValue(), size.longValue());

        String category = filters != null ? (String) filters.get("category") : null;
        String keyword = filters != null ? (String) filters.get("keyword") : null;
        String price = filters != null ? (String) filters.get("price") : null;
        String sort = filters != null ? (String) filters.get("sort") : null;
        String order = filters != null ? (String) filters.get("order") : "asc";

        Boolean onlyLikedByMe = null;
        if (filters != null && filters.containsKey("onlyLikedByMe")) {
            Object v = filters.get("onlyLikedByMe");
            if (v instanceof Boolean) onlyLikedByMe = (Boolean) v;
            else if (v != null) onlyLikedByMe = Boolean.parseBoolean(v.toString());
        }
        Long userId = null;
        if (Boolean.TRUE.equals(onlyLikedByMe)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                Optional<User> userOpt = userMapper.findByUsername(auth.getName());
                if (userOpt.isPresent()) userId = userOpt.get().getId();
            }
        }

        PageResponse<Tool> result = toolService.getToolsPageByCategoryAndKeywordAndPrice(pageRequest, category, keyword, price, sort, order, userId, onlyLikedByMe);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取工具", description = "根据工具ID获取详细信息")
    public Result<Tool> getToolById(@PathVariable Long id) {
        try {
            java.util.Optional<Tool> toolOpt = toolService.getToolById(id);
            if (toolOpt.isPresent()) {
                return Result.success(toolOpt.get());
            } else {
                return Result.error(404, "工具不存在");
            }
        } catch (Exception e) {
            return Result.error("获取工具失败: " + e.getMessage());
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门工具", description = "获取评分最高的工具列表")
    public Result<List<Tool>> getPopularTools() {
        try {
            List<Tool> tools = toolService.getPopularTools();
            return Result.success(tools);
        } catch (Exception e) {
            return Result.error("获取热门工具失败: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    @Operation(summary = "获取所有分类", description = "获取工具的所有分类列表")
    public Result<List<String>> getAllCategories() {
        try {
            List<String> categories = toolService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "创建工具", description = "创建新的工具")
    public Result<Tool> createTool(@RequestBody Tool tool) {
        try {
            Tool savedTool = toolService.saveTool(tool);
            return Result.success(savedTool, "工具创建成功");
        } catch (Exception e) {
            return Result.error("创建工具失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除工具", description = "根据ID删除工具")
    public Result<Void> deleteTool(@PathVariable Long id) {
        try {
            toolService.deleteTool(id);
            return Result.success(null, "工具删除成功");
        } catch (Exception e) {
            return Result.error("删除工具失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "增加浏览量", description = "工具详情页访问时调用，浏览量加1")
    public Result<Void> incrementViewCount(
            @Parameter(description = "工具ID") @PathVariable Long id) {
        try {
            toolService.incrementViewCount(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("增加浏览量失败: " + e.getMessage());
        }
    }
}