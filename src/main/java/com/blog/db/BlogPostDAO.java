package com.blog.db;

import com.blog.core.BlogPost;

import java.util.List;

import io.dropwizard.hibernate.AbstractDAO;
import jakarta.persistence.criteria.*;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class BlogPostDAO extends AbstractDAO<BlogPost> {
    public BlogPostDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public List<BlogPost> findAll() {
        return list(namedTypedQuery("com.blog.core.BlogPost.findAll"));
    }
    public List<BlogPost> findAllByUsername(String username) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaQuery<BlogPost> query = criteriaBuilder.createQuery(BlogPost.class);
        Root<BlogPost> root = query.from(BlogPost.class);
        Predicate predicate = criteriaBuilder.equal(root.get("username"), username);
        query.select(root).where(predicate);
        return list(query);
    }
    public Optional<BlogPost> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public BlogPost create(BlogPost blogPost) {
        return persist(blogPost);
    }
    public BlogPost update(BlogPost blogPost) {
        return persist(blogPost);
    }
    public void delete(BlogPost blogPost) {
        currentSession().delete(blogPost);
    }
    public void deleteAllByUsername(String username) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaDelete<BlogPost> deleteQuery = criteriaBuilder.createCriteriaDelete(BlogPost.class);
        Root<BlogPost> root = deleteQuery.from(BlogPost.class);
        deleteQuery.where(criteriaBuilder.equal(root.get("username"), username));
        currentSession().createQuery(deleteQuery).executeUpdate();
    }
}