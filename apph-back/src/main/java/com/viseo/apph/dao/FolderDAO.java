package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FolderDAO {
    @PersistenceContext
    EntityManager em;

    public List<Folder> getFolderByUser(long userId) {
        return em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)
                .setParameter("userId", userId).getResultList();
    }
}
