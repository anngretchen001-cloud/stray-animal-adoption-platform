package org.example.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
//内置了 @ResponseBody，返回值自动序列化为 JSON等格式。
//为 RESTful 风格的 Web 应用提供全局的、统一的增强处理
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message",e.getMessage()));
        //Map.of(): 这是 Java 9 引入的便捷方法，用于快速创建一个不可变的键值对集合（Map）
        //这里创建了一个只包含一个键值对的 Map。
    }
    //方法返回一个 ResponseEntity对象，其 body 部分是一个 Map对象。
    //HTTP 状态码: 400 Bad Request
    //响应头 (Headers):Content-Type: application/json
    //响应体 (Body):{
    //  "message": "这里是在异常中定义的具体错误信息"
    //}

    @ExceptionHandler(PasswordWrongException.class)
    public ResponseEntity<?> handlePasswordWrong(PasswordWrongException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message",e.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }//兜底异常处理


}
