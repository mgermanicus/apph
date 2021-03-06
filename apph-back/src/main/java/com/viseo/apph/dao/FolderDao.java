package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FolderDao {
    @PersistenceContext
    EntityManager em;

    public Folder getFolderById(long id) {
        return em.find(Folder.class, id);
    }

    public void delete(Folder folder) {
        em.remove(folder);
    }

    public List<Folder> getFolderByUser(long userId) {
        return em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)
                .setParameter("userId", userId).getResultList();
    }

    public void createFolder(Folder folder) {
        em.persist(folder);
    }

    public Folder getParentFolderByUser(User user) throws NoResultException {
        return em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)
                .setParameter("user", user).getSingleResult();
    }

    public List<Folder> getFoldersByParentId(long parentId) {
        return em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)
                .setParameter("parentId", parentId).getResultList();
    }

    public Map<Long, Long> getFolderParentChildStructureByUser(User user) {
        return em.createQuery("SELECT folder.id as childId, folder.parentFolderId as parentId from Folder folder WHERE folder.user = :user", Tuple.class)
                .setParameter("user", user).getResultStream().collect(Collectors.toMap(
                        tuple -> (Long) tuple.get("childId"),
                        tuple -> tuple.get("parentId") != null ? (Long) tuple.get("parentId") : -1
                ));
    }
}
