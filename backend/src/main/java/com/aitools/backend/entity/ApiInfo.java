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
@TableName("api_info")
public class ApiInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String provider;

    private String description;

    private String documentation;

    private String pricing;

    private Long categoryId;

    @TableField(exist = false)
    private String category;

    @TableField("base_url")
    private String baseUrl;

    /**
     * 注意：该字段为敏感信息，接口返回时不要直接透出
     */
    @TableField("api_key")
    private String apiKey;

    @TableField("logo_url")
    private String logoUrl;

    /**
     * 是否默认图像大模型提供商（用于 Banner 图生成）
     */
    @TableField("is_default_image_provider")
    private Boolean defaultImageProvider = false;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<String> features;
}
