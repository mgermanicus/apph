package com.viseo.apph.dao;

import com.viseo.apph.domain.Tag;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InvalidObjectException;

@Repository
public class TagDAO {
    @PersistenceContext
    EntityManager em;

    public String createTag(Tag tag) throws InvalidObjectException {
        if (tag.getName() != null && tag.getUser() != null) {
            em.persist(tag);
            return tag.getName() + " créé";
        }
        throw new InvalidObjectException("Le nom du tag est obligatoir");
    }

    // TODO
    // getTagsByUserId(long userId)
}
