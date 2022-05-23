package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
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

    public List<Photo> getPhotosByFolder(Folder folder) {
        return em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)
                .setParameter("folder", folder)
                .getResultList();
    }

    public boolean existNameInFolder(Folder folder, String title) {
        Long count = em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title", Long.class)
                .setParameter("folder", folder)
                .setParameter("title", title)
                .getSingleResult();
        return !count.equals(0L);
    }

    public List<Photo> getUserFilteredPhotos(User user, String filterQuery) {
        return em.createQuery(filterQuery, Photo.class)
                .setParameter("user", user)
                .getResultList();
    }
}
