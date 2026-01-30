package com.aitools.backend.config;

import lombok.Data;

/**
 * 某类上传/Banner 的配置：存储子目录、默认尺寸、AI 生成用的提示词模板等。
 */
@Data
public class UploadTypeConfig {
    /** 存储子目录，如 tutorial-banner、prompt-banner */
    private String folder = "images";
    /** OpenAI 兼容接口的默认尺寸，如 1024x1024 */
    private String defaultSize = "1024x1024";
    /** DashScope 的默认尺寸，如 1440*512 */
    private String defaultSizeDashscope = "1440*512";
    /** AI 生成 Banner 的提示词模板，支持占位符 {title}、{description} */
    private String promptTemplate = "Banner image for: {title}. Description: {description}.";
}
