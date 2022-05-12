package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FolderDao {
    @PersistenceContext
    EntityManager em;

    public Folder getFolderById(long id) {
        return em.find(Folder.class, id);
    }

    public List<Folder> getFolderByUser(long userId) {
        return em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)
                .setParameter("userId", userId).getResultList();
    }

    public void createFolder(Folder folder) {
        em.persist(folder);
    }
}
