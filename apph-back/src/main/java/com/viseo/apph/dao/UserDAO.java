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

    public User getUserByLogin(String login) throws NoResultException {
        return em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public User getUserById(long userId) {
        return em.find(User.class, userId);
    }

    public void editPassword(long userId, String newPassword) {
        User user = em.find(User.class, userId);
        user.setPassword(newPassword);
    }

    public void editLogin(long userId, String newLogin) {
        User user = em.find(User.class, userId);
        user.setLogin(newLogin);
    }

    public void editFirstname(long userId, String newFirstname) {
        User user = em.find(User.class, userId);
        user.setFirstname(newFirstname);
    }

    public void editLastname(long userId, String newLastname) {
        User user = em.find(User.class, userId);
        user.setLastname(newLastname);
    }

    public boolean existByLogin(String login) {
        Long count = em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)
                .setParameter("login", login)
                .getSingleResult();
        return !count.equals(0L);
    }
}