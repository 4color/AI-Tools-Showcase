package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.config.FileProperties;
import com.aitools.backend.dto.UploadFileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@CrossOrigin
@Tag(name = "文件上传", description = "通用文件上传接口")
public class FileController {

    private final FileProperties fileProperties;

    public FileController(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    // Java 8 兼容写法
    private static final Set<String> ALLOWED_EXT = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif", "webp", "svg"))
    );

    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "通用图片上传接口，仅允许图片文件。返回可访问的虚拟路径 /upload-file/... ")
    public Result<UploadFileResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder
    ) {
        if (file == null || file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return Result.error(400, "仅允许上传图片文件");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = getExtension(originalName);
        if (!ext.isEmpty() && !ALLOWED_EXT.contains(ext)) {
            return Result.error(400, "不支持的图片格式");
        }

        // 存储子目录：仅允许配置中声明的 folder（upload-types 的 key 或 images）
        String requestedFolder = (folder == null || folder.trim().isEmpty()) ? "images" : folder.trim();
        if (!fileProperties.getAllowedFolders().contains(requestedFolder)) {
            return Result.error(400, "不支持的上传类型 folder，允许值: " + String.join(", ", fileProperties.getAllowedFolders()));
        }
        String safeFolder = requestedFolder;
        LocalDate now = LocalDate.now();
        String subDir = safeFolder + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue());

        String filename = UUID.randomUUID().toString().replace("-", "");
        if (!ext.isEmpty()) filename = filename + "." + ext;

        Path baseDir = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
        Path targetDir = baseDir.resolve(subDir).normalize();

        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename).normalize();
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }

        String prefix = fileProperties.getUrlPrefix();
        if (!prefix.startsWith("/")) prefix = "/" + prefix;
        if (!prefix.endsWith("/")) prefix = prefix + "/";

        String relativePath = subDir + "/" + filename;
        String url = prefix + relativePath;
        return Result.success(new UploadFileResponse(url, relativePath, originalName));
    }

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private static String sanitizeFolder(String folder) {
        String f = (folder == null || folder.trim().isEmpty()) ? "images" : folder.trim();
        // 只允许字母数字、下划线、短横线、斜杠
        f = f.replace("\\", "/");
        f = f.replaceAll("[^a-zA-Z0-9_\\-/]", "");
        // 去除多余的斜杠
        f = f.replaceAll("/+", "/");
        if (f.startsWith("/")) f = f.substring(1);
        if (f.endsWith("/")) f = f.substring(0, f.length() - 1);
        if (f.isEmpty()) f = "images";
        return f;
    }
}

