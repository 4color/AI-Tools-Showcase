package com.aitools.backend.service;

import com.aitools.backend.dto.LlmChatRequest;
import com.aitools.backend.dto.LlmChatResponse;
import com.aitools.backend.entity.ApiInfo;
import com.aitools.backend.entity.ApiModel;
import com.aitools.backend.mapper.ApiInfoMapper;
import com.aitools.backend.mapper.ApiModelMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LlmChatService {

    private final ApiInfoMapper apiInfoMapper;
    private final ApiModelMapper apiModelMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 目前先按 OpenAI 兼容协议转发：POST {baseUrl}/chat/completions
     * 后续如果需要支持 Gemini/Claude 非兼容协议，可以在此按 provider 做适配。
     */
    public LlmChatResponse chat(LlmChatRequest request) {
        ApiInfo api = apiInfoMapper.selectById(request.getApiId());
        if (api == null) {
            throw new RuntimeException("API 不存在");
        }
        ApiModel model = apiModelMapper.selectById(request.getModelId());
        if (model == null || model.getApiId() == null || !model.getApiId().equals(api.getId())) {
            throw new RuntimeException("模型不存在或不属于该 API");
        }
        if (api.getBaseUrl() == null || api.getBaseUrl().trim().isEmpty()) {
            throw new RuntimeException("API baseUrl 未配置");
        }
        if (api.getApiKey() == null || api.getApiKey().trim().isEmpty()) {
            throw new RuntimeException("API Key 未配置");
        }

        String url = normalizeBaseUrl(api.getBaseUrl()) + "/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());

        List<Map<String, String>> messages = (request.getMessages() == null ? java.util.Collections.<LlmChatRequest.Message>emptyList() : request.getMessages())
                .stream()
                .map(m -> {
                    Map<String, String> mm = new HashMap<>();
                    mm.put("role", m.getRole());
                    mm.put("content", m.getContent());
                    return mm;
                })
                .collect(Collectors.toList());
        body.put("messages", messages);

        if (request.getTemperature() != null) body.put("temperature", request.getTemperature());
        if (request.getTopP() != null) body.put("top_p", request.getTopP());
        if (request.getMaxTokens() != null) body.put("max_tokens", request.getMaxTokens());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(normalizeApiKey(api.getApiKey()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("模型调用失败: " + e.getStatusCode().value() + " " + e.getStatusText()
                    + (e.getResponseBodyAsString() == null || e.getResponseBodyAsString().isEmpty() ? "" : (": " + e.getResponseBodyAsString())));
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("模型调用失败");
        }

        try {
            JsonNode root = objectMapper.readTree(resp.getBody());
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                throw new RuntimeException("模型返回解析失败");
            }
            LlmChatResponse out = new LlmChatResponse();
            out.setContent(content.asText());
            return out;
        } catch (Exception e) {
            throw new RuntimeException("模型返回解析失败: " + e.getMessage());
        }
    }

    /**
     * 流式输出（SSE）：将上游 OpenAI 兼容 stream 响应逐段转发给前端
     */
    public void chatStream(LlmChatRequest request, SseEmitter emitter) {
        // 异步线程避免占用 servlet 线程
        new Thread(() -> {
            try {
                ApiInfo api = apiInfoMapper.selectById(request.getApiId());
                if (api == null) throw new RuntimeException("API 不存在");
                ApiModel model = apiModelMapper.selectById(request.getModelId());
                if (model == null || model.getApiId() == null || !model.getApiId().equals(api.getId())) {
                    throw new RuntimeException("模型不存在或不属于该 API");
                }
                if (api.getBaseUrl() == null || api.getBaseUrl().trim().isEmpty()) {
                    throw new RuntimeException("API baseUrl 未配置");
                }
                if (api.getApiKey() == null || api.getApiKey().trim().isEmpty()) {
                    throw new RuntimeException("API Key 未配置");
                }

                String url = normalizeBaseUrl(api.getBaseUrl()) + "/chat/completions";

                Map<String, Object> body = new HashMap<>();
                body.put("model", model.getName());
                body.put("stream", true);

                List<Map<String, String>> messages = (request.getMessages() == null ? java.util.Collections.<LlmChatRequest.Message>emptyList() : request.getMessages())
                        .stream()
                        .map(m -> {
                            Map<String, String> mm = new HashMap<>();
                            mm.put("role", m.getRole());
                            mm.put("content", m.getContent());
                            return mm;
                        })
                        .collect(Collectors.toList());
                body.put("messages", messages);

                if (request.getTemperature() != null) body.put("temperature", request.getTemperature());
                if (request.getTopP() != null) body.put("top_p", request.getTopP());
                if (request.getMaxTokens() != null) body.put("max_tokens", request.getMaxTokens());

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "text/event-stream");
                conn.setRequestProperty("Authorization", "Bearer " + normalizeApiKey(api.getApiKey()));

                byte[] json = objectMapper.writeValueAsBytes(body);
                conn.getOutputStream().write(json);
                conn.getOutputStream().flush();

                int status = conn.getResponseCode();
                InputStream stream = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
                if (stream == null) {
                    throw new RuntimeException("上游无响应");
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    if (!line.startsWith("data:")) continue;

                    // 注意：不要 trim()，否则会丢失空格/换行等 token
                    String data = line.substring("data:".length());
                    if (data.startsWith(" ")) {
                        data = data.substring(1);
                    }
                    if ("[DONE]".equals(data)) {
                        emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                        emitter.complete();
                        return;
                    }

                    // OpenAI stream chunk: choices[0].delta.content
                    try {
                        JsonNode root = objectMapper.readTree(data);
                        JsonNode delta = root.path("choices").path(0).path("delta").path("content");
                        if (!delta.isMissingNode() && !delta.isNull()) {
                            String token = delta.asText();
                            emitter.send(SseEmitter.event().name("token").data(new TokenChunk(token)));
                        }
                    } catch (Exception ignore) {
                        // 忽略无法解析的 chunk
                    }
                }

                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(new ErrorChunk(e.getMessage())));
                } catch (Exception ignore) {
                }
                emitter.completeWithError(e);
            }
        }, "llm-stream").start();
    }

    @lombok.Data
    static class TokenChunk {
        private final String token;
    }

    @lombok.Data
    static class ErrorChunk {
        private final String message;
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
     * - 存 "Bearer xxx"（避免重复叠加 Bearer 导致 401）
     */
    private String normalizeApiKey(String raw) {
        if (raw == null) return "";
        String k = raw.trim();
        if (k.toLowerCase().startsWith("bearer ")) {
            k = k.substring("bearer ".length()).trim();
        }
        return k;
    }
}

