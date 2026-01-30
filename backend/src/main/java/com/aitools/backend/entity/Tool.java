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
@TableName("tool")
public class Tool {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String introduction;

    @TableField(exist = false)
    private List<String> tags;

    private String url;

    @TableField("logo_url")
    private String logoUrl;
    
    @TableField("category")
    private String category;

    private String price = "FREE";

    private Double rating = 0.0;

    /**
     * 是否置顶 0否 1是
     */
    private Integer pinned = 0;

    @TableField("review_count")
    private Integer reviewCount = 0;

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField(exist = false)
    private List<String> features;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
