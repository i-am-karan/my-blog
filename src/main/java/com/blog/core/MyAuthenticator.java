package com.blog.core;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class MyAuthenticator implements Authenticator<BasicCredentials, User> {
    private final SessionFactory sessionFactory;

    public MyAuthenticator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try (Session session = sessionFactory.openSession()) {
            // Query the database for the user with the provided username
            User user = session
                    .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                // Return the authenticated user
                return Optional.of(user);
            }
        }

        // Authentication failed
        return Optional.empty();
    }
}
