package com.aitools.backend.service;

import com.aitools.backend.dto.AuthResponse;
import com.aitools.backend.dto.LoginRequest;
import com.aitools.backend.dto.RegisterRequest;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.UserMapper;
import com.aitools.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // 前端已使用 SHA256 加密密码，这里用 BCrypt 再次加密存储
        // 数据库存储格式：BCrypt(SHA256(原始密码))
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        userMapper.insert(user);
        
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        return new AuthResponse(token, new com.aitools.backend.dto.UserResponse(user));
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 前端已使用 SHA256 加密密码，request.getPassword() 是 SHA256 值
        // BCrypt.matches() 会自动将 SHA256 值用 BCrypt 加密后与数据库中的 BCrypt 值对比
        // 数据库存储格式：BCrypt(SHA256(原始密码))
        // 对比过程：BCrypt.matches(SHA256(原始密码), BCrypt(SHA256(原始密码)))
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        return new AuthResponse(token, new com.aitools.backend.dto.UserResponse(user));
    }
}