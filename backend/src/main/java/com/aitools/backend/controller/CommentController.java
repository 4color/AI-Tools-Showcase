package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.entity.Comment;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "评论相关接口")
public class CommentController {

    private final CommentService commentService;
    private final UserMapper userMapper;

    @GetMapping("/{entityType}/{entityId}")
    @Operation(summary = "获取评论列表", description = "根据实体类型和ID获取评论列表")
    public Result<List<Comment>> getComments(
            @Parameter(description = "实体类型：tool, tutorial, api") @PathVariable String entityType,
            @Parameter(description = "实体ID") @PathVariable Long entityId) {
        List<Comment> comments = commentService.getCommentsByEntity(entityType, entityId);
        return Result.success(comments);
    }

    @PostMapping
    @Operation(summary = "添加评论", description = "添加新评论")
    public Result<Comment> addComment(@RequestBody Comment comment) {
        // 从 SecurityContext 获取当前登录用户，如果没有认证则使用默认用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId;
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            Optional<User> userOpt = userMapper.findByUsername(username);
            if (userOpt.isPresent()) {
                userId = userOpt.get().getId();
            } else {
                // 如果用户不存在，使用默认用户ID
                userId = 2L; // 默认使用user1
            }
        } else {
            // 未认证时使用默认用户ID
            userId = 2L; // 默认使用user1
        }
        
        comment.setUserId(userId);
        
        Comment savedComment = commentService.saveComment(comment);
        // 填充用户信息
        Comment saved = commentService.getCommentsByEntity(comment.getEntityType(), comment.getEntityId())
                .stream()
                .filter(c -> c.getId().equals(savedComment.getId()))
                .findFirst()
                .orElse(savedComment);
        return Result.success(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新评论", description = "更新评论内容")
    public Result<Comment> updateComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @RequestBody Comment comment) {
        // 检查评论是否存在
        Comment existing = commentService.getCommentsByEntity(comment.getEntityType(), comment.getEntityId())
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (existing == null) {
            return Result.error("评论不存在");
        }
        
        // 验证用户权限（如果有认证）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            Optional<User> userOpt = userMapper.findByUsername(username);
            if (userOpt.isPresent() && !existing.getUserId().equals(userOpt.get().getId())) {
                return Result.error("无权修改此评论");
            }
        }
        // 如果没有认证，允许修改（因为所有匿名评论都使用默认用户ID）
        
        comment.setId(id);
        comment.setUserId(existing.getUserId());
        comment.setEntityType(existing.getEntityType());
        comment.setEntityId(existing.getEntityId());
        
        Comment savedComment = commentService.saveComment(comment);
        // 填充用户信息
        Comment updated = commentService.getCommentsByEntity(comment.getEntityType(), comment.getEntityId())
                .stream()
                .filter(c -> c.getId().equals(savedComment.getId()))
                .findFirst()
                .orElse(savedComment);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定评论")
    public Result<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success(null);
    }

    @GetMapping("/{entityType}/{entityId}/rating")
    @Operation(summary = "获取平均评分", description = "获取实体的平均评分")
    public Result<Double> getAverageRating(
            @Parameter(description = "实体类型") @PathVariable String entityType,
            @Parameter(description = "实体ID") @PathVariable Long entityId) {
        Double rating = commentService.getAverageRating(entityType, entityId);
        return Result.success(rating != null ? rating : 0.0);
    }
}