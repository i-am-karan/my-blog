package com.blog.auth;

import com.blog.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.Optional;

public class JwtSecurityContext implements SecurityContext {
    private final Principal principal;
    private final boolean secure;
    private final Authenticator<JwtCredentials, User> authenticator;

    public JwtSecurityContext(String username, boolean secure, Authenticator<JwtCredentials, User> authenticator) {
        this.principal = new PrincipalImpl(username);
        this.secure = secure;
        this.authenticator = authenticator;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        // Implement your role-checking logic here, if needed
        return true;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }


    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    public static JwtSecurityContext build(String token, Authenticator<JwtCredentials, User> authenticator) throws AuthenticationException {
        // Verify the token and retrieve the authenticated user
        JwtCredentials credentials = new JwtCredentials(token);
        Optional<User> user = authenticator.authenticate(credentials);

        if (user.isPresent()) {
            // Create a new JwtSecurityContext with the authenticated user's username
            return new JwtSecurityContext(user.get().getName(), true, authenticator);
        } else {
            throw new AuthenticationException("Authentication failed");
        }
    }
}
