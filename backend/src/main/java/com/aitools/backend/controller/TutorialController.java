package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Tutorial;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tutorials")
@CrossOrigin
@Tag(name = "教程管理", description = "教程相关接口")
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    @Operation(summary = "获取所有教程", description = "获取所有教程信息")
    public Result<List<Tutorial>> getAllTutorials() {
        List<Tutorial> tutorials = tutorialService.getAllTutorials();
        return Result.success(tutorials);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取教程", description = "分页获取教程信息，支持搜索、筛选和排序；仅登录且 onlyLikedByMe=true 时返回当前用户收藏（likes 表）的教程")
    public Result<PageResponse<Tutorial>> getTutorialsPage(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String query,
            @Parameter(description = "难度筛选") @RequestParam(required = false) String difficulty,
            @Parameter(description = "工具ID筛选") @RequestParam(required = false) Long toolId,
            @Parameter(description = "分类筛选（来自 categories，type=tutorial）") @RequestParam(required = false) String category,
            @Parameter(description = "排序方式：latest-最新、popular-最热门、views-浏览最多") @RequestParam(defaultValue = "latest") String sortBy,
            @Parameter(description = "仅我的收藏（需登录）") @RequestParam(required = false) Boolean onlyLikedByMe
    ) {
        PageRequest pageRequest = new PageRequest(page.longValue(), size.longValue());
        Long userId = null;
        if (Boolean.TRUE.equals(onlyLikedByMe)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                Optional<User> userOpt = userMapper.findByUsername(auth.getName());
                if (userOpt.isPresent()) userId = userOpt.get().getId();
            }
        }
        PageResponse<Tutorial> result = tutorialService.getTutorialsPage(pageRequest, query, difficulty, toolId, category, sortBy, userId, onlyLikedByMe);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取教程详情", description = "根据ID获取教程详细信息")
    public Result<Tutorial> getTutorialById(
            @Parameter(description = "教程ID") @PathVariable Long id
    ) {
        try {
            Tutorial tutorial = tutorialService.getTutorialById(id);
            if (tutorial == null) {
                return Result.error("教程不存在");
            }
            return Result.success(tutorial);
        } catch (Exception e) {
            return Result.error("获取教程详情失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/related")
    @Operation(summary = "获取相关教程", description = "根据工具ID获取相关教程")
    public Result<List<Tutorial>> getRelatedTutorials(
            @Parameter(description = "工具ID") @RequestParam Long toolId
    ) {
        try {
            List<Tutorial> tutorials = tutorialService.getRelatedTutorials(toolId);
            return Result.success(tutorials);
        } catch (Exception e) {
            return Result.error("获取相关教程失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "保存教程（新增/更新）", description = "后台管理使用的保存接口")
    public Result<Tutorial> saveTutorial(@RequestBody Tutorial tutorial) {
        try {
            Tutorial saved = tutorialService.saveTutorial(tutorial);
            return Result.success(saved, "保存成功");
        } catch (Exception e) {
            return Result.error("保存教程失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教程", description = "根据ID删除教程，用于后台管理")
    public Result<Void> deleteTutorial(@PathVariable Long id) {
        try {
            tutorialService.deleteTutorial(id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            return Result.error("删除教程失败: " + e.getMessage());
        }
    }
    @PostMapping("/{id}/view")
    @Operation(summary = "增加浏览量", description = "教程详情页访问时调用，浏览量加1")
    public Result<Void> incrementViewCount(
            @Parameter(description = "教程ID") @PathVariable Long id) {
        try {
            tutorialService.incrementViewCount(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("增加浏览量失败: " + e.getMessage());
        }
    }
}