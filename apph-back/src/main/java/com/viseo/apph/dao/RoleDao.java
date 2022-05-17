package com.viseo.apph.dao;

import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RoleDao {
    @PersistenceContext
    EntityManager em;

    public Role getRole(ERole eRole){
        return em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class)
                .setParameter("name", eRole)
                .getSingleResult();
    }
}
