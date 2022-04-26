package com.viseo.apph.dao;

import com.viseo.apph.domain.Photo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PhotoDao {
    @PersistenceContext
    EntityManager em;

    public Photo addPhoto(Photo photo) {
        em.persist(photo);
        return photo;
    }
    public List<Photo> getUserByLogin(long idUser) throws NoResultException {
        return em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)
                .setParameter("idUser",idUser)
                .getResultList();
    }
}
