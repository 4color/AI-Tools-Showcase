package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.AdminDashboardResponse;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.User;
import com.aitools.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "后台仪表盘")
    public Result<AdminDashboardResponse> getDashboard(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        int l = (limit == null || limit < 1) ? 5 : Math.min(limit, 20);
        return Result.success(adminService.getDashboard(l));
    }

    @GetMapping("/users")
    @Operation(summary = "后台用户列表（分页，支持关键词）")
    public Result<PageResponse<User>> getUsersPage(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词（用户名/邮箱）") @RequestParam(required = false) String keyword
    ) {
        PageRequest pr = new PageRequest(page.longValue(), size.longValue());
        return Result.success(adminService.getUsersPage(pr, keyword));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "后台更新用户（仅 role、officeId）")
    public Result<User> updateUser(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String role = body.get("role");
            String officeId = body.get("officeId");
            User updated = adminService.updateUser(id, role, officeId);
            return Result.success(updated, "更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "后台删除用户")
    public Result<Void> deleteUser(@Parameter(description = "用户 ID") @PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}

