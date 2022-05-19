package com.viseo.apph.dao;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

    public List<Photo> getUserPhotos(User user) {
        return em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)
                .setParameter("user", user)
                .getResultList();
    }

    public Photo getPhoto(long id) {
        return em.find(Photo.class, id);
    }

    public void deletePhoto(Photo photo) {
        em.remove(photo);
    }
}
