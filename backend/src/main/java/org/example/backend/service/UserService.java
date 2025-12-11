package org.example.backend.service;

import org.example.backend.entity.User;

public interface UserService {
    //注册
    void register(User user);
    //登录
    String login(String username,String password);
    //更新密码
    void updatePassword(Long userId,String oldPwd,String newPwd);
    //通过ID获取用户
    User getById(Long userId);
    //更新用户信息
    void updateProfile(User user);
}
