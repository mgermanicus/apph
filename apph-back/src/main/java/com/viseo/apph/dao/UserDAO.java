package com.viseo.apph.dao;

import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {
    @PersistenceContext
    EntityManager em;

    public void createUser(User user) {
        em.persist(user);
    }

    public User getUser(long id) {
        return em.find(User.class, id);
    }

    public User getUserByLogin(String login) throws NoResultException {
        return em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }
}
