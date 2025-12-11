package org.example.backend.common;

import lombok.Data;

@Data
public class R<T> {
    private int code;       // 0 成功，1 失败
    private String message;
    private T data;

    /** 返回带 data 的成功响应 */
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    /** 返回只带 message 的成功响应，data 为 null */
    public static <T> R<T> okMsg(String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.message = msg;
        r.data = null;
        return r;
    }

    /** 返回失败响应，data 为 null */
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 1;
        r.message = msg;
        r.data = null;
        return r;
    }
}
