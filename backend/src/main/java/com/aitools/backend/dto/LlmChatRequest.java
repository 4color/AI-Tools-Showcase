package com.aitools.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LlmChatRequest {

    @NotNull
    private Long apiId;

    @NotNull
    private Long modelId;

    private List<Message> messages;

    private Double temperature;
    private Integer maxTokens;
    private Double topP;

    @Data
    public static class Message {
        @NotBlank
        private String role; // user | assistant | system

        @NotBlank
        private String content;
    }
}

