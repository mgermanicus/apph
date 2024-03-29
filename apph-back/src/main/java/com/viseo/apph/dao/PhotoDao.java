package com.viseo.apph.dao;

import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FilterRequest;
import com.viseo.apph.security.AuthTokenFilter;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
public class PhotoDao {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @PersistenceContext
    EntityManager em;

    public Photo addPhoto(Photo photo) {
        em.persist(photo);
        return photo;
    }

    public List<Photo> getUserPhotos(User user, String filterQuery, Queue<Object> argQueue) {
        Query spSQLQuery = em.createQuery(filterQuery, Photo.class).setParameter("user", user);
        try {
            for (int i = 1; !argQueue.isEmpty(); i++) {
                spSQLQuery.setParameter(i, argQueue.poll());
            }
        } catch (IllegalArgumentException iae) {
            logger.error(iae.getMessage());
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

    public SearchResult<Photo> searchPhotoByTargetAndUser(FilterRequest filterRequest, User user, String defaultParams, List<String> tagsParams, Collection<Range<Float>> rangesParams) {
        SearchSession searchSession = Search.session(em);
        SearchScope<Photo> scope = searchSession.scope(Photo.class);
        return searchSession.search(Photo.class)
                .where(f -> f.bool(b -> {
                    b.must(f.match().field("user.id")
                            .matching(user.getId()));
                    b.must(scope.predicate().match()
                            .fields("title", "description", "tags.name", "address")
                            .matching(defaultParams));
                    for (String tag : tagsParams) {
                        b.should(f.match()
                                .field("tags.name")
                                .matching(tag));
                    }
                    for (Range<Float> range : rangesParams) {
                        b.should(f.range()
                                .field("size")
                                .range(range));
                    }
                }))
                .fetch((filterRequest.getPage() - 1) * filterRequest.getPageSize(), filterRequest.getPageSize());
    }

    public Map<String, Map<?, Long>> getSearchFacets(FilterRequest filterRequest, User user, Collection<Range<Float>> ranges) {
        SearchSession searchSession = Search.session(em);
        SearchScope<Photo> scope = searchSession.scope(Photo.class);
        AggregationKey<Map<String, Long>> countsByTagKey = AggregationKey.of("countsByTag");
        AggregationKey<Map<Range<Float>, Long>> countsBySize = AggregationKey.of("countsBySize");
        SearchResult<Photo> result = searchSession.search(scope)
                .where(scope
                        .predicate()
                        .bool()
                        .must(scope.predicate().match().field("user.id")
                                .matching(user.getId()))
                        .must(scope.predicate().match()
                                .fields("title", "description", "tags.name", "address")
                                .matching(filterRequest.getTarget()))
                        .toPredicate()
                )
                .aggregation(countsByTagKey, scope.aggregation().terms()
                        .field("tags.name_keyword", String.class)
                        .maxTermCount(5)
                        .toAggregation()
                )
                .aggregation(countsBySize, f -> f.range()
                        .field("size", Float.class)
                        .ranges(ranges))
                .fetchAll();
        Map<String, Map<?, Long>> facets = new HashMap<>();
        facets.put("tagFacet", result.aggregation(countsByTagKey));
        facets.put("sizeFacet", result.aggregation(countsBySize));
        return facets;
    }

    public List<Photo> getAllPhotos(User user) {
        return em.createQuery("SELECT photo FROM Photo photo WHERE photo.user = :user", Photo.class)
                .setParameter("user", user)
                .getResultList();
    }
}
