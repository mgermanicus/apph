package com.viseo.apph.dao;

import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {
    @PersistenceContext
    EntityManager em;

    public void createUser(User user)
    {
        em.persist(user);
    }
    public void deleteUser(long id)
    {
        User user = em.find(User.class,id);
        em.remove(user);
    }


}
