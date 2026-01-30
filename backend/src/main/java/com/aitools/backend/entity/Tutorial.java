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
@TableName("tutorial")
public class Tutorial {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String content;

    /**
     * 教程 Banner / 封面图（上传或 AI 生成的虚拟路径，如：/upload-file/...）
     */
    @TableField("cover_image")
    private String coverImage;

    @TableField("category_id")
    private Long categoryId;

    // 分类名（来自 categories 表）
    @TableField(exist = false)
    private String category;

    @TableField("tool_id")
    private Long toolId;

    @TableField("author_id")
    private Long authorId;

    private String difficulty = "BEGINNER";

    @TableField(exist = false)
    private List<String> tags;

    @TableField("read_time")
    private Integer readTime;

    @TableField("view_count")
    private Integer viewCount = 0;

    // 作者名称（关联查询）
    @TableField(exist = false)
    private String authorName;

    // 点赞数（关联查询）
    @TableField(exist = false)
    private Integer likeCount;

    // 是否置顶
    private Boolean pinned = false;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
