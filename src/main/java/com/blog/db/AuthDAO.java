package com.blog.db;
import com.blog.core.Auth;
import io.dropwizard.hibernate.AbstractDAO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class AuthDAO extends AbstractDAO<Auth> {

    public AuthDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public Optional<Auth> findByUsername(String username) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Auth> criteriaQuery = criteriaBuilder.createQuery(Auth.class);
        Root<Auth> root = criteriaQuery.from(Auth.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("username"), username));
        return Optional.ofNullable(uniqueResult(criteriaQuery));
    }
    public Auth create(Auth auth) {
      return  persist(auth);
    }


    public void update(Auth auth) {
        currentSession().merge(auth);
    }
    public void createOrUpdate(Auth auth) {
        Optional<Auth> existingAuth = findByUsername(auth.getUsername());
        if (!existingAuth.isEmpty()) {
            Auth auth1 = existingAuth.get();
            auth1.setToken(auth.getToken()); // Explicit cast to Auth
            update(auth1);
        } else {
            create(auth);
        }
    }

    public void delete(Auth auth) {
        currentSession().delete(auth);
    }
    public void deleteByUsername(String username) {
        Optional<Auth> authOptional = findByUsername(username);
        authOptional.ifPresent(auth -> delete((Auth) auth));
    }
}
