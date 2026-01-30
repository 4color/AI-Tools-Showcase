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
@TableName("tags")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /**
     * 栏目类型：tool/tutorial/api/prompt（共用 tags 表）
     */
    private String type;

    /**
     * 栏目数据主键ID
     */
    @TableField("entity_id")
    private Long entityId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}