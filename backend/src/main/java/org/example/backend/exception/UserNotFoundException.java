package org.example.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
//继承RuntimeException变成运行时异常，可以被全局异常处理器捕获。构造方法super(msg)设置
//异常信息，可以通过getMessage()获取。
//service层抛出异常，handler捕获，返回前端标准信息。
