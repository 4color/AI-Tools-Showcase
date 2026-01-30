package com.aitools.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "首页 4 项指标数据")
public class HomeStatsResponse {

    @Schema(description = "AI 工具数量")
    private Long toolCount;

    @Schema(description = "教程文章数量")
    private Long tutorialCount;

    @Schema(description = "活跃用户数量")
    private Long userCount;

    @Schema(description = "用户评分（工具平均评分，1 位小数）")
    private Double averageRating;
}
