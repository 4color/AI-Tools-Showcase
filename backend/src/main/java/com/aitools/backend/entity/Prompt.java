package com.aitools.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("prompt")
public class Prompt {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String description;

    @TableField("category_id")
    private Long categoryId;

    /**
     * 分类名（来自 categories 表，返回给前端用）
     */
    @TableField(exist = false)
    private String category;

    @TableField("creator_id")
    private Long creatorId;

    private Integer likes = 0;

    private Integer views = 0;

    @TableField("comment_count")
    private Integer commentCount = 0;

    /**
     * Banner/封面图（上传或 AI 生成的虚拟路径，如 /upload-file/...）
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 使用方法说明（支持 Markdown）
     */
    @TableField("usage_guide")
    private String usageGuide;

    /**
     * 生成效果图（以提示词内容为 AI 生成来源，展示在详情页提示词上方）
     */
    @TableField("effect_image")
    private String effectImage;

    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private User creator;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
