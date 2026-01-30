package com.aitools.backend.service;

import com.aitools.backend.entity.Comment;
import com.aitools.backend.entity.User;
import com.aitools.backend.mapper.CommentMapper;
import com.aitools.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public List<Comment> getCommentsByEntity(String entityType, Long entityId) {
        List<Comment> comments = commentMapper.findByEntity(entityType, entityId);
        // 填充用户信息
        return comments.stream().map(comment -> {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
                comment.setUserAvatar(user.getAvatarUrl());
            }
            return comment;
        }).collect(Collectors.toList());
    }

    public Comment saveComment(Comment comment) {
        if (comment.getId() == null) {
            commentMapper.insert(comment);
        } else {
            commentMapper.updateById(comment);
        }
        return comment;
    }

    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }

    public Double getAverageRating(String entityType, Long entityId) {
        return commentMapper.findAverageRating(entityType, entityId);
    }

    public Integer getRatingCount(String entityType, Long entityId) {
        return commentMapper.countRatings(entityType, entityId);
    }
}