package com.aitools.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadFileResponse {
    /**
     * 可直接访问的虚拟路径，如：/upload-file/images/2026/01/xx.png
     */
    private String url;

    /**
     * 相对存储路径，如：images/2026/01/xx.png
     */
    private String path;

    /**
     * 原始文件名
     */
    private String originalName;
}

