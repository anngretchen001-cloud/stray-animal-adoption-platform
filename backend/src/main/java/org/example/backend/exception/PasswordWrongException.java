package org.example.backend.exception;

public class PasswordWrongException extends RuntimeException{
    public PasswordWrongException(String msg) {
        super(msg);
    }
}
