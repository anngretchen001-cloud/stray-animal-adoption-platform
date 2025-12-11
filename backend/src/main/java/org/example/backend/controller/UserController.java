package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.R;
import org.example.backend.entity.User;
import org.example.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R<?> register(@RequestBody User user) {
        userService.register(user);
        return R.okMsg("注册成功");
    }

    /**
     * 登录，返回 token
     */
    @PostMapping("/login")
    public R<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        String token = userService.login(username, password);

        return R.ok(Map.of(
                "token", token
        ));
    }

    /**
     * 更新密码
     */
    @PutMapping("/password")
    public R<?> updatePassword(HttpServletRequest request,
                               @RequestBody Map<String, String> passwords) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return R.error("未登录");

        String oldPwd = passwords.get("oldPassword");
        String newPwd = passwords.get("newPassword");

        userService.updatePassword(userId, oldPwd, newPwd);

        return R.okMsg("密码修改成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public R<?> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return R.error("未登录");

        User user = userService.getById(userId);
        return R.ok(user);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public R<?> updateProfile(HttpServletRequest request,
                              @RequestBody User user) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return R.error("未登录");

        user.setId(userId);
        userService.updateProfile(user);

        return R.okMsg("资料更新成功");
    }
}
