package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.backend.entity.User;
import org.example.backend.exception.PasswordWrongException;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.mapper.UserMapper;
import org.example.backend.security.JwtUtil;
import org.example.backend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder(); // 初始化加密器
    }

    @Override
    public void register(User user) {
        // 检查用户名是否存在
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null) {
            throw new RuntimeException("用户名已存在"); // 也可以自定义异常
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) throw new UserNotFoundException("用户名不存在");

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordWrongException("密码错误");
        }
        // ⭐ 加封禁验证
        if (user.getEnabled() != null && !user.getEnabled()) {
            throw new RuntimeException("该用户已被封禁，无法登录");
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new UserNotFoundException("用户不存在");

        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new PasswordWrongException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new UserNotFoundException("用户不存在");
        return user;
    }

    @Override
    public void updateProfile(User user) {
        User dbUser = userMapper.selectById(user.getId());
        if (dbUser == null) throw new UserNotFoundException("用户不存在");

        dbUser.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : dbUser.getAvatarUrl());
        dbUser.setEmail(user.getEmail() != null ? user.getEmail() : dbUser.getEmail());
        dbUser.setPhone(user.getPhone() != null ? user.getPhone() : dbUser.getPhone());
        dbUser.setUpdatedAt(LocalDateTime.now());

        userMapper.updateById(dbUser);
    }
}
