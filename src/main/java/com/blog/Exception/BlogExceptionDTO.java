package com.blog.Exception;

public class BlogExceptionDTO{
    private int status;

    private String message;
    public BlogExceptionDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
