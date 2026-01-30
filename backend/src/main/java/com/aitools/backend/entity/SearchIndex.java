package com.aitools.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一搜索索引：主键为 (entity_type, entity_id)，非自增
 */
@Data
@NoArgsConstructor
@TableName("search_index")
public class SearchIndex {

    @TableField("entity_type")
    private String entityType;

    @TableField("entity_id")
    private Long entityId;

    private String title;

    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
