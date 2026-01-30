package com.aitools.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    /**
     * 上传文件存储目录（本地路径）
     */
    private String uploadDir = "uploads";

    /**
     * 对外访问的虚拟前缀（建议以 / 开头，以 / 结尾）
     */
    private String urlPrefix = "/upload-file/";

    /**
     * 按类型配置：key 为类型名（如 tutorial-banner、prompt-banner），也是上传时允许的 folder 值。
     */
    private Map<String, UploadTypeConfig> uploadTypes = new LinkedHashMap<>();

    /**
     * 允许的上传 folder 列表：upload-types 的 key + 默认 images
     */
    public Set<String> getAllowedFolders() {
        Set<String> set = uploadTypes.keySet().stream().collect(Collectors.toSet());
        set.add("images");
        set.add("tool-logo");
        return Collections.unmodifiableSet(set);
    }

    /**
     * 根据类型名取配置；若不存在则返回 null
     */
    public UploadTypeConfig getUploadType(String type) {
        return type == null ? null : uploadTypes.get(type.trim());
    }
}

