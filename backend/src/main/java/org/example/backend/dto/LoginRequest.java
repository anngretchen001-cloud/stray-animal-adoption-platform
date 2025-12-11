package org.example.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
//DTO分层隔离，避免后端修改user表导致前段也崩，而且也更安全，避免了将后端数据库表结构直接暴露到接口。
