package com.aitools.backend.dto;

import lombok.Data;

@Data
public class LlmImageGenerateRequest {

    /**
     * 类型：tutorial-banner / prompt-banner / prompt-effect，决定存储目录与默认尺寸/提示词模板。
     * prompt-effect 时以 content（提示词全文）为 AI 生成内容来源；其他类型用 title+description。
     */
    private String type;

    /** 标题（banner 类型时使用） */
    private String title;

    /** 简介（banner 类型时使用） */
    private String description;

    /** 提示词全文（prompt-effect 时作为 AI 生成的内容来源） */
    private String content;

    /**
     * 可选：输出尺寸（OpenAI 兼容 images/generations 的 size），如 1024x1024
     */
    private String size;
}

