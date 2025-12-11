package org.example.backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean enabled;
    private String createTime; // 可以用 String 或 LocalDateTime，根据前端需要
}
