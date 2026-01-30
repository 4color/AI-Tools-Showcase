package com.aitools.backend.dto;

import com.aitools.backend.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
    private String officeId;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
        this.role = user.getRole();
        this.officeId = user.getOfficeId();
        this.createdAt = user.getCreatedAt();
    }
}