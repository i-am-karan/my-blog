package com.blog;

import com.blog.Exception.BlogExceptionMapper;
import com.blog.api.*;
import com.blog.auth.JwtAuthFilter;
import com.blog.auth.JwtAuthenticator;
import com.blog.auth.JwtService;
import com.blog.core.*;
import com.blog.db.AuthDAO;
import com.blog.db.BlogPostDAO;
import com.blog.db.UserDAO;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
public class BlogApplication extends Application<BlogConfiguration> {
    public static void main(String[] args) throws Exception {
        new BlogApplication().run(args);
    }

    private final HibernateBundle<BlogConfiguration> hibernateBundle = new HibernateBundle<BlogConfiguration>(
           User.class,BlogPost.class,Auth.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(BlogConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<BlogConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }
    @Override
    public void run(BlogConfiguration configuration, Environment e) {
        // Create an instance of the User subclass
        String secretKey = configuration.getJwtSecretKey();
        long expirationTime = configuration.getJwtExpirationTime();
        final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        final JwtService jwtService = new JwtService(secretKey,expirationTime,userDAO);
        final BlogPostDAO blogPostDAO = new BlogPostDAO(hibernateBundle.getSessionFactory());
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator(jwtService);
        final AuthDAO authDAO=new AuthDAO(hibernateBundle.getSessionFactory());
        final BlogPostResource blogPostResource=new BlogPostResource(blogPostDAO);
        e.jersey().register(new BlogExceptionMapper());
        e.jersey().register(new UserResource(userDAO, jwtService,authDAO,blogPostResource));
        e.jersey().register(blogPostResource);
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        e.jersey().register(RolesAllowedDynamicFeature.class);
        e.jersey().register(new AuthDynamicFeature(new JwtAuthFilter(jwtAuthenticator, "realm")));
        e.jersey().register(RolesAllowedDynamicFeature.class);
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }
}
