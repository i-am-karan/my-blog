package com.blog.auth;

import com.blog.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

    private final Authenticator<JwtCredentials, User> authenticator;
    private final String realm;

    public JwtAuthFilter(Authenticator<JwtCredentials, User> authenticator, String realm) {
        this.authenticator = authenticator;
        this.realm = realm;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Extract the JWT token from the request headers
        String token = extractToken(requestContext);

        // Create the JwtCredentials object
        JwtCredentials credentials = new JwtCredentials(token);

        // Authenticate the credentials using the authenticator
        User user = null;
        try {
            user = authenticator.authenticate(credentials).orElse(null);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        // Check if authentication succeeded
        if (user == null) {
            // Authentication failed, send 401 Unauthorized response
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Bearer realm=\"" + realm + "\"")
                    .build());
        } else {
            // Authentication succeeded, set the authenticated user in the security context
            try {
                requestContext.setSecurityContext(JwtSecurityContext.build(token, authenticator));
            } catch (AuthenticationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String extractToken(ContainerRequestContext requestContext) {
        // Extract the token from the Authorization header
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return null;
    }
}
