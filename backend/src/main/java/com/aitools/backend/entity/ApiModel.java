package com.aitools.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("api_model")
public class ApiModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long apiId;

    private String name;

    private String description;

    @TableField("context_length")
    private Integer contextLength;

    /**
     * 存储为逗号分隔字符串，如：文本,图像,音频
     */
    private String capabilities;

    @TableField("is_recommended")
    private Boolean recommended;

    /**
     * 是否为“图像生成模型”（用于 Banner 图生成）
     */
    @TableField("is_image_model")
    private Boolean imageModel = false;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

