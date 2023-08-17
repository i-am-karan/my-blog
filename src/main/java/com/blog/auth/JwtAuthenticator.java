package com.blog.auth;

import com.blog.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<JwtCredentials, User> {

    private final JwtService jwtService;

    public JwtAuthenticator(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Optional<User> authenticate(JwtCredentials credentials) throws AuthenticationException {
        String token = credentials.getToken();

        // Verify and decode the JWT token
        Optional<User> user = jwtService.verifyToken(token);

        if (user.isPresent()) {
            // Return the authenticated user
            return user;
        }

        // Authentication failed
        return Optional.empty();
    }
}
