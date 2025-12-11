package org.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    //JWT分为三部分：Header.Payload.Signature。Signature是使用密钥对前两部分签名生成的，用于验证Token是否被篡改。
    // 32 字节以上的秘钥，HS256 最少 256 bit，应该放在配置里，不过这里硬编码也没影响
    private static final String SECRET_KEY = "YourSecretKeyMustBeAtLeast32Bytes!";

    // token 有效期 24 小时(以毫秒为单位）
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    // 使用 Keys 工具类生成 Key 对象，对秘钥进行加密，后面用来签名
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 JWT token
     *
     * @param userId 用户id
     * @param role   用户角色
     * @return token
     */
    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
//    .claim("userId", userId)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
    //这一段就是最终token中的payload部分内容。
    //signWith部分则会自动生成header，基于这俩生成签名

    /**
     * 验证 token 是否有效
     *
     * @param token token 字符串
     * @return true 有效，false 无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从 token 中获取用户 id
     *
     * @param token token
     * @return userId
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 token 中获取角色
     *
     * @param token token
     * @return role
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 解析 token
     *
     * @param token token
     * @return Claims
     * @throws JwtException token 无效或过期
     */
    private Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()//创建解析器建造器
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //设置签名秘钥（传入生成Token时使用的相同密钥）、创建解析器实例、解析并验证JWT、返回payload部分
    //.parseClaimsJws(token)：
//    1. 分割Token: header.payload.signature
//2. Base64Url解码Header → 获取算法信息
//3. Base64Url解码Payload → 获取Claims数据
//4. 重新计算签名: HMACSHA256(header + "." + payload, key)
//5. 比较计算签名与传入签名是否一致
//6. 检查exp是否大于当前时间
//7. 检查nbf是否小于当前时间（如果有）
//            8. 验证其他标准声明...
//            9. 所有验证通过，返回Jws<Claims>对象
}
