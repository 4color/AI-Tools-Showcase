package com.aitools.backend.controller;

import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserMapper userMapper;

    @GetMapping("/{entityType}/{entityId}/count")
    public ResponseEntity<Integer> getLikeCount(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(likeService.getLikeCount(entityType, entityId));
    }

    @GetMapping("/{entityType}/{entityId}/status")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        // 从 SecurityContext 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.ok(false);
        }
        
        String username = authentication.getName();
        Optional<User> userOpt = userMapper.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.ok(false);
        }
        
        Long userId = userOpt.get().getId();
        return ResponseEntity.ok(likeService.isLikedByUser(userId, entityType, entityId));
    }

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        // 从 SecurityContext 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        Optional<User> userOpt = userMapper.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = userOpt.get().getId();
        boolean liked = likeService.toggleLike(userId, entityType, entityId);
        return ResponseEntity.ok(liked);
    }
}