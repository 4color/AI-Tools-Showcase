package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.dto.LlmChatRequest;
import com.aitools.backend.dto.LlmChatResponse;
import com.aitools.backend.dto.LlmImageGenerateRequest;
import com.aitools.backend.dto.UploadFileResponse;
import com.aitools.backend.service.LlmChatService;
import com.aitools.backend.service.LlmImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/llm")
@CrossOrigin
@RequiredArgsConstructor
public class LlmController {

    private final LlmChatService llmChatService;
    private final LlmImageService llmImageService;

    @PostMapping("/chat")
    public Result<LlmChatResponse> chat(@Valid @RequestBody LlmChatRequest request) {
        try {
            return Result.success(llmChatService.chat(request));
        } catch (Exception e) {
            return Result.error("对话失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/chat/stream", produces = "text/event-stream")
    public SseEmitter chatStream(@Valid @RequestBody LlmChatRequest request) {
        SseEmitter emitter = new SseEmitter(0L);
        llmChatService.chatStream(request, emitter);
        return emitter;
    }

    /**
     * 生成教程 Banner 图（默认图像提供商 + 图像模型），并保存到 uploads 目录（与上传接口一致）
     */
    @PostMapping("/image/generate")
    public Result<UploadFileResponse> generateTutorialBanner(@Valid @RequestBody LlmImageGenerateRequest request) {
        try {
            return Result.success(llmImageService.generateTutorialBanner(request));
        } catch (Exception e) {
            return Result.error("生成 Banner 失败: " + e.getMessage());
        }
    }
}

