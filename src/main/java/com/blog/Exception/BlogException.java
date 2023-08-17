package com.blog.Exception;

public class BlogException extends RuntimeException{
    private int code;
    public static final String BLOG_EXCEPTION = "BLOG_EXCEPTION";
    private BlogExceptionDTO blogExceptionDTO;
    public BlogException(String errorMessage) {
        this(500, BLOG_EXCEPTION, errorMessage, null);
    }

    public BlogException(String errorMessage, Throwable error) {
        this(500, BLOG_EXCEPTION, errorMessage, error);
    }

    public BlogException(int code, String errorMessage) {
        this(code, BLOG_EXCEPTION, errorMessage, null);
    }

    public BlogException(int code, String errorCode, String errorMessage) {
        this(code, errorCode, errorMessage, null);
    }

    public BlogException(int code, String errorCode, String errorMessage, Throwable error) {
        super(errorMessage, error);
        this.code = code;
        this.blogExceptionDTO = new BlogExceptionDTO(code, errorMessage);
    }

    public BlogExceptionDTO getBlogExceptionDTO() {
        return blogExceptionDTO;
    }
    public int getCode(){
        return  code;
    }
}
