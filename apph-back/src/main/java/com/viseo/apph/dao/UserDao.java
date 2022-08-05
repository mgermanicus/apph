package com.viseo.apph.dao;

import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDao {
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

    public List<User> getUserList() {
        return em.createQuery("SELECT user FROM User user JOIN user.roles role where role.name = 'ROLE_USER'", User.class).getResultList();
    }

    public boolean existByLogin(String login) {
        Long count = em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)
                .setParameter("login", login)
                .getSingleResult();
        return !count.equals(0L);
    }
    public void resetPassword(String login, String newPassword){
        User user = getUserByLogin(login);
        user.setPassword(newPassword);
        em.flush();
    }
}
