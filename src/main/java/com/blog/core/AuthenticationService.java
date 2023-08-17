package com.blog.core;

import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AuthenticationService implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role,@Nullable ContainerRequestContext requestContext) {

        return user.getRole().equals(role);
    }

}
