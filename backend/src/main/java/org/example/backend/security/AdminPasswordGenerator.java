package org.example.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminPasswordGenerator {
    public static void main(String[] args) {
        // 1. 管理员用户名和明文密码
        String username = "admin";
        String rawPassword = "123456";

        // 2. 创建 BCrypt 加密器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 3. 加密密码
        String encodedPassword = encoder.encode(rawPassword);

        // 4. 输出可用的 SQL 插入语句
        String sql = String.format(
                "INSERT INTO user (username, password, role) VALUES ('%s', '%s', 'ADMIN');",
                username, encodedPassword
        );

        System.out.println(sql);
    }
}
