package org.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    // 评论用户信息
    private String authorName;
    private String authorAvatar;
}
