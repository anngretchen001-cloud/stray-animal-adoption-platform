package org.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private String type;
    private String coverUrl;
    private Integer viewCount;
    private LocalDateTime createdAt;

    // 作者信息
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String authorRole;
}
