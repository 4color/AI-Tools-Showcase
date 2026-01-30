package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.HomeStatsResponse;
import com.aitools.backend.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@CrossOrigin
@Tag(name = "首页统计", description = "首页 4 项指标数据")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    @Operation(summary = "获取首页指标", description = "返回 AI 工具数、教程数、用户数、平均评分")
    public Result<HomeStatsResponse> getStats() {
        return Result.success(homeService.getStats());
    }
}
