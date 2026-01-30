package com.aitools.backend.service;

import com.aitools.backend.dto.AdminDashboardResponse;
import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.dto.PageResponse;
import com.aitools.backend.entity.Tool;
import com.aitools.backend.entity.Tutorial;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.ApiInfoMapper;
import com.aitools.backend.mapper.PromptMapper;
import com.aitools.backend.mapper.ToolMapper;
import com.aitools.backend.mapper.TutorialMapper;
import com.aitools.backend.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ToolMapper toolMapper;
    private final TutorialMapper tutorialMapper;
    private final UserMapper userMapper;
    private final ApiInfoMapper apiInfoMapper;
    private final PromptMapper promptMapper;

    public AdminDashboardResponse getDashboard(int recentLimit) {
        AdminDashboardResponse resp = new AdminDashboardResponse();

        AdminDashboardResponse.Stats stats = new AdminDashboardResponse.Stats();
        stats.setTools(Long.valueOf(toolMapper.selectCount(null)));
        stats.setTutorials(Long.valueOf(tutorialMapper.selectCount(null)));
        stats.setUsers(Long.valueOf(userMapper.selectCount(null)));
        stats.setApis(Long.valueOf(apiInfoMapper.selectCount(null)));
        stats.setPrompts(Long.valueOf(promptMapper.selectCount(null)));
        resp.setStats(stats);

        List<AdminDashboardResponse.Item> recent = new ArrayList<>();

        List<Tool> tools = toolMapper.selectList(new LambdaQueryWrapper<Tool>()
                .select(Tool::getId, Tool::getName, Tool::getCreatedAt)
                .orderByDesc(Tool::getCreatedAt)
                .last("LIMIT " + recentLimit));
        for (Tool t : tools) {
            AdminDashboardResponse.Item item = new AdminDashboardResponse.Item();
            item.setId(t.getId());
            item.setTitle(t.getName());
            item.setCreatedAt(t.getCreatedAt());
            item.setKind("tool");
            recent.add(item);
        }

        List<Tutorial> tutorials = tutorialMapper.selectList(new LambdaQueryWrapper<Tutorial>()
                .select(Tutorial::getId, Tutorial::getTitle, Tutorial::getCreatedAt)
                .orderByDesc(Tutorial::getCreatedAt)
                .last("LIMIT " + recentLimit));
        for (Tutorial t : tutorials) {
            AdminDashboardResponse.Item item = new AdminDashboardResponse.Item();
            item.setId(t.getId());
            item.setTitle(t.getTitle());
            item.setCreatedAt(t.getCreatedAt());
            item.setKind("tutorial");
            recent.add(item);
        }

        resp.setRecent(recent);
        return resp;
    }

    /**
     * 后台分页获取用户列表，支持关键词（用户名/邮箱）筛选；返回前清空密码字段
     */
    public PageResponse<User> getUsersPage(PageRequest pageRequest, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(User::getUsername, kw).or().like(User::getEmail, kw));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        IPage<User> result = userMapper.selectPage(page, wrapper);
        List<User> list = result.getRecords();
        for (User u : list) {
            u.setPassword(null);
        }
        User[] content = list.toArray(new User[0]);
        return new PageResponse<>(content, result.getTotal(),
                pageRequest.getPage().intValue(), pageRequest.getSize().intValue());
    }

    /**
     * 后台更新用户（仅允许更新 role、officeId；officeId 传 null 或空串表示清空）
     */
    public User updateUser(Long id, String role, String officeId) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (role != null) {
            existing.setRole(role);
        }
        if (officeId != null) {
            existing.setOfficeId(officeId.trim().isEmpty() ? null : officeId.trim());
        }
        userMapper.updateById(existing);
        existing.setPassword(null);
        return existing;
    }

    /**
     * 后台删除用户
     */
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}

