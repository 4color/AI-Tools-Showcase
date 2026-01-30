package com.aitools.backend.service;

import com.aitools.backend.config.FileProperties;
import com.aitools.backend.config.UploadTypeConfig;
import com.aitools.backend.dto.LlmImageGenerateRequest;
import com.aitools.backend.dto.UploadFileResponse;
import com.aitools.backend.entity.ApiInfo;
import com.aitools.backend.entity.ApiModel;
import com.aitools.backend.mapper.ApiInfoMapper;
import com.aitools.backend.mapper.ApiModelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LlmImageService {

    private final ApiInfoMapper apiInfoMapper;
    private final ApiModelMapper apiModelMapper;
    private final RestTemplate restTemplate;
    private final FileProperties fileProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成 Banner 图（通用）：按 type 从 yaml 读取 folder/尺寸/提示词模板，选择默认图像大模型并保存到 uploads。
     * type 为 tutorial-banner / prompt-banner 等，不传时默认 tutorial-banner。
     */
    public UploadFileResponse generateBanner(LlmImageGenerateRequest req) {
        String type = (req.getType() != null && !req.getType().trim().isEmpty()) ? req.getType().trim() : "tutorial-banner";
        UploadTypeConfig config = fileProperties.getUploadType(type);
        if (config == null) {
            throw new RuntimeException("未配置的上传类型: " + type + "，请在 file.upload-types 中配置");
        }
        String folder = config.getFolder() != null && !config.getFolder().isEmpty() ? config.getFolder() : type;
        String promptText;
        if ("prompt-effect".equals(type)) {
            String content = req.getContent() != null ? req.getContent().trim() : "";
            if (content.isEmpty()) {
                throw new RuntimeException("生成效果图时请提供提示词内容（content）");
            }
            promptText = buildPromptFromTemplate(
                    config.getPromptTemplate(),
                    "",
                    "",
                    content
            );
        } else {
            String title = req.getTitle() != null ? req.getTitle() : "";
            String description = req.getDescription() != null ? req.getDescription() : "";
            if (title.isEmpty() && description.isEmpty()) {
                throw new RuntimeException("请提供标题或简介");
            }
            promptText = buildPromptFromTemplate(
                    config.getPromptTemplate(),
                    title,
                    description,
                    null
            );
        }
        String size = (req.getSize() != null && !req.getSize().trim().isEmpty()) ? req.getSize().trim() : config.getDefaultSize();
        if (size == null || size.isEmpty()) size = "1024x1024";
        String sizeDashscope = (config.getDefaultSizeDashscope() != null && !config.getDefaultSizeDashscope().isEmpty())
                ? config.getDefaultSizeDashscope() : "1440*512";

        ApiInfo api = apiInfoMapper.selectOne(
                new LambdaQueryWrapper<ApiInfo>()
                        .eq(ApiInfo::getDefaultImageProvider, true)
                        .last("LIMIT 1")
        );
        if (api == null) {
            throw new RuntimeException("未配置默认图像大模型提供商（api_info.is_default_image_provider）");
        }
        ApiModel model = apiModelMapper.selectOne(
                new LambdaQueryWrapper<ApiModel>()
                        .eq(ApiModel::getApiId, api.getId())
                        .eq(ApiModel::getImageModel, true)
                        .last("LIMIT 1")
        );
        if (model == null) {
            throw new RuntimeException("未配置图像生成模型（api_model.is_image_model）");
        }
        if (api.getBaseUrl() == null || api.getBaseUrl().trim().isEmpty()) {
            throw new RuntimeException("默认图像提供商 baseUrl 未配置");
        }
        if (api.getApiKey() == null || api.getApiKey().trim().isEmpty()) {
            throw new RuntimeException("默认图像提供商 apiKey 未配置");
        }

        try {
            if (isDashScope(api)) {
                return generateWithDashScope(api, model, folder, promptText, sizeDashscope);
            }
            return generateWithOpenAICompatible(api, model, folder, promptText, size);
        } catch (RuntimeException e) {
            throw new RuntimeException("apiId=" + api.getId()
                    + ", provider=" + safe(api.getProvider())
                    + ", baseUrl=" + safe(api.getBaseUrl())
                    + ", modelId=" + model.getId()
                    + ", modelName=" + safe(model.getName())
                    + " -> " + e.getMessage());
        }
    }

    /** 兼容旧接口：按请求中的 type 或默认 tutorial-banner 调用 generateBanner */
    public UploadFileResponse generateTutorialBanner(LlmImageGenerateRequest req) {
        return generateBanner(req);
    }

    private UploadFileResponse generateWithOpenAICompatible(ApiInfo api, ApiModel model, String folder, String promptText, String size) {
        String url = normalizeBaseUrl(api.getBaseUrl()) + "/images/generations";

        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        body.put("prompt", promptText);
        body.put("size", size);
        body.put("n", 1);
        body.put("response_format", "b64_json");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(normalizeApiKey(api.getApiKey()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("上游调用失败: " + e.getStatusCode().value() + " " + e.getStatusText()
                    + (e.getResponseBodyAsString() == null || e.getResponseBodyAsString().isEmpty() ? "" : (": " + e.getResponseBodyAsString())));
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("图像生成失败");
        }

        byte[] bytes = parseImageBytesOpenAI(resp.getBody());
        return saveBytesToUploads(bytes, folder, "banner.png");
    }

    /**
     * DashScope 异步：创建任务 -> 轮询 task -> 下载图片 -> 存储到 uploads
     * <p>
     * 关键点：
     * - 创建任务必须设置 Header：X-DashScope-Async: enable
     * - 轮询 GET /api/v1/tasks/{task_id}
     */
    private UploadFileResponse generateWithDashScope(ApiInfo api, ApiModel model, String folder, String promptText, String sizeDashscope) {
        String base = normalizeBaseUrl(api.getBaseUrl());
        String createUrl = base + "/api/v1/services/aigc/text2image/image-synthesis";

        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", promptText);
        body.put("input", input);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("negative_prompt", " ");
        parameters.put("size", sizeDashscope);
        parameters.put("n", 1);
        parameters.put("prompt_extend", true);
        parameters.put("watermark", false);
        body.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(normalizeApiKey(api.getApiKey()));
        headers.set("X-DashScope-Async", "enable");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(createUrl, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("DashScope 任务创建失败: " + e.getStatusCode().value() + " " + e.getStatusText()
                    + (e.getResponseBodyAsString() == null || e.getResponseBodyAsString().isEmpty() ? "" : (": " + e.getResponseBodyAsString())));
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("图像任务创建失败");
        }

        String taskId = parseDashScopeTaskId(resp.getBody());
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new RuntimeException("任务创建成功但未返回 task_id");
        }

        String taskUrl = base + "/api/v1/tasks/" + taskId;
        String imageUrl = pollDashScopeTaskForImageUrl(taskUrl, api.getApiKey(), Duration.ofSeconds(60), Duration.ofSeconds(2));

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new RuntimeException("任务已完成但未返回图片地址");
        }

        try {
            URI uri = new URI(imageUrl);
            byte[] bytes = restTemplate.getForObject(uri, byte[].class);
            return saveBytesToUploads(bytes, folder, "banner.png");
        } catch (Exception e) {
            throw new RuntimeException("任务已完成但图片下载失败: " + e.getMessage());
        }
    }

    private String parseDashScopeTaskId(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            // 常见返回：output.task_id
            JsonNode id = root.path("output").path("task_id");
            if (!id.isMissingNode() && !id.isNull() && !id.asText().isEmpty()) {
                return id.asText();
            }
            // 兜底：task_id
            JsonNode id2 = root.path("task_id");
            if (!id2.isMissingNode() && !id2.isNull() && !id2.asText().isEmpty()) {
                return id2.asText();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("任务创建返回解析失败: " + e.getMessage());
        }
    }

    /**
     * 轮询任务直到 SUCCEEDED/FAILED/UNKNOWN 或超时
     */
    private String pollDashScopeTaskForImageUrl(String taskUrl, String apiKey, Duration timeout, Duration interval) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        String key = normalizeApiKey(apiKey);

        while (System.currentTimeMillis() < deadline) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(key);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<String> resp;
                try {
                    resp = restTemplate.exchange(taskUrl, HttpMethod.GET, entity, String.class);
                } catch (HttpStatusCodeException e) {
                    throw new RuntimeException("DashScope 任务查询失败: " + e.getStatusCode().value() + " " + e.getStatusText()
                            + (e.getResponseBodyAsString() == null || e.getResponseBodyAsString().isEmpty() ? "" : (": " + e.getResponseBodyAsString())));
                }

                if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                    throw new RuntimeException("任务查询失败");
                }

                JsonNode root = objectMapper.readTree(resp.getBody());
                JsonNode statusNode = root.path("output").path("task_status");
                String status = statusNode.isMissingNode() || statusNode.isNull() ? "" : statusNode.asText();

                if ("SUCCEEDED".equalsIgnoreCase(status)) {
                    // 常见返回：output.results[0].url
                    JsonNode urlNode = root.path("output").path("results").path(0).path("url");
                    if (!urlNode.isMissingNode() && !urlNode.isNull() && !urlNode.asText().isEmpty()) {
                        return urlNode.asText();
                    }
                    // 兜底：results[0].url
                    JsonNode urlNode2 = root.path("results").path(0).path("url");
                    if (!urlNode2.isMissingNode() && !urlNode2.isNull() && !urlNode2.asText().isEmpty()) {
                        return urlNode2.asText();
                    }
                    return null;
                }

                if ("FAILED".equalsIgnoreCase(status)) {
                    String msg = root.path("output").path("message").asText();
                    throw new RuntimeException(msg == null || msg.isEmpty() ? "图像生成失败" : msg);
                }

                if ("UNKNOWN".equalsIgnoreCase(status)) {
                    throw new RuntimeException("任务状态 UNKNOWN（task_id 可能已过期，DashScope task_id 有效期 24 小时）");
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception ignore) {
                // 解析/网络短暂失败：继续轮询
            }

            try {
                Thread.sleep(Math.max(200L, interval.toMillis()));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("轮询被中断");
            }
        }

        throw new RuntimeException("图像生成超时，请稍后重试");
    }

    private byte[] parseImageBytesOpenAI(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode data0 = root.path("data").path(0);
            JsonNode b64 = data0.path("b64_json");
            if (!b64.isMissingNode() && !b64.isNull() && !b64.asText().isEmpty()) {
                return Base64.getDecoder().decode(b64.asText());
            }
            JsonNode url = data0.path("url");
            if (!url.isMissingNode() && !url.isNull() && !url.asText().isEmpty()) {
                return restTemplate.getForObject(url.asText(), byte[].class);
            }
            throw new RuntimeException("图像返回解析失败");
        } catch (Exception e) {
            throw new RuntimeException("图像返回解析失败: " + e.getMessage());
        }
    }

    private UploadFileResponse saveBytesToUploads(byte[] bytes, String folder, String originalName) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("图像数据为空");
        }

        String safeFolder = sanitizeFolder(folder);
        LocalDate now = LocalDate.now();
        String subDir = safeFolder + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue());

        String filename = UUID.randomUUID().toString().replace("-", "") + ".png";

        try {
            Path baseDir = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
            Path targetDir = baseDir.resolve(subDir).normalize();
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename).normalize();
            Files.write(targetFile, bytes);

            String prefix = fileProperties.getUrlPrefix();
            if (!prefix.startsWith("/")) prefix = "/" + prefix;
            if (!prefix.endsWith("/")) prefix = prefix + "/";

            String relativePath = subDir + "/" + filename;
            String url = prefix + relativePath;
            return new UploadFileResponse(url, relativePath, originalName);
        } catch (Exception e) {
            throw new RuntimeException("保存图像失败: " + e.getMessage());
        }
    }

    private boolean isDashScope(ApiInfo api) {
        String provider = api.getProvider() == null ? "" : api.getProvider().toLowerCase();
        String baseUrl = api.getBaseUrl() == null ? "" : api.getBaseUrl().toLowerCase();
        return provider.contains("dashscope")
                || provider.contains("aliyun")
                || baseUrl.contains("dashscope.aliyuncs.com")
                || baseUrl.contains("dashscope-intl.aliyuncs.com");
    }

    /** 从 yaml 配置的 prompt-template 替换 {title}、{description} */
    /** 支持占位符 {title}、{description}、{content}（效果图类型用 content） */
    private String buildPromptFromTemplate(String template, String title, String description, String content) {
        if (template == null || template.isEmpty()) {
            if (content != null && !content.isEmpty()) {
                return "Effect image for prompt: " + content;
            }
            return "Banner image. Title: " + (title != null ? title : "") + ". Description: " + (description != null ? description : "") + ".";
        }
        String t = template
                .replace("{title}", title != null ? title : "")
                .replace("{description}", description != null ? description : "");
        return t.replace("{content}", content != null ? content : "");
    }

    private static String sanitizeFolder(String folder) {
        String f = (folder == null || folder.trim().isEmpty()) ? "images" : folder.trim();
        f = f.replace("\\", "/");
        f = f.replaceAll("[^a-zA-Z0-9_\\-/]", "");
        f = f.replaceAll("/+", "/");
        if (f.startsWith("/")) f = f.substring(1);
        if (f.endsWith("/")) f = f.substring(0, f.length() - 1);
        if (f.isEmpty()) f = "images";
        return f;
    }

    private String normalizeBaseUrl(String baseUrl) {
        String b = baseUrl.trim();
        while (b.endsWith("/")) {
            b = b.substring(0, b.length() - 1);
        }
        return b;
    }

    /**
     * 兼容两种存储方式：
     * - 仅存 key：sk-xxxx / DASHSCOPE_API_KEY
     * - 存 "Bearer xxx"（避免 setBearerAuth 再次叠加 Bearer）
     */
    private String normalizeApiKey(String raw) {
        if (raw == null) return "";
        String k = raw.trim();
        if (k.toLowerCase().startsWith("bearer ")) {
            k = k.substring("bearer ".length()).trim();
        }
        return k;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

