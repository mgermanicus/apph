package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FilterRequest;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Queue;

@Repository
public class PhotoDao {
    @PersistenceContext
    EntityManager em;

    public Photo addPhoto(Photo photo) {
        em.persist(photo);
        return photo;
    }

    public List<Photo> getUserPhotos(User user, String filterQuery, Queue<String> argQueue) {
        Query spSQLQuery = em.createQuery(filterQuery, Photo.class).setParameter("user", user);
        for (int i = 1; !argQueue.isEmpty(); i++) {
            spSQLQuery.setParameter(i, argQueue.poll());
        }
        return spSQLQuery.getResultList();
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

    public boolean existNameInFolder(Folder folder, String title, String format) {
        Long count = em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)
                .setParameter("folder", folder)
                .setParameter("title", title)
                .setParameter("format", format)
                .getSingleResult();
        return !count.equals(0L);
    }

    public List<Photo> getPhotosByUserAndIds(List<Long> ids, User user) {
        return em.createQuery("SELECT photo FROM Photo photo WHERE photo.id IN :ids AND photo.user = :user", Photo.class)
                .setParameter("ids", ids)
                .setParameter("user", user)
                .getResultList();
    }

    public SearchResult<Photo> searchPhotoByTargetAndUser(FilterRequest filterRequest, User user) {
        SearchSession searchSession = Search.session(em);
        SearchScope<Photo> scope = searchSession.scope(Photo.class);
        return searchSession.search(Photo.class)
                .where(scope
                        .predicate()
                        .bool()
                        .must(scope.predicate().match().field("user.login")
                                .matching(user.getLogin()))
                        .must(scope.predicate().match()
                                .fields("title", "description", "tags.name")
                                .matching(filterRequest.getTarget()))
                        .toPredicate()
                )
                .sort(SearchSortFactory::score)
                .fetch((filterRequest.getPage() - 1) * filterRequest.getPageSize(), filterRequest.getPageSize());
    }
}
