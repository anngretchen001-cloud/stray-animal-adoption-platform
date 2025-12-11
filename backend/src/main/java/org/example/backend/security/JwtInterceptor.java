package org.example.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.exception.UserNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 注册和登录接口不需要 token
        if (path.startsWith("/api/users/register") || path.startsWith("/api/users/login")) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"未提供token\"}");
            return false;
        }

        token = token.substring(7); // 去掉 "Bearer "

        try {
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\":\"token无效或已过期\"}");
                return false;
            }

            // 将 userId 放入 request attribute，Controller 可直接获取
            Long userId = jwtUtil.getUserId(token);
            String role=jwtUtil.getRole(token);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            // 角色校验：如果访问 admin 接口，必须是 ADMIN
            if(path.startsWith("/api/admin") && !"ADMIN".equals(role)){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"message\":\"权限不足\"}");
                return false;
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"token解析失败\"}");
            return false;
        }

        return true; // token 合法，放行
    }

}
