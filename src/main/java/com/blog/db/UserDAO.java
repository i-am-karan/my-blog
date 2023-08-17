package com.blog.db;

import com.blog.core.BlogPost;
import com.blog.core.User;
import com.blog.db.BlogPostDAO;
import io.dropwizard.hibernate.AbstractDAO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {
    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public List<User> findAll() {
        return list(namedTypedQuery("com.blog.core.User.findAll"));
    }
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(uniqueResult(namedTypedQuery("com.blog.core.User.findByUsername")
                .setParameter("username", username)));
    }


    public User create(User user) {
        return persist(user);
    }

    public void delete(User user) {
        currentSession().delete(user);
    }
    public void deleteUser(String username) {
        Optional<User> optionalUser = findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Delete the user's blog posts
            deleteBlogPostsByUser(user);

            // Delete the user
            delete(user);
        }
    }

    private void deleteBlogPostsByUser(User user) {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaDelete<BlogPost> delete = builder.createCriteriaDelete(BlogPost.class);
        Root<BlogPost> root = delete.from(BlogPost.class);
        delete.where(builder.equal(root.get("username"), user.getName()));
        currentSession().createQuery(delete).executeUpdate();
    }
}
