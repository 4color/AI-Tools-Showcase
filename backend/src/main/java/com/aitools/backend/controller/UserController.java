package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.UserResponse;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "根据 JWT token 获取当前登录用户的信息")
    public Result<UserResponse> getProfile() {
        // 从 SecurityContext 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return Result.error("未登录");
        }
        
        String username = authentication.getName();
        Optional<User> userOpt = userMapper.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        
        User user = userOpt.get();
        UserResponse userResponse = new UserResponse(user);
        
        return Result.success(userResponse);
    }
}
