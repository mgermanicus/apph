package com.viseo.apph.dao;

import com.viseo.apph.domain.Photo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PhotoDao {
    @PersistenceContext
    EntityManager em;

    public Photo addPhoto(Photo photo){
        em.persist(photo);
        return photo;
    }
}
