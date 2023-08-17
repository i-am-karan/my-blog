package com.blog.auth;
public class JwtCredentials {
    private String token;

    public JwtCredentials(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

